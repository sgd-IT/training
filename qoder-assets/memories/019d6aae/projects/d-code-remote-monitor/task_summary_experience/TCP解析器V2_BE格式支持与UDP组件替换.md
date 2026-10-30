---
title: "TCP解析器V2_BE格式支持与UDP组件替换"
usage_scenario:
    - "When debugging Dahua protocol parsing failures with 'data content length exceeds remaining bytes' warnings"
    - "When adding multi-protocol support to device access modules"
keywords:
    - "Dahua protocol"
    - "ADU parser"
    - "byte order"
    - "V2_BE"
    - "UDP component"
    - "pom.xml"
source: "auto"
---

## Task description
- Core requirement: fix the Dahua protocol ADU parser's data content length parsing bug and replace the generic UDP component with the Dahua-capable one
- Task background: the DahuaAduParser incorrectly read dataContentLength in big-endian order while the protocol and builder use little-endian; the server/pom.xml was configured to use the generic iot-component-udp instead of iot-component-udp-remote-monitor

## Execution process
1. Analyzed the user's raw hex frame and confirmed it is V2 format with BE addresses/lengths
2. Modified DahuaAduParser.java to add V2_BE validation and parsing logic, and updated determineFrameFormat() to try V2_LE → V2_BE → V1_LE
3. Updated server/pom.xml to replace iot-component-udp dependency with iot-component-udp-remote-monitor
4. Verified the database already had protocolMode="dahua" for the UDP component

## Related files
- /module-eiot/iot-components/iot-component-dahua-common/src/main/java/com/enjoyiot/eiot/component/dahua/common/parser/DahuaAduParser.java
- /server/pom.xml

## Notes
- None

## Task overview
Successfully fixed the Dahua protocol ADU parser byte order issue by adding V2_BE support and replaced the generic UDP component with the Dahua-capable one, enabling full protocol support.
