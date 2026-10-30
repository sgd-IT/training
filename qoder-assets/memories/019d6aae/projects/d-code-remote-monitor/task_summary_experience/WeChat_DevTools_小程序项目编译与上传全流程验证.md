---
title: "WeChat DevTools 小程序项目编译与上传全流程验证"
usage_scenario:
    - "Diagnosing and fixing WeChat DevTools project import errors"
    - "Verifying production package compilation and upload for uni-app projects"
    - "Resolving miniprogramRoot synchronization issues between HBuilderX and WeChat DevTools"
keywords:
    - "WeChat DevTools"
    - "HBuilderX"
    - "miniprogramRoot"
    - "app.json"
    - "app.js"
    - "CLI upload"
source: "auto"
---

## Task description
- Core requirement: verify that the anwei-fire-remote-monitor-mobile project compiles and uploads successfully to WeChat DevTools without app.json or path-related errors
- Task background: previous errors included 'ERR_INVALID_ARG_TYPE: path undefined' (caused by opening source root) and 'app.js file not found' (caused by HBuilderX syncing miniprogramRoot into build output); this task resolves both and confirms end-to-end production package upload

## Execution process
1. Verified initial `cli open` succeeded but IDE showed compile error due to `miniprogramRoot` in build directory's project.config.json
2. Identified root cause: HBuilderX synchronizes root project.config.json (including manually added `miniprogramRoot`) into unpackage/dist/build/mp-weixin/project.config.json
3. Removed `miniprogramRoot` from build directory's project.config.json and confirmed `app.js` and `app.json` exist at correct location
4. Ran `cli preview` to force full compilation; succeeded with QR code generation
5. Used HBuilderX '发行 → 小程序-微信' to rebuild and auto-open build directory in DevTools
6. Closed old source-root project window via `cli close`, re-opened correct build directory, and ran `cli upload --v 1.0.0`
7. Upload succeeded: 602 KB package, version 1.0.0, description '安为消防移动端首版'

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor-mobile\project.config.json
- d:\code\remote-monitor\anwei-fire-remote-monitor-mobile\unpackage\dist\build\mp-weixin\project.config.json
- d:\code\remote-monitor\anwei-fire-remote-monitor-mobile\unpackage\dist\build\mp-weixin\app.js
- d:\code\remote-monitor\anwei-fire-remote-monitor-mobile\unpackage\dist\build\mp-weixin\app.json

## Notes
- The core pitfall is HBuilderX syncing `miniprogramRoot` from root config into build output, causing WeChat DevTools to search one level deeper for app.json
- Always verify the project path in DevTools shows `...\unpackage\dist\build\mp-weixin` before uploading
- Never add `miniprogramRoot` to the root project.config.json — it is only for HBuilderX's internal use, not for DevTools

## Task overview
Successfully verified and completed the end-to-end WeChat DevTools compilation and upload flow: diagnosed and fixed both ERR_INVALID_ARG_TYPE and app.js not found errors; confirmed production package builds correctly; uploaded version 1.0.0 via CLI with no errors.

## Notes
- 【补充 2026-10】后续发布任务再次复用本流程并验证有效：上传前用 grep 检查产物 utils/request.js 的 BASE_URL（fire-remote-admin.anxunwang.cn 为生产包，192.168.250.130 为开发包）；若报 800059 "app.js file not found"，原因是开发者工具窗口打开的是源码根目录，处置是用 cli.bat open --project 直接打开 build 产物目录并关闭源码根窗口，而不是往根目录加回 miniprogramRoot。
