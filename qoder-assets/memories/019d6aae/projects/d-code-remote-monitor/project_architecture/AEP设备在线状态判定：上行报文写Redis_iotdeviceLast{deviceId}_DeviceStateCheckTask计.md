---
title: "AEP设备在线状态判定：上行报文写Redis iot:deviceLast:{deviceId}, DeviceStateCheckTask计算离线, 禁用deviceOnlineOfflineReport"
usage_scenario:
    - "排查AEP设备状态抖动或长期离线问题"
    - "解读AEP上行报文判断是否刷新心跳"
    - "维护AEP回调入口消息过滤逻辑"
keywords:
    - "AEP"
    - "在线状态"
    - "iot:deviceLast"
    - "DeviceStateCheckTask"
    - "dataReport"
    - "deviceOnlineOfflineReport"
source: "auto"
---

AEP设备在线状态判定存在一个关键架构约束：
- 在线状态由任意上行报文（dataReport/eventReport/commandResponse）触发，DeviceStateCheckHandler 将其时间戳写入 Redis iot:deviceLast:{deviceId}；
- 离线状态由 DeviceStateCheckTask 定时任务计算 `now - deviceLast > keepAliveTime × 1.1` 后置为 state=0；
- AEP 平台推送的 deviceOnlineOfflineReport 消息必须被入口过滤丢弃，因为它反映的是 NB-IoT 模组的 PSM 寻呼状态而非设备真实存活，实测会导致 0.3 秒内 online→offline 抖动。
