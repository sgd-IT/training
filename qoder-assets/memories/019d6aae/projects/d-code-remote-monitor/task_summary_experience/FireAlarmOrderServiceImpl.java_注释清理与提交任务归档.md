---
title: "FireAlarmOrderServiceImpl.java 注释清理与提交任务归档"
usage_scenario:
    - "When cleaning up outdated or poorly worded comments in Java files"
    - "When committing code changes that only involve comment modifications"
keywords:
    - "comment cleanup"
    - "FireAlarmOrderServiceImpl"
    - "Git commit"
    - "Conventional Commits"
source: "auto"
---

## Task description
- Core requirement: clean up outdated or problematic comments in FireAlarmOrderServiceImpl.java without changing any code logic
- Task background: the file contains two outdated comments (L1085-1086) and one comment with poor wording (L780); the user explicitly instructed to only modify comments and then commit the file

## Execution process
1. FireAlarmOrderServiceImpl.java L1085-L1086: deleted the outdated comments "// 尝试从 record 中获取设备ID相关信息" and "// 如果有设备ID，通过多表关联获取设备类型"
2. FireAlarmOrderServiceImpl.java L780: revised the comment from "//重复的key可以过，空值过滤不了" to "// 先去重再用 HashMap 手动 put：toMap 遇重复 key 抛异常且不接受 null 值"
3. Executed git add and git commit for the modified file with the message "perf(alert): 告警分页列表设备与租户信息改批量查询，消除 N+1"
4. Verified the commit was successful and the commit message was correctly stored as UTF-8 (confirmed via hex dump)

## Related files
- module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/firewatch/FireAlarmOrderServiceImpl.java

## Notes
- The PowerShell terminal displayed the commit message as乱码, but this was confirmed to be a display-layer issue; the actual commit message stored in Git is correct UTF-8.
- The commit was not pushed to the remote repository, as the user did not request it.

## Task overview
Successfully completed the comment cleanup and Git commit task: two outdated comments were deleted and one was revised; the file was committed with a Conventional Commits-formatted message; the commit's integrity was verified by examining the raw bytes of the commit message.
