---
title: "小程序页面console.log清理与异常处理修复"
usage_scenario:
    - "When cleaning up debug logs before production release"
    - "When fixing unhandled Promise rejections in page lifecycle methods"
keywords:
    - "console.log"
    - "debugger"
    - "try catch"
    - "unhandled rejection"
    - "pages cleanup"
source: "auto"
---

## Task description
- Core requirement: remove all 11 console.log statements from the specified pages code and fix the unhandled Promise rejection bug introduced by an incomplete try/finally block in index.vue
- Task background: console.log statements are prohibited in production code; the pullData() method in index.vue was incorrectly changed to try/finally without catch, causing unhandled rejections on request failure

## Execution process
1. Identified and removed 3 console.log from pages/index/index.vue (L236, L259, L271)
2. Removed 3 console.log from pages/profile/index.vue (L85, L107, L123)
3. Removed 2 console.log from pages/login/index.vue (L113, L153)
4. Removed 2 console.log from pages/profile/device-list.vue (L94, L112)
5. Removed 1 console.log from pages/alarm-center/index.vue (L149)
6. Fixed the pullData() method in pages/index/index.vue by restoring the empty catch block (`} catch (e) {} finally {`) to prevent unhandled Promise rejections
7. Verified no console.log or debugger remains using grep

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor-mobile\pages\index\index.vue
- d:\code\remote-monitor\anwei-fire-remote-monitor-mobile\pages\profile\index.vue
- d:\code\remote-monitor\anwei-fire-remote-monitor-mobile\pages\login\index.vue
- d:\code\remote-monitor\anwei-fire-remote-monitor-mobile\pages\profile\device-list.vue
- d:\code\remote-monitor\anwei-fire-remote-monitor-mobile\pages\alarm-center\index.vue

## Notes
- The original `pullData()` method had a `catch (e) {}` block that was accidentally deleted during log removal, leaving only `try {...} finally {...}` which allows exceptions to propagate. This was corrected by reinstating the catch.
- All `try` blocks in the five pages now have appropriate `catch` or `finally` clauses to ensure syntactic correctness and prevent unhandled rejections.

## Task overview
Successfully cleaned all 11 console.log statements from the five pages and fixed the critical unhandled Promise rejection bug in index.vue's pullData method. Grep verification confirmed zero remaining console.log or debugger statements.
