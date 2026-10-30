---
title: "大屏实时告警口径取证与无offset接口的上拉加载更多实现"
usage_scenario:
    - "解释大屏实时告警为何只显示当天数据"
    - "为固定高度滚动列表实现加载更多"
    - "排查某日告警数量异常暴涨来源"
keywords:
    - "大屏实时告警日期口径"
    - "selectTodayUnclosedAlerts"
    - "上拉加载更多"
    - "limit无offset整体替换"
    - "定时刷新共存"
source: "auto"
---

## Task description
- Core requirement: 三连问 —— ①大屏「实时告警」显示的是什么日期的数据；②今天真有那么多条告警吗；③实时告警最多展示多少条，并要求加上"上拉加载更多"。
- Task background: duty-screen/index.vue 的实时告警列表数据来自 /eiot/firewatch/dashboard/statistics 的 realtimeAlarms，后端已由 selectTodayUnclosedAlerts 取代 selectLatestAlerts；列表此前已取消自动滚动、改为纯手动滚轮。

## Execution process
1. 日期口径取证：AlertRecordMapper.selectTodayUnclosedAlerts 三个条件叠加 —— DATE(FROM_UNIXTIME(alert_time/1000)) = CURDATE()（数据库服务器时区当天）、handle_status IS NULL OR < 3（只含未闭环）、ORDER BY alert_time DESC LIMIT #{limit}；前端 filterTodayAlarms 再按浏览器本地时区过滤一遍。实测该库 time_zone=SYSTEM 即 +08:00。
2. 条数上限取证：statistics 固定 buildRealtimeAlarms(10) → 首屏 10 条；仅 statistics 失败降级调 /realtime-alarms 时前端传 limit=15；Controller 默认值 10、无 offset 参数、无上限校验。视觉上面板是 .card-content overflow-y:auto 且滚动条隐藏，一屏约 5~6 条，无自动轮播。
3. 数据异常诊断（MCP 只读查询）：今日 1424 条中 device_id=7「展厅远程控制终端」的"消防主机故障报警"占 1422 条，间隔实测 6~7 秒一条，从 11:18 持续到 14:49（小时分布 11/475/512/427），内容完全一致且 handle_status=0；对比 10-19 仅 4 条、10-20 19 条基线，判定为设备/模拟器重复上报造成的告警风暴，且该链路无同设备同事件时间窗去重（摄像头链路的 alert_image 哈希去重不覆盖）。
4. 结论澄清两点易误判：标题红点是今日未闭环总数（实测 1414+），列表最多渲染 10 条，两者必然不等；浏览器与数据库跨时区会导致二次过滤把记录滤空。
5. duty-screen/index.vue 实现上拉加载更多：因后端只有 limit 无 offset，采用"放大 limit 重拉后整体替换"——新增 ALARM_PAGE_STEP=10 / ALARM_MAX_LIMIT=50 常量与 alarmLoadLimit/alarmLoadingMore/alarmNoMore 三态状态（上限常量防长驻页无限拉取拖垮后端，三态标志是提示文案与并发防抖的唯一依据）；容器 @scroll.passive 距底 24px 触发 loadMoreAlarms；底部三态提示（上拉加载更多/加载中…/已显示全部）且提示本身可点击兜底（不产生滚动条场景的入口）。
6. 边界处理：改造前必须先确认接口入参能力（有无 offset/游标），决定走增量追加还是整体替换；并盘点同页面所有写入该列表变量的位置（首屏赋值、降级分支、定时刷新、切换维度清空、兜底 mock），遗漏任一处会造成新状态与旧赋值互相覆盖。statistics 分支按 list.length < ALARM_PAGE_STEP 置 alarmNoMore，避免不足一屏时出现永不触发的提示；已上拉过时 30 秒定时刷新改走 fetchRealtimeAlarms() 按当前 limit 重拉后整体替换，防止列表缩回 10 条；降级分支把 alarmLoadLimit 抬到 15 使后续从 25 开始；超管切换租户清空列表并重置 limit/noMore。
7. 验证：两次 GetProblems 均只剩既有 DevelopingTag 未使用警告，零新增错误；未跑浏览器实测。

## Related files
- /anwei-fire-remote-monitor-vue/src/views/eiot/firewatch/duty-screen/index.vue

## Notes
- 分页改造前先确认后端游标能力：本例只有 limit 没有 offset，硬做增量追加会产生重复条目，"放大 limit 整体替换"是零后端改动的正确形态。
- 只改加载函数不改整页定时刷新 causes 用户上拉看到的条数在下一个刷新周期被截回首屏数量，必须在刷新分支按当前上限重拉。
- 用"返回空数组"作为到底判据 causes 首屏不满一页时提示永不触发或提前误判，必须用返回条数是否小于请求条数判定。
- 大屏这类长期不关闭的页面，任何"加载更多"都必须与整页定时刷新共存，否则轮询会把已加载区间截断回去；若页面有"切换租户/维度即清空列表"逻辑，须一并重置上限与到底标志。
- 统计徽标与明细列表条数不同源是设计使然而非缺陷，回答用户时应主动区分，避免误判为数据错误。
- 展示层"同设备同事件合并"仍是待决策项，未实施。

## Task overview
已完成：大屏实时告警口径讲清（服务器当天 + 未闭环 + 最新 10 条，降级 15 条）；实证今日告警 99.8% 来自单台设备每 6~7 秒重复上报同一条故障；上拉加载更多落地（每次 +10、上限 50、到底判定、可点击兜底、切换租户重置），类型检查零新增问题，改动未提交 Git。
[Memory source: session 7b6b1afc-1b4f-4bf5-b9e1-5b24fefb3e57. Locate via Glob (path: C:\Users\Administrator, pattern: **/conversation-history/7b6b1afc/7b6b1afc.jsonl), then Read/Grep it.]
[This memory is a historical execution record and may be outdated — verify against current code before relying on it; re-verify any appended corrections/caution section first.]
