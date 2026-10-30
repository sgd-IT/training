---
title: "JWT令牌存储与使用规范"
usage_scenario:
    - "实现或排查移动端登录token续期与401重放逻辑"
    - "编写API请求工具时配置Authorization头和刷新策略"
    - "排查长时间使用后突然被踢回登录页的原因"
keywords:
    - "JWT"
    - "accessToken"
    - "refreshToken"
    - "token存储"
    - "Authorization头"
    - "登录状态"
    - "token过期"
    - "401账号未登录"
    - "access_token_validity_seconds"
source: "auto"
---

项目使用JWT访问令牌（accessToken）进行身份认证：登录成功后调用uni.setStorageSync('token', accessToken)持久化存储；后续HTTP请求在Authorization请求头中携带Bearer令牌；页面加载时通过uni.getStorageSync('token')判断用户是否已登录。
