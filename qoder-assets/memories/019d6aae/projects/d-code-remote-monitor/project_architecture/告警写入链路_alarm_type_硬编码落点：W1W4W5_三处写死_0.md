---
title: "告警写入链路 alarm_type 硬编码落点：W1/W4/W5 三处写死 0"
usage_scenario:
    - "改造写入链路补打真实来源标识前定位必改点"
    - "按告警来源做统计时选择分组字段"
    - "排查AEP与主机告警来源无法区分问题"
keywords:
    - "alarmType硬编码0"
    - "AlarmRecordService落点"
    - "createAlarmOrder"
    - "addAlertRecord缺省"
    - "设备维度区分来源"
source: "auto"
---

四类来源在 eiot_alert_record.alarm_type 列上不可区分的根因是写入链路硬编码，具体落点（改造补打真实来源值时的必改点）：

- W1 AEP 链路：AlarmRecordService.buildAlertRecord 中 `.alarmType(0)` 硬编码；
- W4 人工建单：FireAlarmOrderServiceImpl.createAlarmOrder 同样写 `.alarmType(0)`；
- W5 主机链路：AlertConfigServiceImpl.addAlertRecord 构建 AlertRecord 时根本不设置该字段，走 DB 默认值 0。

仅摄像头类链路（DeviceAlertConfigServiceImpl.reportCameraAlert）会写真实的 alarm_type 取值。

需要按来源统计或筛选时，改用设备维度关联（device_id / product_key / device_type，见 eiot_device_info 台账），不能依赖 alarm_type。反向印证：设备台账（product_key + device_type + parent_id 父子挂接）才是来源的实际登记载体，接入渠道只决定走哪条写入链路。

注意：W1/W4/W5 写 0 属"非视频通路的正确取值"，不是待修缺陷——只有当业务真要区分主机/AEP/人工等来源做统计时，才需要按上述落点补打真实值。

## Notes
- 《消防告警字段语义定稿》.md 的实际路径是仓库工作区根下的 `docs\消防告警字段语义定稿.md`（即 d:\code\remote-monitor\docs\消防告警字段语义定稿.md），不在 d:\code\remote-monitor\ 根目录；直接按根目录路径 Grep 会零命中并误判文档缺失，检索该文档一律从 docs 目录入手。
