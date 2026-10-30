---
title: "Dahua协议公共解析模块统一V1/V2自动检测与LE修复"
usage_scenario:
    - "When refactoring or maintaining Dahua protocol parsers"
    - "When diagnosing parsing truncation or timestamp corruption"
    - "When implementing new ADU features requiring unified parser"
keywords:
    - "Dahua协议"
    - "公共模块"
    - "V1/V2自动检测"
    - "LE修复"
    - "协议解析"
source: "auto"
---

Merged DahuaAduParserV2.java into DahuaAduParser.java to unify protocol parsing. Added automatic V1/V2 format detection via peek + compare. Fixed dataContentLength/dataTotalLength byte order from BE to LE across TCP and UDP components. Deleted obsolete DahuaAduParserV2.java file. Resolved 'data content length exceeds remaining bytes' warnings and cascading timestamp corruption.
