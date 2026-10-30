---
title: "UDP注册确认帧实现：CMD=0x03应答帧构建与发送"
usage_scenario:
    - "当设备注册后需要平台返回确认帧时"
    - "当调试UDP设备注册流程失败时"
    - "当扩展UDP组件支持其他应答类型时"
keywords:
    - "UDP注册"
    - "CMD=0x03"
    - "确认帧"
    - "GbtFrameBuilder"
    - "UdpVerticle.sendPacket"
source: "auto"
---

实现了UDP组件在接收到设备注册帧（CMD=0x00）后，向设备发送确认帧（CMD=0x03）的完整链路。关键改动包括：1) 在iot-component-dahua-common中新增GbtFrameBuilder.java，提供buildConfirmFrame()方法构建符合协议的确认帧；2) 在UdpConfig中添加centerAddr字段配置平台中心地址；3) 在UdpVerticle中新增sendPacket()方法通过DatagramSocket发送UDP报文；4) 在UdpPacketDispatcher和UdpComponent中传递senderAddr并调用GbtFrameBuilder.buildConfirmFrame()生成并发送确认帧。确认帧格式严格遵循协议图2.2.1.1-2：@@ | 流水号 | 版本 | 时间标签 | 源地址(平台) | 目的地址(设备) | ADU长度=0 | CMD=0x03 | 校验和 | ##。
