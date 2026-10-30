---
title: "告警四字段去冗余范围拍板：不动存量、tier保留现状"
usage_scenario:
    - "界定告警字段去冗余改造的实施范围"
    - "有人提议删level/tier/alarm_type列时"
    - "判断双读回退分支能否清理"
keywords:
    - "不回填旧数据"
    - "双读回退长期保留"
    - "tier保留现状"
    - "四列均不删"
    - "可删冗余仅剩三处"
source: "auto"
---

针对 eiot_alert_record 四个告警字段（level / alarm_kind / alarm_type / tier）的"去冗余"，用户 2026-10 拍板两项：

① 去冗余范围 = **不管旧数据**：不回填 alarm_kind IS NULL 的 14,554 条存量（占生产库 84.8%），读侧双读回退分支（mapLevel / mapLevelToAlarmType 及 AlertRecordMapper 中 8 处 COALESCE(alarm_kind, CAST(level AS UNSIGNED))）长期保留，不作为本次清理对象。
② tier 处置 = **保留现状**：既不冻结写入也不删列，大屏"档位分布"环图（countByTierInRange + AlarmAnalysis.vue）与移动端 alarm-detail 的 TIER_MAP 继续消费；Ⅰ-Ⅳ 判定链等部件类型/视频覆盖原料就位后再启用。

由此结论：**四列一列都不删**。level 虽在新数据里是 deriveLevel(alarm_kind) 的纯派生值（实测 2,609/2,609 吻合、全仓无提级改写），但排序/索引/筛选要用且未来提级需独立列；alarm_type 是摄像头事件来源的唯一载体；tier 有实际消费点。真正能去掉的冗余只剩三项：FireAlarmOrderServiceImpl 与 FireDashboardServiceImpl 各自重复实现的一套 mapLevel/mapLevelNew/mapLevelToAlarmType/mapAlarmKindToAlarmType（收敛到 AlarmSemantics）、W5 靠 DB NOT NULL DEFAULT 0 隐式落 alarm_type 的写法、以及 level 与 alarm_type 两处失真 DB 注释。

Rejected alternatives: 回填存量后删回退分支（用户否，代价与风险不抵收益）；删 tier 列并下线前端消费点（用户否）；level 不落库改查询派生（不可行，破坏排序索引与存量）。

Applicable / expiry conditions: 在"新旧语义混存、旧数据不回填"的前提下持续有效；一旦后续决定做全量回填或 tier 判定原料齐备启用 Ⅰ-Ⅳ，本决策需重新评估。适用于告警字段模型演进与相关重构范围界定。
