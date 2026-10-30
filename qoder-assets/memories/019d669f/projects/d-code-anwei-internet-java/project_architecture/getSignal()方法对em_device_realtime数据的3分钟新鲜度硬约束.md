---
title: "getSignal()方法对em_device_realtime数据的3分钟新鲜度硬约束"
usage_scenario:
    - "When debugging castingStatus=0 caused by stale signal data"
    - "When modifying or extending the real-time signal query logic"
keywords:
    - "getSignal"
    - "em_device_realtime"
    - "3分钟新鲜度"
    - "硬约束"
source: "auto"
---

The getSignal() method in DeviceRealtimeServiceImpl enforces a hard 3-minute freshness constraint on the em_device_realtime table data: if the updateTime of the record is older than 3 minutes, the method returns 0 directly without querying the ax0083 point. This constraint is not configurable and is embedded in the business logic.
