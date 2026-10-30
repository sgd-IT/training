---
title: "标记误报无独立状态列仅靠details文本且操作人不落库"
usage_scenario:
    - "改造告警工单处置状态字段前核对现状"
    - "排查误报无法筛选或统计的根因"
    - "审计处置动作的操作人追溯能力"
keywords:
    - "标记误报落库"
    - "details文本追加"
    - "handle_status覆盖"
    - "handler兜底缺失"
    - "alarm_kind正交"
source: "auto"
---

告警工单"标记误报"在数据层的结构事实（2026-10-26 代码 + 生产库双重核实）：

1. eiot_alert_record 没有任何误报专用列：既无 false_alarm 标记位、无误报原因码列、也无处置动作列，且全库无 handle_record 类处置流水表。人工标记误报的唯一持久化痕迹是 details（线上类型为 text，非 varchar(255)，实测最长 323 字符无截断）末尾追加的一行自由文本 `[<LocalDateTime ISO>] 标记误报：<中文原因>`，同时写 read_flg=1、handle_status=3。

2. handle_status 是可被覆盖的单值槽位，不是动作历史：标记误报写 3 后，若再执行确认火警/稍后处置会被改写（线上 73 条中 5 条已变 2、1 条变 1），因此"曾经标过误报"只能靠扫 details 文本还原。AlertOrderStatusResolver 的 CASE 只有 pending/processing/handled/confirming 四值，不存在 false_alarm 出口。

3. 反直觉点一：alarm_kind=5 是"告警源头本身就是误报"的类型语义，人工标记动作绝不修改 alarm_kind——线上 38 条被标误报的火警其 alarm_kind 仍为 1，故按"报警类型=误报"筛选与误报率统计都捞不到人工标记的工单。

4. 反直觉点二：操作人系统性丢失。FireAlarmOrderController 中只有 confirmAlarm 做了 handler 为空时回退取 SecurityFrameworkUtils.getLoginUserNickname()，markFalseAlarm / confirmRealFire / closeOrder 三个端点均无该兜底，而前端也只提交 id + remark，结果线上 73 条标记误报记录 with_handler=0，无法追溯是谁标的。

5. 后端 VO 的 handleType / handleRemark 在 alarm-order 分页路径下不会被填充：resolveHandlerInfo 只从 details 末段抽 handler 与 handleTime，不解析动作名，所以前端即便想区分"误报"也没有可读字段。

适用边界：描述 alarm-order/monitoring-center 共用的 /false-alarm 写入链路与 eiot_alert_record 现状；不适用于设备规则引擎自动写入的 alarm_kind 语义判定。
