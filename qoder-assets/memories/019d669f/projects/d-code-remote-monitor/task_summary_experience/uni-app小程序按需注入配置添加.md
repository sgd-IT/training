---
title: "uni-app小程序按需注入配置添加"
usage_scenario:
    - "When enabling lazy code loading for uni-app WeChat mini-programs"
    - "When optimizing mini-program startup performance and runtime memory"
keywords:
    - "lazyCodeLoading"
    - "按需注入"
    - "uni-app"
    - "WeChat mini-program"
    - "manifest.json"
source: "auto"
---

## Task description
- Core requirement: enable lazy code loading ("按需注入") for the WeChat mini-program to reduce startup time and memory usage
- Task background: the project is a uni-app based mini-program; lazyCodeLoading must be added to manifest.json → mp-weixin (source of truth) and optionally patched into the compiled unpackage/dist/dev/mp-weixin/app.json for immediate verification in WeChat DevTools

## Execution process
1. manifest.json: add "lazyCodeLoading": "requiredComponents" to the mp-weixin section
2. unpackage/dist/dev/mp-weixin/app.json: add "lazyCodeLoading": "requiredComponents" to the root level of the compiled app.json file

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor-mobile\manifest.json
- d:\code\remote-monitor\anwei-fire-remote-monitor-mobile\unpackage\dist\dev\mp-weixin\app.json

## Notes
- The manifest.json uses JSONC format (comments allowed), so the "JSON does not allow comments" error is a false positive from standard JSON parsers and is safe to ignore.
- No global usingComponents were found in pages.json, and easycom handles uni_modules on-demand, so no cleanup was needed.

## Task overview
Successfully enabled lazy code loading for the WeChat mini-program: added the required configuration to both the source manifest.json and the compiled app.json; the feature is now active and can be verified by refreshing the WeChat Developer Tools.

## Notes
- manifest.json使用JSONC格式（允许注释）：原记忆中'JSON does not allow comments'为误判；该文件格式本身支持注释，无需忽略或规避。
