---
title: "anwei-fireworks-retail-admin 前端对接实施规划任务执行归档"
usage_scenario:
    - "当需要为一个已部分实现的后端模块制定前端对接实施计划时"
    - "当发现前端页面路径与菜单 SQL 不一致且菜单 SQL 尚未执行时"
keywords:
    - "前端对接"
    - "实施计划"
    - "优先级规划"
    - "anwei-fireworks-retail-admin"
source: "auto"
---

## Task description
- Core requirement: 为 anwei-fireworks-retail-admin 前端项目制定后端 fireworks 模块的对接实施计划，按优先级分阶段交付
- Task background: 后端已实现 18 个 /fireworks/* Controller（含 homepage、alarm、device、video 等），前端已有部分 api/views（scene/stock/alarm/device），但存在目录路径不一致、缺失 API、菜单 SQL 未执行等问题；需基于真实代码结构生成可落地的方案

## Execution process
1. 并行探索：列出 ruoyi-fireworks 模块结构、frontend src/api 和 src/views 目录、读取 .qoder/rules/00-project-overview.md
2. 验证 API 表面：grep @RequestMapping 获取所有 /fireworks/* 路由，确认 VideoController 对接 Dahua ICC 平台，VoiceBroadcastLogController 仅为日志存储
3. 核对前端现状：确认 api/fireworks/scene 和 api/fireworks/stock 已存在，api/fireworks/broadcast 缺失导致 views/fireworks/broadcast/index.vue 编译报错；views/device 和 views/alarm 在顶层而非 fireworks/ 子目录
4. 分析菜单 SQL：fireworks_menu.sql 定义了 fireworks/device/index 等组件路径，但未执行；权限字符串与现有页面不一致（如 fireworks:broadcast:* vs fireworks:voice:*）
5. 确认技术栈事实：echarts@6.1.0 已安装；RuoYi-Vue-Plus 使用动态菜单；报警处置闭环接口（start/submit/false-alarm）在 AlarmRecordController 中存在但前端未对接
6. 提出并验证 P0-P3 实施步骤：P0 修编译坏点（删 broadcast 页）、建首页看板、接报警处置；P1 补进出货+超量检测；P2 补零售店/人员/配置；P3 补风险预警/视频/区域；同步提出 4 个关键问题请用户确认
7. 用户确认：菜单 SQL 未执行；语音播报无需前端对接；视频页只做流地址配置列表；首页图表用 echarts
8. 更新方案：调整菜单 SQL 组件路径以匹配现有 views/ 目录；删除 voice 菜单；补 alarm disposal 权限；明确视频页采用“设备列表+取流URL+新标签页打开”方案

## Related files
- d:\code\retail\anwei-fireworks-retail\.qoder\rules\00-project-overview.md
- d:\code\retail\anwei-fireworks-retail\script\sql\fireworks_menu.sql
- d:\code\retail\anwei-fireworks-retail\ruoyi-modules\ruoyi-fireworks\src\main\java\org\dromara\fireworks\controller\VideoController.java
- d:\code\retail\anwei-fireworks-retail\ruoyi-modules\ruoyi-fireworks\src\main\java\org\dromara\fireworks\controller\VoiceBroadcastLogController.java
- d:\code\retail\anwei-fireworks-retail-admin\src\views\index.vue
- d:\code\retail\anwei-fireworks-retail-admin\src\views\fireworks\broadcast\index.vue
- d:\code\retail\anwei-fireworks-retail-admin\package.json

## Notes
- None

## Task overview
Successfully completed the frontend integration planning task: verified the full backend API surface (/fireworks/*), mapped frontend coverage gaps (missing broadcast API, path mismatch), confirmed technical facts (echarts version, menu dynamic loading), proposed a prioritized P0-P3 implementation plan with concrete file-level steps, and obtained user confirmation on key decisions (SQL unexecuted, broadcast deletion, video scope, echarts usage). The plan is ready for execution.
