---
title: "uni-icons 不支持 grid 图标名"
usage_scenario:
    - "在TabBar中配置uni-icons图标时选择合适的图标名"
    - "优化TabBar图标语义以提升用户体验"
keywords:
    - "uni-icons"
    - "TabBar"
    - "图标优化"
    - "图标存在性核验"
    - "uniicons_file_vue"
source: "auto"
---

uni-icons 组件不支持 `grid` 图标名，该名称未在 `uniicons_file_vue.js` 或 `uniicons_file.ts` 的 font_class 列表中定义。可用图标包括：`compose`、`list`、`checkbox`、`settings` 等（来源：Read/Grep 工具读取 `uniicons_file_vue.js` 文件）。

图标存在性核验方法：uni-icons 的图标名定义全部在组件目录下的 `uniicons_file_vue.js`（font_class 列表）中，组件文件 `uni-icons.vue` 本身不含图标名清单；用 Grep 直接搜 `uni-icons.vue` 会返回 0 匹配并导致"图标不存在"的误判，必须检索 `uniicons_file_vue.js`。已核验存在的图标示例：`locked-filled`、`info-filled`、`refresh`。
