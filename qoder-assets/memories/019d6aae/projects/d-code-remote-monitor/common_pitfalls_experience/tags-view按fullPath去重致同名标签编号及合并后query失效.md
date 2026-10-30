---
title: "tags-view按fullPath去重致同名标签编号及合并后query失效"
usage_scenario:
    - "排查顶部标签页出现同名编号重复标签"
    - "合并单标签后query参数静默失效"
    - "设计跨页跳转传参的目标页响应机制"
keywords:
    - "fullPath判重"
    - "titleSuffix编号"
    - "组件复用不触发mounted"
    - "_t防重参数"
    - "addVisitedView"
source: "auto"
---

Bug 类：顶部标签栏出现「XX」「XX (2)」「XX (3)」同名重复编号标签；以及修复合并后"点告警不弹详情"的隐性回归。
根因：yudao TagsView 的 addVisitedView 以 fullPath 判重（store/modules/tagsView.ts），同 path 不同 query 被当作另一个标签，标题相同时由 titleSuffix 机制自动加 (2)(3) 后缀。处置中心 /eiot/firewatch/alarm-order 有多种入口 query（?id=xxx、?today=1&orderStatus=unclosed、无 query），每进一次多一个标签。进一步地，Vue Router 在同 path 仅 query 变化时复用组件实例、不再触发 onMounted——而该页 openAlarmFromQuery 只在 onMounted 执行，标签合并后从别处带新 id 跳入会静默失效；hash 路由下重复点同一条告警时 URL 完全相同则连导航事件都没有。
正确做法：①在 TagsView.vue addTags() 用 SINGLE_TAB_PATHS 白名单，进入命中路径前先 delVisitedView 掉同 path 异 fullPath 的旧标签再 addView；②页面侧必须补 watch 监听所消费的 query 组合 key，变化时重建状态；③跳转入口给 query 附加 _t=Date.now() 时间戳保证 URL 必变、watch 必触发。
可复用教训：不要只改 tags-view 去重就收工，因为标签合并会把"每次进入都重新 mounted"的隐含前提打破，依赖 mounted 消费 query 的页面会静默失效；必须同步把 query 消费改为 watch 驱动并给跳转加防重参数。适用边界：本仓库 layout/components/TagsView 与所有靠 route.query 传参的一次性跳转目标页（如 alarm-order）；不适用于 query 常驻且页面本身持续读取 query 的场景。另注意 tagsView store 未持久化，历史脏标签刷新即消。
