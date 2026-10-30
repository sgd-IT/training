---
title: "大华设备上报链路三项缺陷核实：TCP确认帧缺失与UDP去重签名缺陷"
usage_scenario:
    - "核查GB/T 26875上行确认帧是否实现"
    - "定位同源码帧被误去重的原因"
    - "验证协议文档与代码实现一致性"
keywords:
    - "GB/T 26875"
    - "确认帧缺失"
    - "UDP去重签名"
    - "SLF4J占位符"
    - "Dahua设备"
source: "auto"
---

## Task description
- Core requirement: verify the user's three claims about TCP/UDP device reporting chain issues — (1) TCP confirmation frame missing, (2) UDP no-response design, (3) log placeholder bug
- Task background: two remote monitoring components (TCP for real Dahua devices, UDP for simulator); protocol is GB/T 26875; key files include GbtTcpComponent.java, UdpComponent.java, GbtCommandSender.java, DahuaProtocolConstants.java, and protocol documentation; diagnosis required cross-file verification of method signatures, call sites, command constants, and protocol compliance

## Execution process
1. Verified TCP handleDahuaDeviceReport signature lacks GbtDataPackage pkg parameter and does not call sendConfirm, while handleDahuaRegister does call it — confirming a code omission, not design
2. Confirmed GbtCommandSender.sendConfirm requires receivedPkg.getSerialNumber(), proving the missing pkg prevents correct sequence number echo
3. Verified UDP parseSimpleReportFrame logs "解析#0 注册" via DahuaAduParser.java:101, confirming user's log observation and chain understanding
4. Discovered critical UDP dedup signature flaw: signature = srcAddr + ":sf:" + i excludes data content, causing all subsequent frames from same source to be deduped regardless of actual data change
5. Confirmed SLF4J {:02X} placeholder bug exists in 18 locations across UdpComponent, GbtTcpComponent, and GbtCommandSender
6. Cross-referenced protocol document v1.04 sections 5.1 (information upload = send/confirm) and 5.2 (≤30s timeout × 3 retries), proving TCP device reconnection is direct evidence of missing confirmation

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\iot-components\iot-component-tcp-remote-monitor\src\main\java\com\enjoyiot\eiot\component\ff\service\GbtTcpComponent.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\iot-components\iot-component-udp-remote-monitor\src\main\java\com\enjoyiot\eiot\component\udp\service\UdpComponent.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\iot-components\iot-component-tcp-remote-monitor\src\main\java\com\enjoyiot\eiot\component\ff\service\GbtCommandSender.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\iot-components\iot-component-dahua-common\src\main\java\com\enjoyiot\eiot\component\dahua\common\parser\DahuaAduParser.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\doc\消防终端与平台通讯国标协议v1.04解读.md

## Notes
- None

## Task overview
Successfully verified all three user claims: (1) TCP confirmation frame is a genuine code omission, not design; (2) UDP 0x02 reporting is currently designed without response, but protocol mandates send/confirm — making it a defect; (3) {:02X} log placeholder bug exists across 18 locations. Additionally uncovered a critical UDP dedup signature flaw that suppresses all data changes from the same source.
