---
title: "告警今日判据改毫秒区间与Druid慢SQL统计判读"
usage_scenario:
    - "排查今日类统计列表在当天无数据时变慢"
    - "判读Druid慢SQL统计并确定修复验收方式"
    - "改动共用SQL片段常量后做等价性与计划回归"
keywords:
    - "TODAY_CONDITION_SQL"
    - "Druid慢SQL统计"
    - "全索引扫描"
    - "毫秒区间判据"
    - "等价性验证"
    - "PROCESSLIST连接数"
source: "auto"
---

## Task description
- Core requirement: 判读用户贴出的两条 Druid SQL 统计（LIMIT ? 1263ms、LIMIT ?, ? 560ms、以及大屏今日未闭环告警 429ms），并修复测试环境中暴露的"今日"判据走全索引扫描问题。
- Task background: eiot_alert_record（测试库 33,460 行）上 AlertRecordMapper.TODAY_CONDITION_SQL 用 DATE(FROM_UNIXTIME(alert_time/1000)) = CURDATE() 在列上套函数；idx_alert_cover 以 alert_time 打头但不含 handle_status；同一批次此前已为 eiot_product.product_key 补索引并在测试库验证。

## Execution process
1. 判读 Druid 统计行：时间分布数组第 4 档对应 100~1000ms 分桶、行数分布第 1 档对应极少量返回，得出"560ms 只交付一页"；确认 LIMIT ? 与 LIMIT ?, ? 是同一条 Mapper 在第 1 页与翻页时生成的两条独立统计项，另有 _mpCount 的 COUNT 是第三条统计。
2. 生产库复核 SHOW INDEX FROM eiot_product 仍只有 PRIMARY(id)，EXPLAIN ... LIMIT 1000,20 计划仍为 ALL 13705 + Using temporary + Using filesort + t2 Block Nested Loop，证明 offset 对退化计划无改善。
3. 用户澄清统计来自测试环境后，先纠正此前"560ms 发生在生产"的错误定性，把证据按来源分类（Druid=测试应用侧历史值、2517ms=生产直连实测、18.85ms=测试库加索引后实测）。
4. 用 information_schema.PROCESSLIST 按 client_host 聚合连接数，确认有 17 条连接挂在 anwei_fire_remote_monitor，排除"索引加到另一个副本"的可能。
5. 明确 Druid 统计为累积型：ExecuteCount 只增不减、MaxTimespan 取历史最大值，加索引不会刷掉历史值，必须重置该条统计或重启后端后再判断修没修好。
6. 定位 selectTodayUnclosedAlerts（AlertRecordMapper.java）慢因：EXPLAIN 显示 type=index/key=idx_alert_cover/rows=20 看似正常，但 rows=20 只是 LIMIT 估算值；测试库今日 0 行使提前终止彻底失效，须读完全部索引；CASE handle_status 表达式无法下推且该列不在覆盖索引内，导致逐行回表。
7. 修改 AlertRecordMapper.java 的 TODAY_CONDITION_SQL 常量为毫秒左闭右开区间（UNIX_TIMESTAMP(CURDATE())*1000 至 UNIX_TIMESTAMP(CURDATE()+INTERVAL 1 DAY)*1000），基准仍取数据库 CURDATE 不引入 Java 时间。
8. 等价性验证：全表按日分组比对两套判据得 mismatch_days=0；单日抽样昨天 3/3、今天 0/0。
9. 逐个 EXPLAIN 回归 4 处引用（Mapper 三处 + FireAlarmOrderServiceImpl 的 todayOnly），type 由 index/ALL 变 range、key_len=9、rows=1，其中按报警类型计数出现 Using index；实测列表由 175.83ms 降至 0.99ms。
10. GetProblems 核验改动文件无新增错误（既有 27 条 null-safety warning 位于 L55~L77，与本次无关）；完整 Maven 构建未跑。

## Related files
- /anwei-fire-remote-monitor/module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/dal/mysql/AlertRecordMapper.java
- /anwei-fire-remote-monitor/_sql/mysql/V20261029__eiot_product_add_product_key_index.sql

## Notes
- 首轮把用户提供的 Druid 统计默认当作生产数据展开结论，被用户纠正为测试环境；判读外部监控统计前必须先确认数据来源环境，不能凭查询对象推断。
- CASE handle_status 的等价改写应为 (handle_status IS NULL OR handle_status NOT IN (3,4))，不可照抄统计侧的 handle_status < 3：现数据只有 0~3 故等价，一旦出现第 5 态两者语义分叉。本轮按用户选择只改常量，该项未落地。
- 简化项 (deleted = 0 OR deleted IS NULL) 与 SELECT a.*（含 details TEXT）本轮均未动，避免超范围。
- 截至本轮结束：AlertRecordMapper.java 处于工作区 M 状态未提交；V20261029 SQL 脚本未提交；生产库 idx_product_key 仍未执行；踩坑手册记录未落盘。

## Task overview
部分完成：测试环境的"今日"判据已由列上套函数改为毫秒区间，4 处调用点执行计划全部转为 range 并通过全表逐日等价性验证，列表实测 175.83ms→0.99ms；同时澄清了 Druid 累积统计需重置才能验收、offset 分页无法救退化计划两点。生产库 DDL、git 提交与 push、work-log 落盘均待用户单独授权。
