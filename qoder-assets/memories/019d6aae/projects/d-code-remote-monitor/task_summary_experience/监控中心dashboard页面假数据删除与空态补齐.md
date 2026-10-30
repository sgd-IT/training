---
title: "监控中心dashboard页面假数据删除与空态补齐"
usage_scenario:
    - "删除或改造前端页面硬编码兜底假数据"
    - "区分监控中心两套同名页面的归属"
    - "排查轮询定时器覆盖用户筛选条件"
keywords:
    - "监控中心假数据"
    - "dashboard页面"
    - "ECharts空态"
    - "轮询天数覆盖"
    - "前后端接口盘点"
source: "auto"
---

## Task description
- Core requirement：删除 PC 端「监控中心」页面（浅色卡片版）上所有前端硬编码假数据，让页面完全走真实接口。
- Task background：anwei-fire-remote-monitor-vue 的 src/views/eiot/firewatch/dashboard/index.vue（936 行）里有 loadMockData() 兜底，铺出设备总数 20/在线 0/报警 1/故障 1/未激活 16、今日告警 5/待处置 3/处置率 20%、三条「烟感探测器-WJ-0x」告警、设备类型分布与风险单位分布；告警趋势另用 generateTrendMockData() 以 Math.random() 随机造折线。同目录另有 monitoring-center/index.vue（深蓝带地图大屏），名字极易混淆但本身无 mock 兜底。

## Execution process
1. 页面归属判定：截图含「快捷操作」卡 + 三横向按钮 + 浅色 #eef5f5 卡片布局，为 dashboard/index.vue 独有；monitoring-center 经 Grep 确认背景为 rgba(14,44,78,..) 深色且有 AMap.Marker，排除。remaining.ts 里 dashboard 静态路由已被注释，页面靠后端动态菜单挂载。
2. 后端能力盘点后再动手：Grep 确认 FireDashboardController 存在 device-status / today-alarm / realtime-alarms / alarm-trend / device-category / risk-distribution 六个真实实现（权限 eiot:firewatch:dashboard:query），故可直接摘除兜底而不产生空板回归。
3. dashboard/index.vue：整体删除 loadMockData()（原 279-358 行）与 generateTrendMockData()（原 360-374 行）。
4. onMounted 改为「初始化图表 → 按空数据画骨架 → fetchDashboardData()」，不再先铺假数据；30 秒轮询保持不变。
5. handleTrendDaysChange 去掉 mock 兜底分支，只认接口返回，取不到即清空该图。
6. 空态补齐：三个图表在无数据时用 ECharts graphic 文本显示「暂无数据」（抽成常量 NO_DATA_GRAPHIC），实时告警列表沿用原 el-empty「暂无告警」。
7. 顺手修真实数据被覆盖的 bug：fetchDashboardData 轮询里 getAlarmTrend(7) 写死 7 天，用户切到近 30 天后 30 秒被打回，改为 getAlarmTrend(trendDays.value)。
8. 验证：Grep 全文件 mock / Math.random / loadMockData / generateTrend 命中 0；GetProblems 仅历史遗留 alarmScrollRef 未读取告警；node 直调 vue-tsc --noEmit 后按 firewatch/dashboard 路径过滤命中 0（仓库存量约 420 条类型错误与本次无关）。

## Related files
- anwei-fire-remote-monitor-vue/src/views/eiot/firewatch/dashboard/index.vue

## Notes
- 「监控中心」在本仓库有两套页面（dashboard 浅色卡片版与 monitoring-center 深色地图版），改前必须靠布局特征与主题色二选一定位，不能只按菜单名猜文件。
- 删 mock 后赋值写法 `d.deviceStatus || deviceStatus.value` 会让接口失败时保留上一次数据而非清零，属预期行为，需在交付说明里点明避免被当成 bug。
- MCP mysql 实例（anwei_fire_remote_monitor_test）本轮 CallMcpTool 报 not found MCPHost，无法查 system_menu 反证菜单指向，只能退回代码证据判断。
- 同类假数据仍残留在 large-screen/index.vue（安为科技示范单位、烟感探测器-WJ-01）、alarm-record/index.vue、enterprise/index.vue 以及 data-center/mock.ts，均不在本次范围。

## Task overview
已完成：dashboard/index.vue 的假数据全量清除，页面改为纯真实接口驱动并补齐三图空态，同时修正轮询打回趋势天数的缺陷；vue-tsc 与 Grep 双重校验零残留、零新增类型错误。改动停留在工作区，未提交也未推送。账号缺 eiot:firewatch:dashboard:query 权限时会显示 0 与「暂无数据」，属预期表现。
