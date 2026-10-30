---
title: "移动端告警详情页header样式对齐与小程序热编译修复"
usage_scenario:
    - "When aligning UI header styles across multiple mobile pages"
    - "When diagnosing and fixing WeChat Mini-Program hot-recompile failures"
keywords:
    - "header alignment"
    - "margin-left"
    - "hot-recompile"
    - "WeChat DevTools"
    - "project.config.json"
source: "auto"
---

## Task description
- Core requirement: Align alarm-detail page's NavBar header styling with alarm-center and other pages; fix WeChat Mini-Program hot-recompile failure
- Task background: alarm-detail had inconsistent margin-left (15px vs 35px) and padding-top; hot-recompile failed due to service port disabled and invalid JSON comment in project.config.json

## Execution process
1. Confirmed alarm-center, duty/index.vue, messages/index.vue all use ::v-deep .aw-navbar__title-wrap.is-center { margin-left: 35px }
2. Fixed project.config.json by removing JavaScript-style // comment that broke JSON parsing
3. Verified WeChat DevTools service port must be enabled manually in Settings → Security Settings → Service Port
4. User requested alarm-detail title shift left by 15px → updated margin-left from 35px to 20px

## Related files
- /pages/alarm-detail/index.vue
- /pages/alarm-center/index.vue
- /pages/duty/index.vue
- /pages/messages/index.vue
- /project.config.json

## Notes
- None

## Task overview
Successfully aligned alarm-detail header with other pages (margin-left:20px); fixed hot-recompile failure by correcting project.config.json and enabling WeChat DevTools service port.
