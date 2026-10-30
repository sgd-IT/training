---
title: "PC大屏角色跳转入口四处硬编码与默认落地页配置"
usage_scenario:
    - "改造大屏弹窗或推送提示时确定改动范围"
    - "调整按角色分流的登录后默认首页"
    - "排查值守大屏路由入口与scene参数来源"
keywords:
    - "角色跳转入口硬编码"
    - "登录默认落地页"
    - "monitoring-center企业主页"
    - "dashboard浅色卡片版"
    - "监控中心双页面歧义"
source: "auto"
---

消防值守 PC 端两块大屏按登录角色分属两条独立使用线，改动需同步评估两侧：

- duty-screen（值守大屏）：super_admin / monitor_role / fire_watcher 使用，可跨租户查看数据。
- monitoring-center（监控中心大屏）：企业账号专用落地页，仅展示本企业数据；"告警趋势(近7天)"位于中栏底部图卡区。

判定依据四处硬编码入口：Login/components/LoginForm.vue 登录后默认路径改写逻辑、views/Redirect/HomeRedirect.vue 根路径按角色重定向、layout/components/ToolHeader.vue "去大屏"按钮的角色分支、router/modules/remaining.ts 的路由注册与注释。因此 monitoring-center 不是废弃页面，任何弹窗/推送类前端改造若只改 duty-screen，企业账号这条线会整体漏掉。

补充（2026-10-14 实测 test 库 + 代码）：演示账号（username=test，昵称"演示账号"）角色为 monitor_role/reader，按 HomeRedirect 同样落地 /duty-screen。用户需求中说"首页"时指登录落地页——超管与演示账号的首页都是值守大屏，故"首页的实时告警/点击"等界面行为默认指 duty-screen。

## Notes
- 2026-08-27 实测更新：为满足"消控室值班员需要 duty-center（即 /duty-screen）"需求，上述前三处入口的角色判断已统一加上 fire_watcher（LoginForm.vue 登录默认路径、HomeRedirect.vue 根路径重定向、ToolHeader.vue "去大屏"按钮），即 super_admin / monitor_role / fire_watcher 均落地 /duty-screen。ToolHeader 原先只判 super_admin、不含 monitor_role 的口径不一致本次未动（monitor_role 点"去大屏"仍去 monitoring-center）。
- duty-screen 是 hidden 固定路由、菜单表里无入口，调整默认首页即可，不要新增菜单（会与 remaining.ts 固定路由生成第二个路由实例冲突）。
- scene 参数无需在跳转处显式携带：duty-screen/index.vue 内 currentScene 默认 'city'，且有 immediate watch 用 router.replace 把 URL 归一化为 ?scene=city，跳 /duty-screen 后地址栏自动变成 #/duty-screen?scene=city。
- 【环境差异警示 2026-10 prod 实测】fire_watcher 角色只存在于 test 库；prod 库 system_role 全表无 fire_watcher，实际存在的值守相关角色为 monitor_role（id=162，tenant_id=1，有 eiot:firewatch:dashboard:query）、duty_person（id=169，碧桂园租户174，未配置任何权限）、以及各租户 tenant_admin。核对角色可见性/权限时必须先确认所查环境，不能拿 test 的角色清单套 prod。
- 【"监控中心"存在两套页面 2026-10-28 实测更正】上文"monitoring-center 即监控中心"的口径不完整：tags-view 上名为「监控中心」的标签实际指向 src/views/eiot/firewatch/dashboard/index.vue（浅色 #eef5f5 卡片版，含 stat-cards + 快捷操作三按钮 + 告警趋势/设备类型分布/风险单位分布三图，靠后端动态菜单挂载，remaining.ts 里的 dashboard 静态路由已被注释），而 monitoring-center/index.vue 是深蓝带 AMap 地图的另一套大屏。用户口头说"监控中心"时需先按布局特征（有无地图、深浅色、是否有快捷操作卡）二选一定位文件，不能只按菜单名猜。dashboard 页消费的六个端点 device-status / today-alarm / realtime-alarms / alarm-trend / device-category / risk-distribution 均由 FireDashboardController 真实实现，权限同为 eiot:firewatch:dashboard:query。
