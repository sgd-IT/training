---
title: "设备告警统一写入 eiot_alert_record 并停用 eiot_device_alert_record"
usage_scenario:
    - "When deciding between dual-write and full migration for device alert persistence"
    - "When balancing duty-screen popup capability against de-duplication and recovery requirements"
keywords:
    - "device alert"
    - "dual-write"
    - "eiot_device_alert_record"
    - "de-duplication"
    - "recovery"
source: "user"
---

Conclusion: 设备告警（DeviceAlertChecker/DeviceAlertAction 链路）统一写入 eiot_alert_record 并推送 WebSocket firewatch-alert-new，代码层全面停用 eiot_device_alert_record（表结构与历史数据保留，DeviceAlertRecordDO 保留）。
Rationale: duty-screen 弹窗、告警处置、报警记录页统一认 eiot_alert_record；停用 device 表后去重与恢复必须迁移——去重查询 AlertRecordMapper.selectActiveAlert（同设备同名且 handle_status IS NULL OR < 3），设备恢复置 handle_status=3 闭环，新告警 handle_status=0。
Rejected alternatives: 双写（保留 device 表）方案已被用户明确否决；跨模块传 dal 的 AlertRecordDO 不可行（api 模块看不见 dal 类）。
Applicable: 适用于该仓库设备告警路由；若未来恢复 device 表使用或引入统一告警抽象层需重新评估。
