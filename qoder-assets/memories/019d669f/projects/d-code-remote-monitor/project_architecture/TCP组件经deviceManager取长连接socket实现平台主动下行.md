---
title: "TCP组件经deviceManager取长连接socket实现平台主动下行"
usage_scenario:
    - "在TCP链路上扩展校时或配置类下行功能"
    - "排查已知在线设备主动下行失败原因"
keywords:
    - "TCP"
    - "主动下行"
    - "长连接"
    - "GbtVertxTcpClient"
    - "deviceManager"
    - "sendGbtPacket"
source: "auto"
---

TCP 组件的平台主动下行（校时、功能配置、远程控制等）实现方式：从 deviceManager.getClient(deviceAddr) 取回存活的 GbtVertxTcpClient 实例，直接向其 socket 写入下行报文（GbtCommandSender.sendGbtPacket 即按此解析 deviceAddr）。该能力依赖 TCP 长连接持久性——socket 存活期间不受 NAT 端口漂移影响可正常下行；socket 已关闭时操作失败并报 "device not online"。因此新增主动下行功能只能挂在 TCP 链路，UDP 链路无对应能力。
