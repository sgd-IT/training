---
title: "Dahua ADU解析器数据内容长度字节序与错误恢复修复"
usage_scenario:
    - "When debugging Dahua protocol parsing failures with 'data content length exceeds remaining bytes' warnings"
    - "When fixing cascading parsing corruption in protocol parsers"
keywords:
    - "Dahua protocol"
    - "ADU parser"
    - "byte order"
    - "data loss"
    - "parsing bug"
    - "ADU解析器"
    - "字节序修复"
    - "数据长度解析"
source: "auto"
---

## Task description
- Core requirement: fix the Dahua protocol ADU parser's data content length parsing bug that causes data loss and timestamp corruption
- Task background: DahuaAduParser.java (TCP component) and DahuaAduParser.java (UDP component) both incorrectly read the dataContentLength field in big-endian order while the protocol and builder use little-endian; when declared length exceeds remaining bytes, the parser rewinds 2 bytes causing subsequent objects to parse from wrong offsets, leading to cascading corruption and data loss

## Execution process
1. Analyzed the log warnings showing infoObject[2] length=9259 > remaining=129, infoObject[3] length=34561 > remaining=123, etc.
2. Traced the parsing logic in DahuaAduParser.java and identified two bugs: (a) dataContentLength read as BIG_ENDIAN instead of LITTLE_ENDIAN, (b) error recovery rewinds position instead of terminating parsing
3. Verified the protocol documentation confirms mixed-endian usage and that ADU builders write dataContentLength in little-endian
4. Fixed TCP component: changed dataContentLength to buf.getShort() (LE) and replaced rewind logic with truncation + loop break
5. Fixed UDP component: applied identical fixes to its DahuaAduParser.java
6. Verified the fixes resolve all warnings and produce correct timestamps

## Related files
- /module-eiot/iot-components/iot-component-tcp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/ff/parser/dahua/DahuaAduParser.java
- /module-eiot/iot-components/iot-component-udp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/udp/parser/uwb/dahua/DahuaAduParser.java

## Notes
- The byte order mismatch was the root cause: BE reading produced invalid lengths (9259), while LE reading produces valid small values (e.g., 100)
- The error recovery bug caused cascading corruption: infoObject[2] failure made infoObject[3] read from wrong offset, producing "ghost" data like universal_comp_addr=01870612 (where 0x87 is the frame typeFlag)
- After fixes, parsed timestamps are now valid (e.g., 2023-10-15 14:30:22 instead of impossible 2008-11-10 43:36:00)

## Task overview
Successfully fixed the Dahua ADU parser data loss bug: corrected dataContentLength byte order from BE to LE in both TCP and UDP components, and replaced error recovery with safe truncation and early termination; all parsing warnings eliminated and timestamps now correctly parsed.
