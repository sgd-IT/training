---
title: "框架按Controller包自动加API路径前缀：admin-api与app-api"
usage_scenario:
    - "调用AI相关接口返回401且提示'账号未登录'但实际已登录"
    - "根据Controller路径构造HTTP请求时忽略框架自动添加的路径前缀"
    - "为controller.app包下开放回调接口构造测试请求URL"
keywords:
    - "admin-api"
    - "app-api"
    - "路径前缀"
    - "WebProperties"
    - "controller包映射"
    - "接口401"
source: "auto"
---

项目使用自定义Web框架，自动按Controller所在包添加统一路径前缀（来源：`WebProperties.java`）：
- `controller.admin` 包 → `/admin-api` 前缀，例如 `/ai/data/alarm/statistics` 应访问为 `/admin-api/ai/data/alarm/statistics`
- `controller.app` 包 → `/app-api` 前缀，例如 `CameraAlertOpenController`（`@RequestMapping("/open/camera-alert")`，位于 controller.app.cameraalert 包）实际访问路径是 `/app-api/open/camera-alert/report`

构造任何 HTTP 测试/联调请求时必须先确认 Controller 属于 admin 还是 app 包，再显式加上对应前缀，否则 404；安全白名单（application.yaml permit-all urls）中配置的也是带前缀的完整路径，可交叉印证。
