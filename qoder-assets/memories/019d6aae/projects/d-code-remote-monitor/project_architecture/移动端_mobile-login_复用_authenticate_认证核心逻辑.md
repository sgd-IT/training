---
title: "移动端 mobile-login 复用 authenticate 认证核心逻辑"
usage_scenario:
    - "为移动端新增登录方式时接入既有认证核心"
    - "排查账号无法登录或被锁定时定位实现位置"
keywords:
    - "authenticate方法"
    - "mobile-login入口"
    - "失败计数锁定"
    - "登录日志记录"
    - "认证逻辑复用"
source: "auto"
---

移动端登录入口 /mobile-login 复用账号密码登录的核心认证逻辑 authenticate() 方法：失败计数锁定、用户禁用校验、登录日志记录三项均由该方法统一处理，新增任何移动端登录方式（含第三方/快捷登录）应接在 authenticate 之后而不重复实现上述三项。仓库内另有微信手机号快捷登录走 SocialClientServiceImpl 凭证链路，与本条账号密码认证链路相互独立、互不影响。
