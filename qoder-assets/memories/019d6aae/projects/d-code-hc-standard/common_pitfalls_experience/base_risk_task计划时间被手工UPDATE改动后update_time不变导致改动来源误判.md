---
title: "base_risk_task计划时间被手工UPDATE改动后update_time不变导致改动来源误判"
usage_scenario:
    - "核对派送锚点是否已被人工调整过"
    - "排查任务字段变更却查不到操作痕迹"
    - "手工SQL对齐周期任务派送日"
keywords:
    - "update_time不变"
    - "手工UPDATE对齐锚点"
    - "task_next_time改动溯源"
    - "base_risk_task审计"
source: "auto"
---

Bug class: 周期任务的计划时间字段被改动后无法追溯改动来源，误判为代码逻辑失效。

Root cause: hc-standard 的 base_risk_task 走 MyBatis 更新（界面「排查任务」编辑、启停联动 updateStatusByUnitId）时都会刷新 update_time/update_by；而直接用 SQL UPDATE 修改 task_next_time / task_start_time 不经过实体映射，update_time 保持原值。实测 id=633c630caf534232be97fe24f2c9608d（瞿彬,彭亨清，天7）的 task_next_time 已从 2026-10-24 变为 2026-10-23，而 update_time 仍是 2025-04-16 17:19:16、update_by=system11，即可断定是手工改库而非界面操作或代码上线所致。

Fix pattern: 核对派送锚点是否被改动时，把 task_next_time/task_start_time 的值变化与该行的 update_time 是否同步前移做联合判读——值变了但 update_time 没变 = 直接改库；两者同时变 = 走了业务更新入口。存量数据对齐固定星期用单条 UPDATE 同时写 task_start_time 与 task_next_time 到目标日 00:00:00，并加 check_cycle_unit/check_cycle/status/deleted 作为 WHERE 附加条件防误改同表其他行。

Reusable lesson: Don't 因为 update_time 是旧值就断言"没人改过这条任务"，因为手工 SQL 改库不会触碰审计字段，会把已生效的数据对齐误判成未处理；must 以计划字段实际取值为准、用 update_time 只判断改动路径。适用于本表所有"值与审计字段可能不一致"的排查场景；不适用于 insert 新建的行（create_time 与 create_by 由框架填充必然一致）。另注意：改完若不再触发单元停用→启用，新逻辑不会重排日期，因此手工对齐是一次性且持久的。
