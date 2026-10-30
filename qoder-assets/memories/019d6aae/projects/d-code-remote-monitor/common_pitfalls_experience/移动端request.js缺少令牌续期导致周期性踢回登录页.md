---
title: "移动端request.js缺少令牌续期导致周期性踢回登录页"
usage_scenario:
    - "排查周期性掉线或跳登录页"
    - "修改移动端统一请求封装"
    - "小程序登录页反复弹出无法停留的诊断"
keywords:
    - "refresh-token续期"
    - "401账号未登录"
    - "reLaunch不触发onHide"
    - "onUnload兜底清定时器"
    - "轮询token守卫"
source: "auto"
---

Bug 类：移动端（anwei-fire-remote-monitor-mobile）值守首页周期性自动跳回登录页并 toast「账号未登录」。
根因：accessToken 到期后（system_oauth2_client.access_token_validity_seconds，2026-10-16 实测生产库与测试库均为 1800 秒），后端 TokenAuthenticationFilter 校验失败由 AuthenticationEntryPointImpl 返回 code=401、msg=「账号未登录」（HTTP 状态仍是 200）；而 utils/request.js 遇到 401 只清 token + reLaunch 登录页并原样 toast 后端 msg，登录时保存的 refreshToken 从未被使用。PC 端 config/axios/service.ts 已有无感刷新，故仅移动端复现。
修复模式：在 request.js 这一个共享入口里补 401→refresh-token→重放一次（模块级 refreshing promise 让并发 401 共享同一次刷新，避免刷新令牌被重复消费），刷新失败才跳登录页，且不再 toast 原始 msg。
可复用教训：凡是有轮询的客户端，401 必须走「先续期再重放」而不是直接登出；修复要放在所有请求共同经过的那一个封装里。适用边界：uni-app/小程序无 axios 拦截器机制。

## 修复已落地（2026-10-16）
- 后端：AppAuthController（/app-api 侧）补 /system/auth/refresh-token 薄转发到 admin AuthController.refreshToken；@PermitAll 由框架 getPermitAllUrlsFromAnnotations 自动收集进安全白名单，无需改 application.yaml；tenant ignore-urls 也无需加（刷新请求带 tenant-id 头即可）。
- 移动端：request.js 401 → refreshToken 换新 accessToken → 重放一次（retried 标志防循环）；并发 401 共享同一次刷新；网络异常不误踢（保留登录态待下次请求重试）；仅后端明确拒绝刷新或重放后仍 401 才清 token+refreshToken、toast「登录已过期，请重新登录」回登录页。
- 部署顺序：后端必须先上线（否则 /app-api/system/auth/refresh-token 404 仍被踢）；小程序须以 HBuilderX 发行版重新编译上传体验版/正式版后生效。
- 验证方式：mvn -f <后端 pom> -pl module-system/module-system-biz -am compile 通过 + javap 确认 refreshToken 方法存在；request.js 用 node --check（复制为 .mjs）验证语法。

## Notes（2026-10-16 线上诊断补充）
- 代码本地修改完成≠线上生效：build-push.ps1 从 git master checkout+pull 构建镜像，未提交推送的改动不进镜像。
- 线上体验版点登录按钮第一步 generate-login-nonce 即返回 401「账号未登录」，实测对比证明是线上后端版本旧（不含 AppAuthController），而非 token 过期问题；anyRequest().authenticated() 兜底使不存在的 URL 也返 401 而非 404。
- 排查顺序应为：先 WebFetch 线上 app-api 端点确认是否可达 → 若 401 则对比 admin-api 同端点 → 若 admin 正常说明 @PermitAll 收集机制好但线上缺部署 → 执行完整部署流程后再验证续期逻辑。
- 体验版/开发版/正式版本地缓存隔离，首次打开体验版必跳一次登录页属正常，不等于 bug。
- 切页销毁路径的定时器泄漏同源坑：reLaunch/redirectTo 只触发 onUnload 不触发 onHide，故任何写在 onHide 的「切后台清理」都不覆盖该路径，必须同时在 onUnload 兜底 clearInterval；带轮询的页面还须在 onLoad/onShow 双重校验 token，无 token 直接跳过请求以免产生 401 被共享请求层反复踢回、表现为登录页每 30 秒重建打断输入。区分两类现象：输入被打断=本条定时器泄漏；点登录立即 401=线上后端版本旧。
