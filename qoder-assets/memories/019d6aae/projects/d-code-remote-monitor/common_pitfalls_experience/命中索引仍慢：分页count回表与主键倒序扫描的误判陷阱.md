---
title: "命中索引仍慢：分页count回表与主键倒序扫描的误判陷阱"
usage_scenario:
    - "EXPLAIN显示命中索引但接口耗时仍达秒级"
    - "定位分页接口列表与count各自的耗时占比"
    - "解读Druid慢SQL的MaxTimespan与冷热态差异"
keywords:
    - "filesort"
    - "Block Nested Loop"
    - "驱动表ORDER BY顺序"
    - "二级索引补建"
    - "EXPLAIN计划验收"
    - "冷热态耗时不可比"
    - "Druid累积统计"
    - "Handler_read_next"
source: "auto"
---

Bug class: 「EXPLAIN 显示命中索引但查询仍然慢」的误判（分页接口只 EXPLAIN 列表 SQL）

Root cause: 两个可核验机制。①MyBatis-Plus PaginationInnerInterceptor 每次翻页发两条 SQL，列表走 ORDER BY id DESC + LIMIT 时优化器选 PRIMARY 反向扫描、读够 pageSize 行即停（type=index、rows=10，看起来"命中索引且极快"），而 COUNT(*) 无 LIMIT 且 deleted/tenant_id 不在二级索引内 → 非覆盖索引 → 全量回表，耗时占大头；只看列表那条会得出错误结论。②过滤列与排序列和索引顺序错位时，即使 key 有值也必须对整个 range 做 filesort，无法 early-exit。

Fix pattern: 先分离两条 SQL 各自计时；用对照实验归因——只 SELECT 索引列（验回表+行宽）、去掉 ORDER BY（验 filesort）、单条 SQL 耗时对比接口耗时（验 count）；硬指标取 SHOW SESSION STATUS LIKE 'Handler_read_next'（实际读行数，远大于 LIMIT 值即在白读）与 Sort_merge_passes（>0 表示排序落盘）；MySQL 8.0.18+ 用 EXPLAIN ANALYZE 拿 actual time/rows，5.7 用 SET profiling。

Reusable lesson: 不要因 EXPLAIN 的 key 非空就断定"用了预期索引且不慢"，因为 key 有值只说明用索引定位行、不代表只读了目标行，也不代表没有 filesort；必须以 rows / Extra（Using filesort、无 Using index 即回表）/ Handler_read_next 三者交叉判定，并对列表与 count 分别计时。适用于 MyBatis-Plus 分页及一切带自动 COUNT 的分页链路慢查询定位；不适用于无分页的单条 SQL（此时只需看 filesort 与回表两项）。

## Notes
- 【判据已二次验证（2026-10-29 生产/测试两库）】"Extra 里没有 Using index 就是在回表"在非分页场景同样成立：eiot_alert_record 上 `type=ref、key=idx_tenant_alert_time、rows=6852` 的区间聚合看似命中索引，但因 alarm_kind/level/read_flg/tier 不在索引内仍逐行回表读含 details 的宽行；建覆盖索引后 5 条统一出现 `Using index`，才真正消掉回表。
- 【新增判据：单次毫秒数不能跨冷热态比较】同一批 SQL 在暖态只要 40–70ms，而 Druid 记录的同一条是 1,096ms（ExecuteCount=1，即后端启动后首次执行）。所以"A/B 前后耗时"必须在同一缓存状态下测，且要连测多次看方差（本轮同一句实测 29.7→3.8ms）；否则会把冷启动读放大误判成稳态成本，也会把暖态噪声当成收益。判断收益优先看执行计划变化（type/key_len/rows/Extra），耗时只作量级佐证。
