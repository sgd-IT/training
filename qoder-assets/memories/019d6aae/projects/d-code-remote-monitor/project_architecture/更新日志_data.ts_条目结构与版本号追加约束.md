---
title: "更新日志 data.ts 条目结构与版本号追加约束"
usage_scenario:
    - "向更新日志追加新版本条目"
    - "排查登录后更新提醒弹窗未出现原因"
    - "把git提交转写为业务可读日志条目"
keywords:
    - "CHANGELOG数组"
    - "items.type取值"
    - "CURRENT_VERSION取首元素"
    - "CHANGELOG_READ_VERSION"
    - "changelog data.ts"
source: "auto"
---

PC 管理台更新日志的数据源约束：src/views/system/changelog/data.ts 内 CHANGELOG 数组是唯一数据源，条目形状为 {version, date, title, items:[{type, text}]}，其中 type 的实际取值是 feature / improve / fix（页面用 CHANGE_TYPE_LABEL 映射成「新功能/优化/修复」彩色标签），并非早期记录的 feat/opt/fix。发版时只需在数组最前面插入一条，因为 CURRENT_VERSION = CHANGELOG[0]，更新日志页顶部概览、VersionUpdateDialog 登录弹窗以及缓存 key CHANGELOG_READ_VERSION 的已读比对都随之自动指向新版本，无需改其它文件。版本序列当前为 V1.6.0(2026-10-30) → V1.5.0(2026-10-28) → V1.4.0 → V1.3.0 → V1.2.0 → V1.1.0 → V1.0.0 → V0.9.0。同目录另有 index.vue 与 VersionUpdateDialog.vue，二者均只读该数组。条目文案一律写成用户可读的业务说法，不出现类名、中间件名、DDL 等技术术语；且若条目内容依赖尚未上线的后端改动，须提醒"只发前端会出现日志写了但功能没有"。
