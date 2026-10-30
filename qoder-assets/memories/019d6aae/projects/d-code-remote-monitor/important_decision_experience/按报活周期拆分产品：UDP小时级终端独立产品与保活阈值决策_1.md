---
title: "按报活周期拆分产品：UDP小时级终端独立产品与保活阈值决策_1"
usage_scenario:
    - "新增低频报活设备接入时决定建产品还是共用"
    - "设备频繁误判离线的根因定位"
    - "修改组件配置后判断是否需要重启"
keywords:
    - "keep_alive_time 产品级"
    - "UDP与TCP共用产品拆分"
    - "DeviceStateCheckTask 离线判定"
    - "组件配置5秒轮询热加载"
    - "UdpVerticle cleanOffline 删除"
source: "auto"
---

结论：为 1 小时一报的大华 UDP 消防终端单独建产品（'DhUdpF7xQm2Kp931'，keep_alive_time=7200），不再与 GBT26875-TCP 终端共用 'FfGbt8CE75Aw0001'（keep_alive_time=600）。
理由：设备离线判定由 DeviceStateCheckTask 按"设备所属产品的 keep_alive_time × 1.1"逐台计算，是产品级参数；同一产品同时被 UDP 组件（defaultProductKey）和 TCP 组件（fallbackProductKey，端口 8103）使用时，600 秒对常在线 TCP 合理、对小时级 UDP 必然误判离线。
被否方案：把共用产品的 keep_alive_time 改成 7200（会让 TCP 设备失联检测拖到 2.2 小时）；在 UDP 组件内部另做一套秒级超时下线（已删除：UdpVerticle 的 60s cleanOffline，其 lastSeen 混用 srcAddr 与 dn 两种 key，handleOffline 固定用 defaultProductKey，上报的设备名对不上）。
适用边界：接入方式的报活周期与同产品其他设备差异超过一个数量级时才拆产品；否则沿用共享产品。改 eiot_component.config 无需重启——ThingComponent.config() 每 5 秒轮询，状态或配置变化即自动重启监听；但直接改 eiot_product 需经产品管理接口保存或等 Redis 缓存 1 小时 TTL（cache type=REDIS, time-to-live=1h）。
