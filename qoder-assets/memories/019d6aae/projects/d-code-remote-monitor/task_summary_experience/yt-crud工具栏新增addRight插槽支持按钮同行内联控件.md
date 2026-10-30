---
title: "yt-crud工具栏新增addRight插槽支持按钮同行内联控件"
usage_scenario:
    - "列表页工具栏新增按钮旁放内联筛选下拉"
    - "为yt-crud公共组件新增命名插槽扩展位"
    - "空间管理页按所属企业过滤列表"
keywords:
    - "addRight插槽"
    - "yt-table-fun"
    - "yt-crud插槽透传"
    - "工具栏内联筛选下拉"
    - "所属企业筛选"
    - "tenantId即时过滤"
source: "auto"
---

## Task description
- Core requirement: 空间管理页需把"所属企业"筛选下拉放到"新增"按钮右侧同一行，而非占用整行的搜索卡片。
- Task background: 项目自定义列表组件 yt-crud 由 yt-table-search（整行搜索卡片）、yt-table-fun（工具栏，含新增/导出/批量删除按钮）、yt-table、yt-table-form 组成；原工具栏无预留内联控件扩展位。

## Execution process
1. src/components/common/yt-table-fun.vue：在工具栏左组新增按钮 el-col 之后增加命名插槽 `<slot name="addRight">`，作为纯增量扩展位（不影响既有页面）。
2. src/components/common/yt-crud.vue：向页面透传 `#addRight` 插槽。
3. 空间管理页 src/views/eiot/space/index.vue：移除 column 里的 search 项（原整行搜索卡片机制），改用手写 el-select 放入 #addRight 插槽；@change 触发 getData 按 tenantId 即时过滤，clearable 清空恢复全量，空串归一为 undefined 再传 getSpaceList(tenantId)，避免后端收到空串。
4. ESLint 验证三文件 0 errors（yt-crud 第3行有1条既有格式 warning，git diff 确认非本次引入，未扩大改动面）。

## Related files
- /anwei-fire-remote-monitor-vue/src/components/common/yt-table-fun.vue
- /anwei-fire-remote-monitor-vue/src/components/common/yt-crud.vue
- /anwei-fire-remote-monitor-vue/src/views/eiot/space/index.vue

## Notes
- el-select 直接作为 el-row（display:flex）内 col 后的 flex item，加 margin-left 控制与新增按钮间距，垂直居中依赖 el-row align="middle"。
- Vue3 事件名 camelize：模板 @save-fun 编译后与 emit('saveFun') 匹配（onSaveFun），连字符写法可消除 eslint 连字符 warning 且不破坏触发。

## Task overview
已完成：为公共组件补齐 addRight 插槽并在空间管理页用它承载"所属企业"筛选下拉，实现筛选控件与新增按钮同行内联，替代原整行搜索卡片；ESLint 通过。
