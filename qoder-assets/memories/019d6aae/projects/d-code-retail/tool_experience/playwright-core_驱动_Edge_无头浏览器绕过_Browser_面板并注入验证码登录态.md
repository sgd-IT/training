---
title: "playwright-core 驱动 Edge 无头浏览器绕过 Browser 面板并注入验证码登录态"
usage_scenario:
    - "验证本地前端页面渲染与跳转"
    - "登录流程含图形验证码的自动化"
    - "定位页面布局尺寸异常"
keywords:
    - "playwright-core"
    - "Edge headless"
    - "storageState 登录态复用"
    - "Redis 验证码直读"
    - "getBoundingClientRect 布局诊断"
source: "auto"
---

## 使用场景
需要验证本地前端页面真实渲染（登录跳转、布局尺寸、图表、数据），但 Qoder Browser 面板截图不可用（take_screenshot 报 viewport 不可见/visibilityState=hidden）时。

## 使用方法
1. 在临时目录 `npm i playwright-core`（不下载浏览器），用 `chromium.launch({ channel: 'msedge', headless: true })` 驱动系统 Edge。
2. 登录态用 `context.storageState({ path })` 保存、下次加载复用，避免每次重跑都要过登录；结束后务必删除该文件（内含 token）。
3. 图形验证码不可靠时，可从 Redis 直读答案：RuoYi-Plus 存于 `global:captcha_codes:<uuid>`，uuid 通过 `page.on('response')` 拦截 `/auth/code` 响应获得；值为 RedisUtils 序列化的 JSON，需 `Trim('"')` 去引号。用 PowerShell 手写 RESP 协议（`*2\r\n$3\r\nGET\r\n$53\r\n<key>\r\n`）即可查询。
4. 布局问题优先用 `page.evaluate` 采集 `getBoundingClientRect()` 与 `getComputedStyle`（width/max-width/flex/boxSizing），比看截图更快定位。

## 注意事项
- PowerShell 向 node 传空字符串参数会被吞掉导致参数位移，用占位符（如 `x`）代替。
- 无头浏览器只跑在本地时无需沙箱外权限。
