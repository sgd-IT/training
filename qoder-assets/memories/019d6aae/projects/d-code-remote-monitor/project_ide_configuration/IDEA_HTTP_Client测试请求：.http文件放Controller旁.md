---
title: "IDEA HTTP Client测试请求：.http文件放Controller旁"
usage_scenario:
    - "为后端新接口补建.http测试文件时参考位置与写法"
    - "在IDEA里快速发送接口请求验证改动"
    - "排查.http中环境变量无法解析的问题"
keywords:
    - "HTTP Client"
    - ".http文件"
    - "baseUrl变量"
    - "x-www-form-urlencoded"
    - "CameraAlertOpenController"
source: "auto"
---

anwei-fire-remote-monitor 后端仓库的接口手工测试惯例是在每个 Controller 源码旁放置同名 `.http` 文件（IDEA 内置 HTTP Client），全仓库已有 17 个此类文件，如 `AuthController.http`、`FileConfigController.http`。
- 现有文件多用 `{{baseUrl}}`、`{{adminTenentId}}`、`{{token}}` 环境变量，但仓库内**没有** http-client.env.json / http-client.private.env.json（需各成员本地自建）；不依赖变量时可在 `.http` 里直接写完整 URL（含 IP 与端口）。
- 请求段落以 `### 描述` 分隔；发送方式为点行号左侧绿色 ▶ 或光标置于请求上按 Ctrl+Enter，响应在底部 HTTP Client Run 工具窗查看。
- 临时请求（不落盘）可用 Ctrl+Shift+Alt+Insert 或 Tools → HTTP Client → Create Request in HTTP Client。
- 已新增 `module-eiot/module-eiot-biz/.../controller/app/cameraalert/CameraAlertOpenController.http`，内含热成像等 5 种 eventType 上报请求与 multipart 带图示范。
- 摄像头报警开放接口 `/app-api/open/camera-alert/report` 的请求体为 `application/x-www-form-urlencoded` 且 data 值可为未编码的原样 JSON（Node fetch 实测原样字节返回 code:0/data:true；Tomcat 对未编码字符宽容），multipart/form-data 同样可行。
