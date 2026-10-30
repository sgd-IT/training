---
title: "报警类型新字段不复用alarmCategory，避免第三种语义撞车"
usage_scenario:
    - "为拆分后的新语义维度挑选字段名"
    - "评估能否复用VO中未赋值字段"
    - "有人提议合并或裁撤alarm_type与alarm_kind列时"
keywords:
    - "alarmCategory语义冲突"
    - "alarm_kind命名"
    - "新增字段落点选择"
    - "来源与性质正交"
    - "前端零改动"
source: "auto"
---

承载"报警类型 1-6"新语义的字段不复用 AlarmOrderRespVO.alarmCategory，改为另起干净名字（候选 alarm_kind，DB 层 tinyint；前端接口出口仍沿用 alarmType，前端零改动）；alarmCategory 归位为"事件来源 0-5"出口，并给 alarm-order 链路补上赋值。

Rationale (trade-off): alarmCategory 虽在 VO 中定义却从未被 alarm-order 赋值，看似是理想现成落点；但实测已有三处按"来源 0-5"理解并使用它——FireDashboardServiceImpl 以 record.getAlarmType() 赋值、移动端 alarm-detail 的 ALARM_CATEGORY_MAP 注释明写 0=主机/2=热成像、monitoring-center 实时列表以 alarmType: a.alarmCategory 驱动语音播报。若再塞入"性质 1-6"即产生第三套语义撞车，违背本次改造要治的根因。

Rejected alternatives: 复用 alarmCategory 承载新类型语义（省一次 DDL，但会制造三种语义并存，正是被否定的方向）；直接改名 DB 列（触达面过大且破坏移动端既有映射）。

Applicable / expiry conditions: 在"level 收敛为 1-3 等级、报警类型新开字段"的目标态下有效；若后续用户决定让 alarmCategory 统一承载来源维度并同步改移动端映射表，则本决策需重新评估。适用于 eiot 告警字段模型设计；不适用于纯展示层临时字段。

## Notes
- 关于"alarm_type 与 alarm_kind 是否冗余、能否去掉一个"的追加论证（2026-10 复核代码后确认，两列均保留）：二者不可互推——kind=1 火警无法还原来源是主机烟感还是热成像/烟火/高温；type=0 主机链路同时承载火警/故障/离线/误报/信息六种性质，只有 kind 能区分。"看起来冗余"的两个成因：①DDL 里 alarm_type 注释也写作"报警类型"（真实语义是事件来源，定稿 1.4），注释未同步；②摄像头链路存在固定映射（type 1-3 恒→火警、4-5 恒→隐患），而主机链路 type 恒 0。
- 裁撤代价盘点（砍 alarm_type 的实际损失落点）：alarmSpeech.ts 按 0-5 分型的语音播报退化为统一播报；alarmCategory 出口（FireAlarmOrderServiceImpl.convertToAlarmType、FireDashboardServiceImpl、monitoring-center 实时列表）失去视频事件细分；30,269 条热成像存量迁移后粒度只剩"火警"；未来"视频源上报设备故障"类组合无法承载。砍 alarm_kind 不可行：它是 deriveLevel 的唯一输入、读侧双读的优先字段、主机链路性质的唯一载体。
- 曾考虑的第三条路（把视频细分塞进 kind 扩值域如 kind=7 热成像）同样被否定：会重新制造"性质与来源混在一列"的病并打碎定级规则。
- alarm_type 现状"仅摄像头链路写真实值、W1/W4/W5 写 0"不是缺陷，而是非视频通路的正确取值。若要降低混淆，做零风险动作即可：把 alarm_type 的 DB 注释改为"事件来源：0=主机/常规链路 1=高温 2=热成像 3=烟火 4=离岗 5=消防通道占用"。

## Notes
- 【文档路径修正】本条引用的《消防告警字段语义定稿》权威路径为 d:\code\remote-monitor\docs\消防告警字段语义定稿.md（工作区 docs 子目录下），按仓库根目录相对路径 Grep 该文档会零命中；检索该文档一律从 docs 目录入手。
