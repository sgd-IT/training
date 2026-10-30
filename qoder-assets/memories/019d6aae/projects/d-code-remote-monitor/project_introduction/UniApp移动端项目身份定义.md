---
title: "UniApp移动端项目身份定义"
usage_scenario:
    - "改造小程序端列表请求或分页加载逻辑"
    - "判断前后端协作边界与移动端接口是否可用"
    - "识别移动端项目归属与工程构建方式"
keywords:
    - "UniApp"
    - "anwei-fire-remote-monitor-mobile"
    - "app-api前缀"
    - "AppFireAlarmOrderController"
    - "hasMobilePermission"
    - "HBuilderX工程"
    - "onReachBottom"
    - "mp-weixin"
source: "auto"
---

anwei-fire-remote-monitor-mobile 是用户的 UniApp 移动端（微信小程序）项目，与 PC 端前端 anwei-fire-remote-monitor-vue 共用后端仓库 anwei-fire-remote-monitor 的接口。项目已具备较完整功能：账号密码登录（pages/login/index.vue，走 /system/auth/mobile-login）、值班首页、告警中心与告警详情、数据中心图表、我的页面等，非初始化空壳状态。构建产物在 unpackage/dist/dev/mp-weixin，微信开发者工具项目 appid 由 project.config.json 指定。

## Notes
- 【接口前缀修正】移动端不共用 PC 的 /admin-api/**：utils/request.js 会自动为请求拼 /app-api 前缀，后端对应 controller/app/firewatch 下的 App*Controller（鉴权走 @PreAuthorize("@ss.hasMobilePermission")），业务 Service 与 PC 端 admin controller 同源。因此写移动端请求时路径只填 /eiot/firewatch/xxx，且要确认该能力在 App*Controller 已开放（例如 alarm-order/page、data-center/alarm-trend 均有 app 版本）。
- 【工程形态】该工程无 package.json，属 HBuilderX 工程，不能用 npm 脚本起本地预览；验证方式是 HBuilderX 运行到微信开发者工具。临时做语法校验可提取 .vue 的 <script> 段写入 _temp 下 .mjs 后用 node --check。
- 【滚动容器事实】页面多为自定义 NavBar + 普通文档流滚动，把列表包在无固定高度的 scroll-view 里实际不产生内部滚动，触底加载应使用页面级 onReachBottom；pages.json 中未开 enablePullDownRefresh 的页面，写了 onPullDownRefresh 也不会触发。
