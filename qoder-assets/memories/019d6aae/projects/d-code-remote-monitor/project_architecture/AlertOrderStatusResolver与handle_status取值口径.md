---
title: "AlertOrderStatusResolver与handle_status取值口径"
usage_scenario:
    - "诊断告警中心或大屏待处置状态显示异常"
    - "扩展告警处置或顺延流程时核对状态取值"
    - "同步Java解析逻辑与SQL未处置过滤条件"
keywords:
    - "handle_status取值"
    - "未闭环判据"
    - "AlertOrderStatusResolver"
    - "deferAlertRecord"
    - "selectActiveAlert"
    - "状态解析优先级"
source: "user"
---

AlertOrderStatusResolver（类名首字母 A，非 Fire）是告警订单状态解析的权威组件，其 resolveOrderStatus() 优先检查 handle_status 字段，仅在 handle_status 未设置时才回退到解析 details 字段中的处置记录文本。

handle_status 取值口径（2026-10 经 V20260805_2__camera_alarm_fields.sql 列注释 + AlertRecordMapper 判据双重核实）：
- 0 = 待处置（NULL 同义，历史行可能为 NULL）
- 1 = 稍后处置（deferAlertRecord 写入，仍属未闭环）
- 2 = 处置中
- 3 = 已处置（handleAlertRecord 写入并置 read_flg=true）

**未闭环（报警中）判据 = `handle_status IS NULL OR handle_status < 3`**，AlertRecordMapper.selectActiveAlert 等既有方法均按此实现；新建统计口径应直接复用该判据而非自行枚举。

## Notes
- 本条旧版曾记为"1=顺延仍属未处置、2=已处置（handleAlertRecord 置 2）"，与当前代码和 DDL 注释不符（已处置实为 3、2 是处置中），据此写未闭环过滤会把"处置中"误算成已闭环。以本条新口径为准；同一知识点另见记忆《消防值守告警链路的现成底座现状事实》第 1 点。
- 弹窗轮询侧判据补充（2026-10 读 AlertServiceImpl.java 与 AlertRecordMapper.java L193 实测核实）：大屏弹窗轮询用的 selectRecentUnprocessedAlerts 过滤条件是 `(handle_status IS NULL AND (read_flg = 0 OR read_flg IS NULL)) OR handle_status < 2`，即 **status=0/1 都仍在"未处置列表"**——deferAlertRecord 只写 handle_status=1 且不改 read_flg，因此"稍后处置"后告警并不会从后端未处置列表消失（与上面 selectActiveAlert 的 <3 判据是两条不同链路，勿混用）。但 2026-10-15 起 duty-screen 前端已拍板抑制：onHandleLater 在 defer 成功后把 alertId 记入页面级 deferredAlertIds，fetchRecentAlerts 一级过滤跳过这些 id，全屏弹窗不再每 60 秒重弹（纯内存态，刷新页面后按当天回捞窗口恢复一次提醒）；后端判据与返回均未变。另外 confirmAlertRecord 实际写 read_flg=true + handle_status=3，handleAlertRecord 写 handle_status=2 + read_flg=true，与旧注"confirm置2/handle置3且read_flg=false"不一致，以本条为准。适用于排查大屏弹窗重复/延后无效问题；不适用于告警中心工单状态展示（那里以 <3 未闭环判据为准）。
