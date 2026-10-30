---
title: "贵州推送仓库和库房place_no编码问题诊断与修复"
usage_scenario:
    - "当贵州推送出现'仓库或库房编码不存在'报错时进行根因定位"
    - "排查SBDY/ZBBJ数据类型place_no编码生成失败问题"
    - "验证GZCodeGenerator.parseSeq方法对非数字后缀的解析能力"
keywords:
    - "贵州推送"
    - "place_no"
    - "编码问题"
    - "parseSeq"
    - "仓库编码"
source: "auto"
---

## Task description
- Core requirement: diagnose and fix the "incorrect and non-existent warehouse/warehouse encoding" issue for Shuicheng Daily Necessities (水城日杂) in Guizhou (贵州) push
- Task background: SBDY/ZBBJ data types require correct `place_no` encoding; the provincial platform rejects non-standard formats like `ryzp-102-ck` and letter-suffixed codes like `ryzp-101-kf-a`, causing cascading errors

## Execution process
1. Database investigation: queried `em_information`, `em_warehouse`, and `em_storeroom` tables to confirm company data and identify problematic `store_num`/`storeroom_num` values (`ryzp-102-ck`, `ryzp-101-kf-a`, etc.)
2. Code analysis: traced `GZCodeGenerator.parseSeq` method logic, confirming it fails to extract numeric sequences from letter suffixes, leading to skipped records and missing encodings on the platform
3. Root cause confirmation: verified that `parseSeq` cannot handle alphabetic suffixes (a/b), causing storeroom codes to be skipped and resulting in "non-existent" errors on the provincial platform
4. Repair plan formulation: recommended converting letter suffixes to numeric ones (e.g., `kf-a` → `kf-1`) to ensure `parseSeq` can correctly extract sequences and generate valid 15-digit warehouse codes

## Related files
- /src/main/java/com/monkey/general/util/gz/util/GZCodeGenerator.java
- /src/main/java/com/monkey/general/platform/push/gz/PushingGZDataService.java
- /src/main/java/com/monkey/general/platform/push/gz/SetGZUploadInfo.java

## Notes
- None

## Task overview
Successfully diagnosed the root cause of the Guizhou push encoding issue: identified that `parseSeq` fails on letter-suffixed `store_num`/`storeroom_num`, leading to skipped records and "non-existent" errors; formulated a precise repair plan to convert letter suffixes to numeric ones to ensure correct 15-digit warehouse code generation.
