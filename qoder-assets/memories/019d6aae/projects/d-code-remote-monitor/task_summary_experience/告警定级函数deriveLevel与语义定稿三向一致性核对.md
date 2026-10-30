---
title: "告警定级函数deriveLevel与语义定稿三向一致性核对"
usage_scenario:
    - "核对告警kind与level映射是否与定稿一致"
    - "排查switch跨维度数字错位是否为真bug"
    - "评审default兜底分支的实际可达性"
keywords:
    - "deriveLevel核对"
    - "跨维度正交映射"
    - "生产库四维实测分布"
    - "level纯函数无提级"
    - "去冗余范围拍板"
source: "auto"
---

## Task description
- Core requirement: 用户贴出 AlarmSemantics.deriveLevel(Integer kind) 与 KIND_* 常量定义，要求"对误差"——核对代码、常量注释、《消防告警字段语义定稿》三者之间是否存在映射偏差。
- Task background: deriveLevel 是写侧改造第 3 步新建的定级层，kind 是无序分类（1火警/2故障/3隐患/4离线/5误报/6信息），level 是有序等级（1最急），两维数字天然不对齐，易被误读为 bug。

## Execution process
1. 读取 AlarmSemantics.java 全文与 d:\code\remote-monitor\消防告警字段语义定稿.md，逐值比对：KIND_FIRE(1)→"1"、KIND_RISK(3)→"2"、KIND_FAULT(2)/KIND_OFFLINE(4)/KIND_FALSE_ALARM(5)/KIND_INFO(6) 落入 default→"3"，与定稿 1.2/3.3 拍板口径一致。
2. grep 全仓 deriveLevel|KIND_FIRE|KIND_RISK|KIND_FAULT，确认五个写入点（AlarmRecordService、DeviceAlertConfigServiceImpl、FireAlarmOrderServiceImpl、AlertConfigServiceImpl、AlertMessageEventListener）均统一经 deriveLevel 派生，无第二套映射。
3. 与定稿 3.3 ③ 存量迁移 SQL 逐格对照 CASE alarm_kind WHEN 1 THEN '1' WHEN 2 THEN '3' WHEN 3 THEN '2' ... ：Java 的 default 覆盖 {2,4,5,6}→"3" 与 SQL 逐值展开结果相同，写侧与迁移侧口径对齐。
4. 边界核验：default 会兜住未知 kind（0/7）而 SQL CASE 返回 NULL；但实际链路 kind 均由归一函数产出、无法识别时返回 null 不调用 deriveLevel，故不可达，无实际偏差。
5. 附带核对 defaultTierForKind（kind==KIND_FIRE ? TIER_IV : null）与定稿不变量 1（tier 有值 ⇔ level=1）等价。
6. 结论：三处对照全部一致，无需改动；显式拆分 case 替代 default 属零收益改动，建议维持原样。

## Related files
- /module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/alarm/AlarmSemantics.java
- /消防告警字段语义定稿.md

## Notes
- 核对跨维度映射只能对语义不能对数字（禁止跨维度比较）：KIND_RISK=3→level"2"、KIND_FAULT=2→level"3" 看似错位实为设计使然，这是本类核对最容易产生的"视觉误差"来源。
- 判断 default 兜底是否有风险，须回查上游归一函数是否可能产出未知值，不能只看函数本身。

## Notes
- 【生产库最新四维实测（2026-10，anwei-fire-remote-monitor-prod MCP 只读查询）】本记忆正文引用的 31,563 条是改造前快照；当前生产库 eiot_alert_record 共 **17,163** 条，其中新语义（alarm_kind 非空）**2,609 条（15.2%）**、旧语义（alarm_kind IS NULL）**14,554 条（84.8%）**，新语义自 2026-10-14 起产出。新数据交叉分布：kind=2/level=3/tier=NULL/type=0 共 2,130（主机故障配置为主）；kind=1/level=1/tier=4/type=0 共 278（主机火警配置）；kind=3/level=2/tier=NULL/type=4 共 178（离岗）；kind=1/level=1/tier=3/type=3 共 21（烟火）；kind=1/level=1/tier=3/type=2 共 2（热成像）。旧数据主力：level=2/type=0 6,241、level=1/type=4 5,216、level=1/type=0 2,329、level=1/type=3 698、level=3/type=0 40、level=1/type=5 30。
- 【level 纯函数性已实证 + 全库无提级】新数据 2,609/2,609 条满足 level == deriveLevel(alarm_kind)，无一例外；全仓 grep 提级/escalate/setLevel 未发现任何"只升不降"运行时改写，即 level 目前确实是 alarm_kind 的纯派生列（可删可留的判据依据此条，勿凭印象断言存在提级逻辑）。
- 【去冗余范围已由用户拍板】针对"类型/等级/档位去冗余"，用户选择：不回填 14,554 条旧数据（双读回退分支长期保留）、tier 保留现状不冻结不删列。因此去冗余可行面收敛为：合并 FireAlarmOrderServiceImpl 与 FireDashboardServiceImpl 各自重复的 mapLevel/mapLevelNew/mapLevelToAlarmType/mapAlarmKindToAlarmType 私有映射到 AlarmSemantics 一处、消除 W5 对 DB 默认值的隐式依赖、校正 level 与 alarm_type 两处失真注释；四列一列都不删。

## Task overview
已完成：纯核对任务（只读+grep，无任何代码修改），结论为 deriveLevel、KIND_* 常量、定稿文档、迁移 SQL 四方一致，无误差，不需要改动；同时识别并解释了表面反直觉的成因与不可达的边界差异。
