---
title: "Remote platform operatorInfo field sanitization for Chinese punctuation"
usage_scenario:
    - "When debugging remote platform sync failures caused by Chinese punctuation in operatorInfo field"
    - "When implementing field sanitization for remote API submissions"
keywords:
    - "operatorInfo"
    - "remote platform"
    - "format error"
    - "Chinese punctuation"
    - "sanitization"
source: "auto"
---

## Task description
- Core requirement: diagnose and fix the '作业人员信息格式错误' error occurring when syncing work tickets to the remote platform
- Task background: the platform's /openapi/work/saveWorkSpecial endpoint validates the operatorInfo field; logs show that sending '石磊:420582198208171674,高处安装、维护、拆除作业,T420582198208171674' fails with code 500, while identical formats without '、' (e.g., '低压电工作业') succeed

## Execution process
1. Grep sys-info.log for ZY-426082708361124 to locate the failing request and response
2. Read the failing request payload to confirm operatorInfo value contains '、'
3. Grep other successful saveWorkSpecial calls to compare operatorInfo formats — confirmed success requires no '、' punctuation
4. Propose fix: add operatorInfo.replace("、", "") in PlatementTicketNewApi.transformOperatorInfo() before sending to platform

## Related files
- c:\Users\Administrator\Desktop\sys-info.2026-08-27.0.log
- d:\code\hc-standard\ruoyi-common\src\main\java\com\ruoyi\common\platement\api\PlatementTicketNewApi.java

## Notes
- The error message '多人用英文分号隔开' is misleading — the failure occurs even for single-person operatorInfo containing '、', not due to semicolon usage

## Task overview
Successfully diagnosed root cause of remote platform '作业人员信息格式错误': the platform's regex parser rejects Chinese punctuation '、' in the operatorInfo field. A minimal code fix was proposed to sanitize the punctuation before API submission.

## Related Memory
- See 'Remote platform API field validation pitfalls: avoid literal interpretation of error messages' for the broader principle and fix pattern.
