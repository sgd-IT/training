---
title: "Backend protocol parser enhancement and TCP/UDP integration"
usage_scenario:
    - "When enhancing Dahua protocol parsers to support multiple byte orders and source-tagged logging"
    - "When integrating GB/T 26875 frame parsing into UDP components"
    - "When adapting TCP components to use enhanced DahuaAduParser with TCP-specific sourceTag"
keywords:
    - "DahuaAduParser"
    - "GbtFrameParser"
    - "TCP component"
    - "UDP component"
    - "sourceTag"
    - "V2_BE"
    - "GB/T 26875"
source: "auto"
---

## Task description
- Core requirement: enhance Dahua protocol parsers to support sourceTag-based TCP/UDP log differentiation, V2 big-endian (V2_BE) frame format, and shared GB/T 26875 frame parsing; adapt TCP and UDP components to use the enhanced parsers
- Task background: DahuaAduParser lacked sourceTag for log context; V2_BE support was missing; GB/T 26875 frame parsing was duplicated across components; TCP and UDP components needed consistent parser instantiation with appropriate sourceTag

## Execution process
1. DahuaAduParser.java: added sourceTag field and constructor; updated logs to include sourceTag; implemented determineFrameFormat() to auto-detect V2_LE/V2_BE/V1_LE formats
2. Created new GbtFrameParser.java for shared GB/T 26875 frame parsing logic used by both TCP and UDP components
3. TypeHandlerRegistry.java: added diagnostic logging for application data units exceeding 512 bytes
4. DahuaUniversalDataHandler.java: updated to instantiate DahuaAduParser("TCP") for TCP-specific logging
5. UdpPacketDispatcher.java and UdpComponent.java (iot-component-udp-remote-monitor): updated to instantiate DahuaAduParser("UDP") and added Gbt26875Handler
6. UdpPacketDispatcher.java and UdpComponent.java (iot-component-udp): updated to instantiate DahuaAduParser("UDP"); added iot-component-dahua-common dependency; added handleGbtFrame method and bytesToHex utility
7. Committed and pushed the changes in three atomic commits: feat(dahua), feat(tcp), feat(udp)

## Related files
- module-eiot/iot-components/iot-component-dahua-common/src/main/java/com/enjoyiot/eiot/component/dahua/common/parser/DahuaAduParser.java
- module-eiot/iot-components/iot-component-dahua-common/src/main/java/com/enjoyiot/eiot/component/dahua/common/parser/DahuaAduBuilderV2.java
- module-eiot/iot-components/iot-component-dahua-common/src/main/java/com/enjoyiot/eiot/component/dahua/common/parser/GbtFrameParser.java
- module-eiot/iot-components/iot-component-tcp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/ff/parser/TypeHandlerRegistry.java
- module-eiot/iot-components/iot-component-tcp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/ff/parser/handler/DahuaUniversalDataHandler.java
- module-eiot/iot-components/iot-component-udp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/udp/dispatcher/UdpPacketDispatcher.java
- module-eiot/iot-components/iot-component-udp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/udp/service/UdpComponent.java
- module-eiot/iot-components/iot-component-udp/pom.xml
- module-eiot/iot-components/iot-component-udp/src/main/java/com/enjoyiot/eiot/component/udp/dispatcher/UdpPacketDispatcher.java
- module-eiot/iot-components/iot-component-udp/src/main/java/com/enjoyiot/eiot/component/udp/service/UdpComponent.java

## Notes
- None

## Task overview
Successfully delivered a 3-commit backend release: enhanced DahuaAduParser with sourceTag-based logging and V2_BE support; introduced shared GbtFrameParser for GB/T 26875 frames; adapted TCP and UDP components to use the enhanced parsers — all verified and pushed to master.
