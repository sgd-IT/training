---
title: "告警今日判据毫秒区间改造与Druid慢查询判读任务归档"
usage_scenario:
    - "回顾告警今日判据改造的完整过程"
    - "查找本轮遗留未授权的提交与生产DDL事项"
    - "复用慢查询判读到修复验收的任务流程"
keywords:
    - "Druid慢查询判读"
    - "今日判据毫秒区间"
    - "等价性验证"
    - "执行计划回归"
    - "待授权清单"
source: "auto"
---

## Task description
- Core requirement: 判读用户提供的 Druid SQL 慢查询统计，并修复测试环境告警"今日"判据导致的索引退化问题。
- Task background: 承接上一轮 eiot_product.product_key 补索引工作；本轮新增两条 Druid 统计（LIMIT ?, ? 560ms、大屏今日未闭环 429ms）需要归因。

## Execution process
1. 判读 Druid 统计行分档数组，得出"560ms 只交付一页"；确认 LIMIT ? 与 LIMIT ?, ? 是同一条 Mapper 第 1 页与翻页的两条独立统计项，另有 _mpCount 的 COUNT 为第三条。
2. 生产库复核 SHOW INDEX 与 EXPLAIN（含 LIMIT 1000,20），证明 offset 分页对退化计划无改善。
3. 用户澄清统计来自测试环境后，纠正"560ms 发生在生产"的错误定性，并按来源分类证据。
4. information_schema.PROCESSLIST 按 client_host 聚合连接数，确认索引落在应用真实使用的库。
5. 定位 selectTodayUnclosedAlerts 慢因：EXPLAIN 的 rows=20 只是 LIMIT 估算值，函数判据使索引退化为全索引扫描，CASE 过滤列不在索引内导致逐行回表。
6. 修改 AlertRecordMapper.TODAY_CONDITION_SQL 为毫秒左闭右开区间，基准仍取数据库 CURDATE。
7. 全表按日分组比对两套判据得 mismatch_days=0，单日抽样昨天 3/3、今天 0/0。
8. 逐个 EXPLAIN 回归 4 处引用，type 由 index/ALL 变 range、rows=1，列表实测 175.83ms→0.99ms；GetProblems 无新增错误。

## Related files
- /anwei-fire-remote-monitor/module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/dal/mysql/AlertRecordMapper.java

## Notes
- 与既有归档「告警今日判据改毫秒区间与Druid慢SQL统计判读」为同一任务的复盘记录，本条为其精简版；细节以该条为准。
- 截至本轮结束：AlertRecordMapper.java 处于工作区 M 状态未提交；V20261029 SQL 脚本未提交；生产库 idx_product_key 仍未执行。

## Task overview
部分完成：测试环境今日判据已改为毫秒区间并通过等价性与四处计划回归验证；生产 DDL、git 提交与 push、work-log 落盘均待用户单独授权。
