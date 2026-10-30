---
title: "FragmentMessage.java 静默失败与重试策略实现"
usage_scenario:
    - "When implementing silent-failure and retry logic for non-critical Android fragment network requests"
    - "When optimizing mobile app startup UX by suppressing transient network failure toasts"
keywords:
    - "FragmentMessage"
    - "静默失败"
    - "重试"
    - "loadNum"
    - "Android fragment"
source: "auto"
---

## Task description
- Core requirement: summarize the modifications made to FragmentMessage.java as work content
- Task background: the mobile app shows disruptive '服务异常' toast on startup due to non-critical network requests failing; FragmentMessage.java's loadNum() method triggers getAfootCount(), which needs resilience without UI interference

## Execution process
1. Added private int loadNumRetryCount = 0; field (line 30) to control automatic retry attempts
2. Refactored successError() (lines 107–113): now only shows login-expired (401) dialog; all other error codes log silently
3. Refactored failed() (lines 116–126): replaced immediate '服务异常' Toast with silent logging and a 3-second delayed single retry, guarded by fragment attachment and visibility checks to prevent memory leaks

## Related files
- d:\code\hc-standard\other1\app\src\main\java\com\sckj\other1\fragment\FragmentMessage.java

## Notes
- The retry logic avoids memory leaks by checking isAdded() && isVisible() before re-executing loadNum()
- This change isolates resilience behavior to the specific fragment, making it reusable for other non-critical startup requests

## Task overview
Successfully implemented silent-fail and single-retry resilience for FragmentMessage.loadNum(), eliminating disruptive '服务异常' toast on app startup while preserving data loading reliability.
