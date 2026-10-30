---
title: "大屏待处置计数与实时告警条数对齐及Toast提示落地"
usage_scenario:
    - "排查大屏待处置计数与实时告警条数不一致"
    - "修复待处置计数统计判据字段"
    - "验证处置动作对 pendingCount 的影响"
keywords:
    - "待处置计数"
    - "read_flg判据差异"
    - "handle_status未闭环"
    - "pending-toast定位"
    - "overflow裁切absolute"
    - "通知匣removeItem"
source: "auto"
---

## Task description
- Core requirement: 值守大屏（duty-screen）三项联动改造 —— ①注释掉「数据中心」「运维工作台」两个功能菜单入口；②在「待处理」格子位置为二三级报警增加 Toast 提示；③消除「待处置显示 2 条、实时告警显示 4/5 条」的数量不一致。
- Task background: 待处置计数来自后端 buildTodayAlarm() 的 countTodayUnreadAlerts/countTodayReadAlerts，实时告警列表来自同 Mapper 的 selectLatestAlerts，三者同源 eiot_alert_record 但过滤判据不同；通知匣（alarm-notice）条目点击后由 openItem 内部 removeItem 立即移除。

## Execution process
1. duty-screen/index.vue 功能菜单：将 data-center、ops-workbench 两个 el-dropdown-item 用 HTML 注释禁用，并同步注释 navRoutes 中对应两条路由映射（模板入口与 JS 路由表必须成对注释，否则残留死路由）。
2. AlertRecordMapper.java 根因修复：countTodayUnreadAlerts 原判据 `read_flg = 0 OR read_flg IS NULL`、selectLatestAlerts 完全不过滤 read_flg → 被标记已读的记录从 pendingCount 消失却仍在实时列表。改为未闭环判据 `handle_status IS NULL OR handle_status < 3`（待处置）与 `handle_status >= 3`（已处置），与 selectActiveAlert / selectRecentUnprocessedAlerts 全代码统一口径，保证 totalAlarms = pendingCount + handledCount。mvn compile -pl module-eiot/module-eiot-biz 通过。
3. alarm-notice/index.vue：openItem 删除 removeItem(item) 调用，只 emit('open')，避免"点完通知匣归 0 而后端仍 5 条未处置"的体感割裂；条目仅在用户点 ✕ 忽略或页面刷新时消失。
4. duty-screen Toast 定位：初版把 .pending-toast 放在 .stat-cell.pending 内用 position:absolute，实测不显示 —— 祖先 .card-content 的 overflow-y:auto 会裁切 absolute 子元素。最终改为 Teleport to="body" + position:fixed，用 pendingCellRef.getBoundingClientRect() 计算 left/top（left 取格子中心，CSS transform: translateX(-50%) 水平居中），top 优先放格子上方、空间不足翻到下方。
5. Toast 触发双入口：fetchRecentAlerts 轮询回调（整批一次）与 firewatch-alert-new WS 回调（每条一次）均调 flashPendingToast()；重复抑制靠 alertShownIds 命中判断 + pendingToastTimer 防抖（格子闪 1.2s、Toast 3s 自动消失）。
6. vue-tsc --noEmit 验证：两个改动文件零新增错误（ExitCode 1 为仓库既有基线错误）。

## Related files
- /anwei-fire-remote-monitor-vue/src/views/eiot/firewatch/duty-screen/index.vue
- /anwei-fire-remote-monitor-vue/src/views/eiot/firewatch/alarm-notice/index.vue
- /anwei-fire-remote-monitor/module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/dal/mysql/AlertRecordMapper.java

## Notes
- 同一张表派生的统计数字与明细列表若判据字段不同源（一个用 read_flg、一个无过滤），必然出现"计数少、列表多"的对不上；新增统计口径应直接复用既有未闭环判据而非自选字段。
- Vue scoped style 不会给 Teleport 到 body 的内容加 data-v 哈希属性，因此锚定类浮层一律走内联 :style，不能依赖 scoped CSS 提供定位。
- 大屏卡片内的 absolute 浮层会被父级 overflow-y:auto 裁切不可见，需挂 body 层用 fixed 定位。
- 最后一轮用户提出「同设备同空间 5 分钟去重」的想法：通知匣 push() 已有 MERGE_WINDOW = 5*60*1000 的同设备同名合并计数 ×N；agent 建议待处置计数保持不做事件级合并（应与库内真实记录数一致），如需减少视觉噪音应在实时告警列表做展示层去重。该方向尚未拍板实施。

## Task overview
已完成：两入口注释、pendingCount/handledCount 判据改 handle_status（编译通过）、通知匣点击不再移除条目、Toast 经 Teleport+fixed 脱离 overflow 裁切并在轮询与 WS 双通道触发（重复抑制到位），前端类型检查零新增错误。改动均未提交 Git。「同设备同空间 5 分钟去重是否扩展到实时告警列表/待处置计数」为待用户决策事项。

## 勘误（后续会话变更，以最新为准）
- 本条第 3、5 步涉及的大屏「待处置 N」通知匣（AlarmNotice 组件）已在后续需求中被整体删除：duty-screen/index.vue 与 monitoring-center/index.vue 均已移除模板中的 <AlarmNotice> 浮层及 alarmNoticeRef、alertShownIds、onNoticeOpen/handleNoticeOpen，并把轮询与 WS 两条通道收敛为「仅一级告警入队全屏弹窗，二三级不再进通知匣」。因此「通知匣点击不移除条目」「alertShownIds 去重」等结论现已失效，仅作为历史记录保留。
- 仍然有效的部分：AlertRecordMapper 的 handle_status 未闭环判据口径、pending-toast 的 Teleport+fixed 定位配方（二三级告警现在只在「待处理」格子闪 Toast）、以及功能菜单注释与实时列表去重待决策事项。
- alarm-notice/index.vue 文件本身未被删除（isLightAlarmLevel 仍被两屏引用做一级/二三级分流），但组件模板与状态逻辑已成无人使用的死代码。
