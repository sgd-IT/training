---
title: "UDP组件不缓存设备地址仅支持同链路即时被动回复"
usage_scenario:
    - "实现基于UDP的平台主动下行前评估可行性"
    - "诊断UDP链路报文回不到设备的原因"
keywords:
    - "UDP"
    - "senderAddr"
    - "被动回复"
    - "无地址缓存"
    - "NAT端口漂移"
source: "auto"
---

UDP 组件不缓存设备网络地址（无 ConcurrentHashMap 或任何 Map 形式的地址表）。所有 senderAddr 只出现在同一次收包链路内的即时回复中：UdpVerticle.handlePacket → dispatch → handleDahua/handleRegister → sendConfirmFrame，链路结束即丢弃。因此 UDP 链路上不存在"平台事后主动下行"的能力，也不能靠缓存 senderAddr 来下发校时/配置类指令（NAT 端口会漂移）。需要主动下行时必须走 TCP 长连接通道。
