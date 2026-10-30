---
title: "KeepAlive缓存页面的onMounted不重跑导致iframe型页面空白"
usage_scenario:
    - "PC端进入某页面后内容区一片空白"
    - "排查iframe容器页无内容问题"
    - "区分接口异常与组件未挂载"
keywords:
    - "KeepAlive"
    - "onMounted不执行"
    - "iframe空白"
    - "页面进入无内容"
    - "Vue3缓存页面"
source: "auto"
---

Bug class：PC 前端 KeepAlive 缓存页面，onMounted 不再执行导致页面级逻辑失效；两个子类症状——① iframe 型页面一片空白；② 轮询定时器在切路由后照跑不停。

Root cause（2026-10-27 / 2026-10-28 在 anwei-fire-remote-monitor-vue 实测）：src/views/infra/druid/index.vue 与 infra/server/index.vue 都是 `v-if="!loading"` 包 IFrame，loading 初值 true，靠 onMounted 里 `await ConfigApi.getConfigKey(...)` 的 finally 置 false。这两个页面被 KeepAlive 缓存，从别的页面切回来时组件不会重新挂载，onMounted 不再执行 → loading 永远 true → IFrame 根本不渲染，页面只剩一个空的 el-card__body。此时后端完全正常（curl 能取到 /druid/index.html 的 Druid 页面 HTML）。轮询侧同源：`useVisiblePolling` 只在 document.hidden 时停表、只在 onUnmounted 清表，而 KeepAlive 页面切走时 document.hidden 仍为 false、onUnmounted 也不触发 → 定时器照跑；叠加"每个页面各建各的表"，实测 `/alert/recentUnprocessedAlerts` 深夜（03:00~03:10）每分钟精确 7 次，3 天累积 2 万多次。

Fix pattern：这类"进入后空白"先用 F5 刷新页面验证——刷新后组件重新挂载，onMounted 跑通，iframe 立刻出现。若刷新后仍空，才去查接口与后端。判断依据是 evaluate_script 里 document.querySelectorAll('iframe').length 为 0，且 .v-content-wrap 的 innerHTML 只有 `<!--v-if-->` 空注释。轮询侧：页面级定时器要用 onActivated/onDeactivated 承载启停（KeepAlive 激活/失活），停表条件要覆盖路由离开与长时间无交互，不止 document.hidden；跨页面共享同一数据的轮询应收敛到一个调度器，避免"页面数 × 每页一条表"线性放大。

Reusable lesson：区分"接口失败"与"组件没挂载"的最快手段是看 DOM 有没有目标节点（iframe/表格/图表），而不是看接口返回；KeepAlive 缓存页面的 onMounted 只跑一次，页面级数据刷新与定时器启停都应改用 onActivated/onDeactivated；"每分钟精确 N 次"这类调用量可作为"当前有几张轮询表在跑"的直接诊断指标。适用边界：uni-app/vue3 PC 后台里所有 iframe 容器页与需要每次进入都刷新的列表页；不适用于首次进入即报错的场景。
