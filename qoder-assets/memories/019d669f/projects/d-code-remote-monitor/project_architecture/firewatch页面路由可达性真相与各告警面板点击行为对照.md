---
title: "firewatch页面路由可达性真相与各告警面板点击行为对照"
usage_scenario:
    - "定位某 firewatch 页面的真实跳转入口"
    - "回归测试告警处置功能前确认测试入口"
    - "排查大屏告警点击无响应或数据为空"
keywords:
    - "remaining.ts静态注册"
    - "alarm-order跳转入口"
    - "大屏告警面板行为"
    - "不存在的后端接口"
    - "firewatch/duty隐藏路由"
source: "auto"
---

anwei-fire-remote-monitor-vue 中 firewatch 模块的页面可达性与告警面板行为（本轮代码走查逐项验证）：

1. 页面路由全部在 src/router/modules/remaining.ts 静态注册，不走后端菜单；大屏跳转目标页多为 hidden: true 的隐藏路由，左侧菜单不可见但直接输 URL 可进。部分历史页面的注册块被整块注释（如 views/eiot/firewatch/dashboard/index.vue 的 FireWatchDashboard），其代码里残留的跳转实为死链。

2. 已验证的具体路径：值守大屏值班管理页 /eiot/firewatch/duty；火警处置（告警工单）页 /eiot/firewatch/alarm-order，name 为 FireWatchAlarmOrder，hidden: true，注释标明"大屏/值守大屏跳转入口，静态注册保证路由可达"。

3. 火警处置页 /eiot/firewatch/alarm-order 的真实跳转点只有 3 个页面 4 处：monitoring-center 快捷操作「火警处置」、alarm-screen 左侧待办任务卡片"更多 >"、dashboard 的实时告警项点击与快捷操作（后两处随 dashboard 路由注释而不可达）。各大屏右侧实时告警点击均不跳该页：duty-screen 开弹窗且"去处置"跳 /remote-console 或 /video-surveillance；monitoring-center 开详情对话框；large-screen 的实时告警卡片实际在左侧面板且纯展示无点击事件；alarm-screen 顶部"详情 »"跳报警记录页。

4. 存在两处前端调用了后端不存在的接口的反直觉事实：large-screen 请求 /eiot/firewatch/alarm-order/list、alarm-record 请求 /alarm-order/export，FireAlarmOrderController 均无对应方法（只有 /export-pdf）；前者因走静默请求长期无声失败，后者有前端 CSV 兜底，因此功能看似正常。

5. 排查"某页面被谁引用"须分三层且不可合并：组件 import（路由注册）、带引号的运行时路径字符串 '/xxx/yyy'（真实跳转点）、api/*/xxx.api.ts 中的同名接口路径常量（噪音，如 firewatch.api.ts 有 13 个 alarmOrder 相关常量）。只搜裸子串会把接口常量误判为引用，只看 import 会漏掉全部入口。
