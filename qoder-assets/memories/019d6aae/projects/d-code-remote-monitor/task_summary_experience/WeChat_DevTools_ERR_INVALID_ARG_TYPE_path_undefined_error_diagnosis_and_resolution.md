---
title: "WeChat DevTools ERR_INVALID_ARG_TYPE path undefined error diagnosis and resolution"
usage_scenario:
    - "Diagnosing WeChat DevTools startup errors related to path resolution"
    - "Resolving 'ERR_INVALID_ARG_TYPE' errors when importing projects into WeChat DevTools"
keywords:
    - "WeChat DevTools"
    - "ERR_INVALID_ARG_TYPE"
    - "path undefined"
    - "mp-weixin"
    - "uni-app source root"
source: "auto"
---

## Task description
- Core requirement: diagnose and resolve the WeChat DevTools error 'TypeError [ERR_INVALID_ARG_TYPE]: The "path" argument must be of type string. Received undefined'
- Task background: This error occurs when WeChat DevTools attempts to resolve the mini-program code root path but receives undefined; common causes include opening the wrong directory (uni-app source root instead of compiled mp-weixin output) or missing miniprogramRoot configuration in project.config.json

## Execution process
1. Analyzed the error message and confirmed it is a Node.js path resolution failure in WeChat DevTools
2. Checked project.config.json in the source root and confirmed it lacks miniprogramRoot/srcMiniprogramRoot fields
3. Verified the compiled output directory unpackage/dist/dev/mp-weixin contains app.json and a valid project.config.json
4. Concluded the root cause is opening the uni-app source root in WeChat DevTools instead of the compiled mp-weixin directory
5. Provided two solutions: (a) use HBuilderX 'Run to Mini-Program Simulator' to auto-launch the correct directory, or (b) manually re-import the mp-weixin directory in WeChat DevTools

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor-mobile\project.config.json
- d:\code\remote-monitor\anwei-fire-remote-monitor-mobile\unpackage\dist\dev\mp-weixin\project.config.json
- d:\code\remote-monitor\anwei-fire-remote-monitor-mobile\unpackage\dist\dev\mp-weixin\app.json

## Notes
- This is not a code bug — no file modifications were needed. The fix is purely operational: importing the correct compiled directory into WeChat DevTools.

## Notes
- 【更正】Task background 中把「根目录 project.config.json 缺少 miniprogramRoot」列为可能原因是错的：根目录不应添加 miniprogramRoot，HBuilderX 会把根目录配置同步进编译产物，产物内出现该字段会让开发者工具再向下钻一层找 app.json 而报「未找到 app.json」。唯一正确处置始终是让开发者工具直接打开编译产物目录（上传发布用 unpackage/dist/build/mp-weixin）。

## Task overview
Successfully diagnosed and resolved the WeChat DevTools 'path undefined' error by identifying the incorrect import directory as the root cause and providing actionable solutions to open the compiled mp-weixin output directory instead of the uni-app source root.
