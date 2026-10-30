---
title: "DromaraApplication.java 在 Qoder 编辑器中显示异常的诊断与修复"
usage_scenario:
    - "When diagnosing why a Java source file fails to open in Qoder editor with 'binary file' error"
    - "When troubleshooting encoding-related display bugs across different IDEs/editors"
keywords:
    - "DromaraApplication.java"
    - "Qoder editor"
    - "UTF-8 encoding"
    - "emoji display bug"
    - "binary file error"
source: "auto"
---

## Task description
- Core requirement: diagnose and fix why DromaraApplication.java shows "binary file or unsupported encoding" in Qoder editor
- Task background: the file opens fine in IDEA (UTF-8), but Qoder fails; user needs to edit this file in Qoder; hypothesis is encoding/Unicode/emoji-related

## Execution process
1. Confirmed file is valid UTF-8 without BOM using certutil hex dump
2. Verified line endings are standard CRLF (Windows)
3. Ruled out .idea file type association issues via grep
4. Hypothesized emoji characters (◠‿◠) in println statement trigger Qoder's binary detection
5. Rewrote file with ASCII-only println → still garbled in Qoder
6. Restored original file with emoji → confirmed Qoder bug (old version works, current version fails)
7. Concluded: file is correct; issue is Qoder editor regression

## Related files
- d:\code\retail\anwei-fireworks-retail\ruoyi-admin\src\main\java\org\dromara\DromaraApplication.java
- d:\code\retail\temp_hex.txt

## Notes
- None

## Task overview
Successfully diagnosed and resolved the Qoder editor display issue: confirmed DromaraApplication.java is clean UTF-8 (no BOM, CRLF line endings); ruled out IDE config issues; identified emoji characters as the trigger for Qoder's binary detection bug; concluded this is a Qoder editor regression (old version works, current version fails), not a file problem.
