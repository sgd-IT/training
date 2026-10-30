---
title: "排查记录每次完成保存触发riskEvent型MQTT推送可作日志操作痕迹"
usage_scenario:
    - "用后端日志核查用户何时完成了几条排查记录"
    - "判断现场反馈的操作时间点是否真实发生过写入"
    - "从海量MQTT日志行中定位有效业务痕迹"
keywords:
    - "AliMQTTCallback痕迹"
    - "riskEvent推送"
    - "操作轨迹还原"
    - "sys-info日志分析"
    - "updateBaseRiskTaskRecord"
source: "auto"
---

counter-intuitive fact（已实测验证）：本项目 sys-info 日志中的 AliMQTTCallback "消息发送成功 topic=demo/system" 记录是**业务事件推送的痕迹**，不是随机噪音，可当作服务端操作轨迹的计数器使用。

链路：BaseRiskTaskRecordServiceImpl.updateBaseRiskTaskRecord() 更新成功后调用 AliMQTTClient.pushType(MqttPushType.riskEvent)，即**每保存/完成一条 base_risk_task_record 就产生 1 条 MQTT 消息**。同类推送点还有：BaseCheckRecordServiceImpl(checkRecord)、BaseHiddenTroubleServiceImpl(hiddenSource)、SysNoticeServiceImpl、TaskList(ticket/gasNotify/alarmNotify)、SyncDataServiceImpl(gasNotify)。

因此"日志里某时刻连续 N 条 MQTT 发送 ≈ 该时刻有 N 条记录被完成"，时间逐秒吻合。实测 2026-10-10 全部对上：02:03(6)=冯中卫完成氯化锌第1批6条、09:39(6)=全正华第2批6条、22:56~22:57(6)=文茂华中班第3批6条、16:04(7)与22:09(7)=液氯储罐气化装置尹超杰各轮完成、13:24(4)同理。全天 224 条 MQTT 推送无一失败。

用途边界：该痕迹只反映"写操作"，APP 侧的查询动作（如打开待检列表 getTaskRecordList）在 sys-info 中完全不留痕（无 access log，日志中检索不到接口名与手机号），所以不能用它证明"某人看过/没看到任务"；需要这类证据只能让用户现场截图。另注意日志按小时切分且内容以 MQTT 行为绝对多数，分析时先抽取非 MQTT 行再看。
