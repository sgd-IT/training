---
title: "处置中心tags-view重复标签合并为单标签"
usage_scenario:
    - "排查顶部标签页出现同名编号重复标签"
    - "为某菜单页配置单标签合并行为"
    - "处理标签合并后query参数失效问题"
keywords:
    - "tags-view去重"
    - "SINGLE_TAB_PATHS"
    - "fullPath标签"
    - "_t时间戳"
    - "query watch"
source: "auto"
---

## Task description
- Core requirement: 用户截图显示顶部标签栏出现「火警处置」「火警处置 (2)」「火警处置 (3)」三个重复标签，要求排查原因并修复。
- Task background: yudao 框架 TagsView（src/layout/components/TagsView/src/TagsView.vue + src/store/modules/tagsView.ts）按 fullPath 去重，同 path 不同 query 视为不同标签并对同名标题加 (2)(3) 后缀；处置中心 /eiot/firewatch/alarm-order 存在三类带不同 query 的入口（?id=xxx、?today=1&orderStatus=unclosed、无 query），因此每次进入新增一个标签。

## Execution process
1. TagsView.vue addTags()：新增 SINGLE_TAB_PATHS 白名单（当前仅 /eiot/firewatch/alarm-order），命中时先用 tagsViewStore.getVisitedViews.filter(v => v.path === route.path && v.fullPath !== route.fullPath) 找出同 path 旧标签并逐个 delVisitedView，再 setSelectTag + addView，实现"同一菜单只保留一个标签"。
2. alarm-order/index.vue：标签合并后同页面换 query 不会重新 mounted，原 openAlarmFromQuery 只在 onMounted 执行会导致详情不弹；补 watch 监听 id/today/orderStatus 组合 key，变化时重建筛选并打开对应告警详情；clearTodayOnly 的刷新改由该 watch 承接，避免重复请求。
3. dashboard/index.vue handleAlarmClick 与 duty-screen/index.vue openAlarmOrder：跳转 query 增加 _t=String(Date.now()) 时间戳——hash 路由下重复点同一条告警时 URL 完全相同则无任何导航事件，_t 保证 query 必变、watch 必触发。
4. 验证：GetProblems 仅剩既有 warning；npx eslint 被 PowerShell 执行策略拦截后改用 node node_modules/eslint/bin/eslint.js 直调，四个改动文件零报错。

## Related files
- /src/layout/components/TagsView/src/TagsView.vue
- /src/views/eiot/firewatch/alarm-order/index.vue
- /src/views/eiot/firewatch/dashboard/index.vue
- /src/views/eiot/firewatch/duty-screen/index.vue

## Notes
- tagsView store 未持久化（persist: false），会话中已存在的重复标签刷新页面即自然消失，无需额外清理逻辑。
- 新标签 push 到数组末尾，合并后处置中心标签位置会跳到末尾，属可接受行为。
- 其他页面若要单标签，把 path 加入 SINGLE_TAB_PATHS 即可；但合并标签的前提是页面自身必须响应 query 变化（组件复用时 mounted 不再触发）。

## Task overview
已完成：TagsView 单标签白名单 + 页面侧 query watch + 跳转入口 _t 时间戳三层修复，处置中心不再产生 (2)(3) 重复标签且各入口的 id/today/orderStatus 参数仍然生效；ESLint 通过、无新增类型错误。Git 提交未执行，待用户确认。
