---
title: "华皓IP音柱HTTP接口地址及超时含义"
usage_scenario:
    - "语音播报日志报SocketTimeoutException且URL为192.168.1.3:10011"
    - "排查华皓音柱无法播报时需优先验证该IP端口连通性"
    - "同类IP音柱设备故障定位时参考标准接口路径"
keywords:
    - "华皓音柱"
    - "SocketTimeoutException"
    - "192.168.1.3:10011"
    - "HTTP接口"
---

华皓IP音柱语音播报接口地址为 `http://192.168.1.3:10011/External/StartTTSBroadcast`，当调用该URL出现 `java.net.SocketTimeoutException: timeout` 时，表明设备物理不可达（网络不通、设备断电、服务未启动或端口被防火墙拦截），而非代码逻辑问题。（来源：日志分析）
