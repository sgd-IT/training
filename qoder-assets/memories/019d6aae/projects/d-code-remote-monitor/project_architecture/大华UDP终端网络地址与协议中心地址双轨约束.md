---
title: "大华UDP终端网络地址与协议中心地址双轨约束"
usage_scenario:
    - "为UDP/TCP终端填写设备端平台地址时"
    - "排查strictCenterAddrCheck丢帧或设备收不到确认帧时"
    - "修改eiot_component的host/port配置字段时"
keywords:
    - "udp://前缀"
    - "中心地址双轨"
    - "strictCenterAddrCheck"
    - "Vert.x host绑定"
    - "设备侧地址配置"
source: "auto"
---

大华 UDP 消防终端接入时存在两套互不相干的地址（本轮 grep 与代码核实）：

1. 网络地址：仅用于设备 socket 发包，不进协议帧。部分型号设备配置界面的"平台地址"字段要求填 URI 串（如 udp://14.22.72.94:48888），该 scheme 前缀纯属设备侧 UI 约定——平台全仓 grep "udp://" 零命中，平台下行给设备的只有二进制确认帧（CMD=0x03）。scheme 须与传输方式一致：UDP 组件用 udp://IP:48888，GB/T 26875 TCP 组件用 tcp://IP:18888。
2. 协议中心地址：6字节12位HEX（平台侧为 UdpConfig.centerAddr，值 020000000001），进帧头目的地址字段。上行帧 dst=centerAddr、src=设备地址；平台确认帧 src=centerAddr、dst=设备地址。strictCenterAddrCheck=true 时上行 dst 必须精确等于 centerAddr，但 dst 全零放行（对应设备侧无中心地址输入项的型号）。

反直觉事实：生产库在跑的 TCP 组件（GBT26875-tcp id=8、四信 id=25）config 均无 centerAddr（全零），说明现网设备大概率未配该项。平台侧 eiot_component.config 的 host/port 是分开裸值（host 必须 0.0.0.0），若在 host 里写 udp:// 前缀会直接传给 Vert.x socket.listen(port, host) 导致解析失败、端口绑不上。

适用边界：大华/国标类 UDP 或 TCP 终端的设备侧地址配置与平台对齐；不适用：AEP 等云端 MQTT 接入链路。
