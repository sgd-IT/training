---
title: "eiot_product product_key 补索引与写侧成本判定落地"
usage_scenario:
    - "为关联列补建二级索引并验证执行计划恢复"
    - "评估索引写侧成本与低区分度列取舍"
    - "跨MySQL版本环境做DDL验证与回滚"
keywords:
    - "product_key索引"
    - "Block Nested Loop"
    - "写侧成本判定"
    - "EXPLAIN计划验收"
    - "在线DDL回滚"
source: "auto"
---

## Task description
- Core requirement: 为 eiot_product.product_key 新增二级索引以修复告警/设备分页的计划退化；同时把"写侧成本判定"补进 sql-standards skill；随后按用户要求核查线上库并在测试库执行验证。
- Task background: product_key 无索引导致关联退化为 Block Nested Loop，连带使驱动表 eiot_alert_record（生产 13,705 行）的 ORDER BY id DESC LIMIT 失去 PRIMARY 索引顺序，变成全表扫 + Using temporary + filesort，实测 2517ms vs 加索引后 14ms 级。

## Execution process
1. SKILL.md 索引节新增「新增索引前必须过一遍写侧成本」：6 条判据表（读写比/写操作类型/列可变性/索引数边际/能否复用覆盖/能否替代）+ 3 条反向事实（写频繁表更需索引管住锁粒度、贵的是随机不是频次、唯一约束不可用性能理由砍）+ 5 条替代手段 + 口诀；反例速查补两行。
2. checklist.md 第三层新增 3.9（写侧成本）、3.10（更便宜的替代）、3.11（P0：缺索引是否在拖死别的表的执行计划）。
3. 否决"列表摘掉 details 字段"的优化项：Grep 前端确认 alarm-record/index.vue 从 item.details 解析设备名称与位置并传给编辑弹窗，摘字段会导致列表设备名/位置全空。
4. 生产库（anwei-fire-remote-monitor-prod，5.6.28-cdb2016-log）只读核查：eiot_product 仅有 PRIMARY(id)；25 行 / distinct product_key=25 / 空值 0 / 逻辑删除 0 / key 长度 8~19；InnoDB + ROW_FORMAT=Dynamic + 数据 16KB；MCP 配置 ALLOW_DDL_OPERATION=true、INSERT/UPDATE/DELETE=false。
5. 落盘迁移脚本 _sql/mysql/V20261029__eiot_product_add_product_key_index.sql：information_schema.STATISTICS + PREPARE 幂等守卫（5.6/5.7 均无 ADD INDEX IF NOT EXISTS），头部写明根因、影响面、回滚语句，末尾保留三条验证 EXPLAIN。
6. 测试库（anwei_fire_remote_monitor，5.7.38）执行 ALTER TABLE eiot_product ADD KEY idx_product_key (product_key), ALGORITHM=INPLACE, LOCK=NONE 成功（523ms）；SHOW INDEX 确认 Non_unique=1、Cardinality=18。
7. 测试库 EXPLAIN 验收：告警分页 t 由 ALL rows=13705+temporary+filesort 变为 type=index/key=PRIMARY/rows=20/Extra 仅 Using where，t2 变 ref/key=idx_product_key/rows=1；设备分页 t 变 index/key=PRIMARY，t1 变 ref/idx_product_key；实际查询 18.85ms。
8. 生产库保持未改动，等待用户单独授权执行；建议用最简 ALTER 语句绕开 MCP 解析器对 ALGORITHM/LOCK 与 PREPARE 的兼容性问题。

## Related files
- C:\Users\Administrator\.qoder\skills\sql-standards\SKILL.md
- C:\Users\Administrator\.qoder\skills\sql-standards\checklist.md
- d:\code\remote-monitor\anwei-fire-remote-monitor\_sql\mysql\V20261029__eiot_product_add_product_key_index.sql

## Notes
- 测试库通过不等于生产必然通过：prod=5.6.28 与 test=5.7.38 优化器不同，生产执行后必须重跑同一组 EXPLAIN。
- 验收一律看执行计划（type/key/rows/Extra）而非毫秒数，同一 SQL 冷热态可差数十倍。
- 明确不做：重建 idx_tenant_alert_time 列序（此前实测已否决）、建 uk_product_key（逻辑删除下会被挡，属正确性议题）、调小 pageSize 或给 name 加前导模糊索引。
- 截至本轮结束，改动 1 的生产执行、SQL 文件的 git 提交、踩坑手册记录三项均未获授权，处于待确认状态。

## Task overview
部分完成：skill 两处增补与幂等迁移脚本已落盘；测试库索引已创建且两条分页执行计划完全恢复（临时表与 filesort 消失）；生产库经完整前置核查判定风险极低但刻意保持未动，等待用户明确授权后再执行同一条 DDL 并按同一标准复核计划。
