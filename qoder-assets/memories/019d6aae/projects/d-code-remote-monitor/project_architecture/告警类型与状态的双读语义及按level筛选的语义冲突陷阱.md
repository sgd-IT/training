---
title: "告警类型与状态的双读语义及按level筛选的语义冲突陷阱"
usage_scenario:
    - "为处置中心或告警列表新增筛选条件"
    - "排查告警类型与等级跨模块显示不一致"
    - "判断按level过滤是否命中混存期旧数据"
keywords:
    - "alarm_kind"
    - "level双读语义"
    - "mapAlarmLevelToRecordLevel"
    - "处置中心筛选入参"
    - "alarmLevel语义冲突"
    - "ORDER_STATUS_CASE_SQL纯handle_status五态"
source: "auto"
---

The project uses a dual-read semantic for alarm type classification: new data uses the `alarm_kind` field, while legacy data falls back to the `level` field via `COALESCE(alarm_kind, CAST(level AS UNSIGNED))`. The values are: 1=火警, 2=故障, 3=隐患, 4=离线, 5=误报, 6=信息. This is distinct from the `alarm_type` field, which encodes event source (e.g., 0=主机报警, 1=高温, 2=热成像, etc.) and is unrelated to filtering. Status derivation follows `ORDER_STATUS_CASE_SQL`, which prioritizes `handle_status`, then parses `details` for keywords like '关闭工单' or '确认真实火警', and finally checks `read_flg` for legacy data fallback.

## Notes
- 【状态判据已演进（2026-10-23 本批改造实测落码）】正文关于状态推导的描述停留在改造前：原文写作"prioritizes handle_status, then parses details for keywords like '关闭工单' or '确认真实火警', and finally checks read_flg"。该三级兜底结构**已被整体移除**——现 ORDER_STATUS_CASE_SQL 是仅约 5 行的 `CASE handle_status`，直接映射 0=pending / 1=deferred / 2=handling / 3=handled / 4=closed；AlertOrderStatusResolver.resolveOrderStatus() 同步简化，并删掉 lastActionRecord / isClosedAction 这类用中文串反推状态的辅助方法。凡涉及处置状态筛选、统计、导出、前端展示，判据只看 handle_status，不要再假设 details 文本或 read_flg 参与状态判定。
- 【新增第 5 态】handle_status 取值集由四态扩为五态，新增 4=已关闭；FireAlarmOrderServiceImpl.resolveHandleStatusByAction 现为：确认火警→2、关闭工单→4、标记误报/确认真实火警/手动处理→3。历史脏数据需靠迁移脚本回填后 CASE 才等价。
- 【level 双语义对筛选入参的影响（2026-10-22 PC 处置中心加筛选时实测）】同一列 `level` 在新旧数据上承载两种不同语义：旧数据（alarm_kind IS NULL）的 level 是 1-6 类型编码；新数据的 level 是风险等级闭集 {1,2,3}（1 为最高优先级）。因此 AlarmOrderPageReqVO.alarmLevel（critical/major/warning/info → mapAlarmLevelToRecordLevel → 1/2/3/4）在旧数据行上实际按"类型"过滤、在新数据行上按"等级"过滤，混存期给前端暴露"告警级别"筛选会产生误筛结果——本次改造据此决定不暴露该项，只暴露 keyword / propertyId / 时间范围 / alarmType / orderStatus。凡新增按 level 或 alarmKind 的筛选、统计入口，必须先确认目标行的 alarm_kind 是否为 NULL。
