---
title: "大屏数据通道结构事实：WS与双轮询并行、alarm-screen无WS、valve/command属外部服务"
usage_scenario:
    - "评估大屏轮询与推送改造的影响面"
    - "定位高频接口调用来源归属"
    - "区分WS提示通道与轮询数据通道职责"
keywords:
    - "duty-screen三通道"
    - "recentUnprocessedAlerts十秒轮询"
    - "alarm-screen无WS"
    - "valve-command外部服务"
    - "PermitAll拉命令通道"
source: "auto"
---

消防远程值守大屏侧的三条结构性事实（2026-10 经前端 duty-screen/alarm-screen 代码 + 后端 RemoteConsoleController + 生产访问日志三方核验）：

1. **duty-screen（值守大屏）是"WS + 双轮询"三通道并行**：`useWebSocket(wsServer, {autoReconnect, heartbeat})` 连 `/infra/ws`；另有 `setInterval(fetchRecentAlerts, 10000)` 每 10 秒拉 `/alert/recentUnprocessedAlerts`（当天全部未处置告警），以及 `setInterval(loadData, 30000)` 每 30 秒串行打 4 个接口（`dashboard/statistics`、`dashboard/realtime-alarms`、`property/page`、`duty-schedule/current-duty`）。WS 收到 `firewatch-alert-new` 时只触发 `scheduleRefreshTodayAlarm()`（刷新今日警情卡片+弹窗），列表数据仍靠 10 秒轮询兜底——即 WS 管提示、轮询管数据，二者不是二选一。单块屏每 30 秒约发 7 个请求。

2. **alarm-screen 没有 WebSocket**，是纯 `useVisiblePolling(loadData, 30000)` 轮询；有 WS 的是 duty-screen 与 monitoring-center。判断"某大屏页面是否有实时推送"必须按页面分别确认，不能由"系统有告警推送"推断每个页面都用了它。

3. **`/eiot/remote-console/valve/command/{deviceId}` 不是前端页面调用**：该接口在 PC/移动前端仓库均搜不到调用点，后端标注 `@PermitAll` 且注释写明"外部空开控制服务（ValveCommandTask）轮询拉取，免登录；配合 platform.tenant.ignore-urls 忽略租户校验"。其特征为 user_id=0、UA 自报 Chrome/65（客户端默认值非真实浏览器）、每小时恒定约 447 次（≈8 秒一次）7×24 不断、单次 duration≈0 秒。推论：它贡献"请求条数"但不贡献服务器压力，治理优先级应低于大屏聚合类接口；要改其频率需动 ValveCommandTask 或外部程序配置，而非前端。
