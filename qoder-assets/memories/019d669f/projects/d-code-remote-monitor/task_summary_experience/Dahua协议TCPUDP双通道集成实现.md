---
title: "Dahua协议TCP/UDP双通道集成实现"
usage_scenario:
    - "When diagnosing Dahua protocol communication failures across TCP and UDP transports"
    - "When extending protocol support to new device vendors in the remote monitoring system"
keywords:
    - "Dahua"
    - "TCP"
    - "UDP"
    - "GB/T 26875"
    - "protocol integration"
    - "0x84"
    - "system analog frame"
source: "auto"
---

## Task description
- Core requirement: integrate Dahua protocol support across the TCP and UDP remote monitoring components
- Task background: the system already supports Fourfaith protocol over TCP; Dahua protocol needs to be added for both TCP and UDP transport layers, requiring updates to parsers, constants, configuration, and service logic

## Execution process
1. Batch 1: Refactor Dahua ADU/Event parsers and add new type constants (DahuaUpgradeTypeConstants, etc.)
2. Batch 2: Extend TCP component (GbtTcpComponent) to support dual-protocol routing via a new protocolMode field; update config (GbtTcpConfig), encoders/decoders, and service logic
3. Batch 3: Enhance UDP component (UdpVerticle, UdpPacketDispatcher) to handle Dahua-specific message types (registration, confirmation, time sync, platform commands) by adding version parameter and new processing branches
4. Add 0x84 system analog frame parsing in DahuaAduParser.java: parse sysType(1)+sysAddr(1)+analogType(2,LE)+analogValue(2,LE)+ts(6)=12B records, and update summarizeResult to read gbtType when dataType is missing
5. Update UdpComponent.java to route 0x84 frames to dahua_analog_report event name

## Related files
- module-eiot/iot-components/iot-component-dahua-common/src/main/java/com/enjoyiot/eiot/component/dahua/common/constants/DahuaAnalogTypeConstants.java
- module-eiot/iot-components/iot-component-dahua-common/src/main/java/com/enjoyiot/eiot/component/dahua/common/constants/DahuaConfirmEncryptConstants.java
- module-eiot/iot-components/iot-component-dahua-common/src/main/java/com/enjoyiot/eiot/component/dahua/common/constants/DahuaFixedParamTypeConstants.java
- module-eiot/iot-components/iot-component-dahua-common/src/main/java/com/enjoyiot/eiot/component/dahua/common/constants/DahuaVariableParamTypeConstants.java
- module-eiot/iot-components/iot-component-dahua-common/src/main/java/com/enjoyiot/eiot/component/dahua/common/constants/DahuaUpgradeTypeConstants.java
- module-eiot/iot-components/iot-component-dahua-common/src/main/java/com/enjoyiot/eiot/component/dahua/common/parser/DahuaAduParser.java
- module-eiot/iot-components/iot-component-dahua-common/src/main/java/com/enjoyiot/eiot/component/dahua/common/parser/DahuaEventParser.java
- module-eiot/iot-components/iot-component-tcp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/ff/model/GbtTcpConfig.java
- module-eiot/iot-components/iot-component-tcp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/ff/parser/GbtConstants.java
- module-eiot/iot-components/iot-component-tcp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/ff/parser/GbtDataDecoder.java
- module-eiot/iot-components/iot-component-tcp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/ff/parser/GbtDataEncoder.java
- module-eiot/iot-components/iot-component-tcp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/ff/parser/GbtDataPackage.java
- module-eiot/iot-components/iot-component-tcp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/ff/parser/GbtDataParser.java
- module-eiot/iot-components/iot-component-tcp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/ff/parser/TypeHandlerRegistry.java
- module-eiot/iot-components/iot-component-tcp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/ff/service/GbtCommandSender.java
- module-eiot/iot-components/iot-component-tcp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/ff/service/GbtDataReporter.java
- module-eiot/iot-components/iot-component-tcp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/ff/service/GbtTcpComponent.java
- module-eiot/iot-components/iot-component-udp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/udp/dispatcher/UdpPacketDispatcher.java
- module-eiot/iot-components/iot-component-udp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/udp/model/UdpConfig.java
- module-eiot/iot-components/iot-component-udp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/udp/service/UdpComponent.java
- module-eiot/iot-components/iot-component-udp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/udp/service/UdpVerticle.java

## Notes
- None

## Task overview
Successfully integrated Dahua protocol support across the TCP and UDP components: Batch 1 introduced foundational Dahua constants and parser refactoring; Batch 2 enabled dual-protocol routing in the TCP component; Batch 3 extended the UDP component to handle Dahua-specific message types. All changes were committed successfully with descriptive git messages.
