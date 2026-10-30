---
title: "告警分页优化取舍：不改跨模块API、不重建索引列序"
usage_scenario:
    - "决定批量取名是改跨模块接口还是本地去重"
    - "评估是否值得重建已有复合索引的列序"
    - "为区间聚合类慢SQL判断能否新增索引"
keywords:
    - "跨模块API改动成本"
    - "租户名去重后逐个查询"
    - "索引列序不动仅限分页链路"
    - "覆盖索引例外"
    - "ADD INDEX在线无锁"
    - "告警分页性能取舍"
source: "auto"
---

Conclusion: 告警工单分页链路（FireAlarmOrderServiceImpl.doPageFromAlertRecord）的性能优化只做应用层批量回填，两处不做——(1) 不给 TenantApi 新增批量取租户名的接口，改为在本页范围内对 tenantId 去重后逐个调用现有 getTenantName；(2) 不重建 eiot_alert_record 索引列序，保留现有 idx_tenant_alert_time (tenant_id, alert_time, deleted)。

Rationale (trade-off): 一页 20 条经 distinct 后通常只剩 1~2 个租户，去重后逐个调用的开销已可忽略，而改跨模块 Api 要动 module-system 的接口契约与所有调用方，收益远小于成本；索引侧 EXPLAIN 已显示 type=ref 且 Extra 无 Using filesort（alert_time 是索引第二列，排序由索引天然有序承担），deleted 位于末位只是过滤顺序问题而非排序缺陷，加上该表 deleted=0 接近全量、前置 deleted 几乎不减扫描行，而行数偏大源于单租户占九成的数据倾斜，重排列序解决不了；生产库 ALTER 索引有锁表风险，不值得换极小收益。

Rejected alternatives: 给 TenantApi 加 selectByIds 批量方法（本身合理，但跨模块改动面大、收益仅省 1 次查询）；DROP 旧索引改建 (tenant_id, deleted, alert_time)（首轮曾建议，后续实测推翻）。

Applicable / expiry conditions: 前者在一页内租户数保持个位数、且跨模块 API 变更需协调成本高时成立；若出现系统租户一次翻页覆盖大量租户的场景（去重后 ID 数量上升到几十），应重新评估加批量接口。后者在 deleted=1 脏数据比例明显上升、或出现带时间范围/多条件组合的慢查询时必须重新评估列序。

## Notes
- 【本条边界（2026-10-29 实测触发，勿越界套用）】"不动索引"只约束**分页链路的既有列序重建**，不等于"这张表不再需要任何新索引"。本轮数据中心 5 条区间聚合（countByDayInRange / countByAlarmKindInRange / countByTierInRange / 按小时 / 按租户排名）全部命中 `(deleted=0 OR deleted IS NULL) + alert_time 区间 + 逐行 COALESCE/FROM_UNIXTIME` 形态，系统租户视图无 tenant_id 等值 → 唯一索引首列失效 → type=ALL 全表扫；在测试库新建覆盖索引 `idx_alert_cover (alert_time, deleted, alarm_kind, level, read_flg, tier, tenant_id)` 后 5 条统一变为 type=range + Using index（不回表），trend 68.8→17.4ms、type-distribution 45.2→7.8ms。引用本条拒绝索引动作前，先确认待优化的是分页链路还是区间聚合。
- 【"生产 DDL 锁表风险"需按动作类别核算】该理由在 MySQL 5.6/5.7 下对**新增二级索引**并不成立：ADD INDEX 支持 ALGORITHM=INPLACE, LOCK=NONE（测试库 33k 行实测 2.58s 完成）。真正有风险的是 DROP/重建列序与改列类型，不是加一条索引。
- 【索引设计上的有意偏离】上述覆盖索引以 alert_time 打头违反"等值在前、范围在后"惯例，是因为查询里缺失唯一的等价条件（系统租户无 tenant_id），alert_time 是唯一可用的定位列，其余 6 列仅作覆盖过滤；照搬通用列序规范反而会失效。
