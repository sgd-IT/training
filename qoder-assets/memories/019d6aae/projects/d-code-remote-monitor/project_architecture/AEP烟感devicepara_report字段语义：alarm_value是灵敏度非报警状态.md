---
title: "AEP烟感devicepara_report字段语义：alarm_value是灵敏度非报警状态"
usage_scenario:
    - "解读AEP烟感上报payload判断是否报警"
    - "排查AEP字段未入库或属性丢失问题"
    - "扩展AEP告警解析策略时确认可用字段"
keywords:
    - "devicepara_report"
    - "alarm_value灵敏度"
    - "smoke_state"
    - "serviceid"
    - "parseDataReport"
    - "DevicePropertyHandler"
source: "auto"
---

AEP 烟感（product_key 17339615）的 `devicepara_report` 类 dataReport 是**周期参数快照，不是报警报文**，判读时必须按以下口径：

- `alarm_value` 在物模型里是「报警灵敏度」（AEP#19），取值 1/2/3 = 低/中/高，**与是否报警无关**；名字极易被误读成报警标志。真正的报警态字段是 `smoke_state`（0 才是报警态）、`error`、`fault`、`tamper_alarm`、`humidity_state`、`temperature_state`、`disable_status`。
- `serviceid` 即 AEP 服务号：10=devicepara_report（参数快照）、11=smoke_report、1003=smoke_state_report；只有后两类才承载报警语义。
- `AepHttpPushAlarmStrategy.parseDataReport` 只检查上述告警字段，devicepara_report 快照一律返回空列表，不产生 eiot_alert_record。
- `DevicePropertyHandler` 按物模型标识符过滤 payload，不在物模型内的字段会被静默丢弃（实测 `battery_voltage` 即被丢弃，只能从原始报文看电压）。
- 任意上行 dataReport 都会刷新 Redis `iot:deviceLast:{deviceId}`，因此这类快照同时充当 NB 设备心跳，维持 86400s 保活窗口内的在线判定。
- 冗余对照字段：`sensor_value`/`base_value` 为蓝光采样与本底，`sensor_redvalue`/`redbasevalue` 为红光采样与本底，采样值≈本底值即无烟；`pollution_index`=迷宫污染指数；`humidity_value` 该型号历史各次上报恒为 0，实际未采集湿度。
