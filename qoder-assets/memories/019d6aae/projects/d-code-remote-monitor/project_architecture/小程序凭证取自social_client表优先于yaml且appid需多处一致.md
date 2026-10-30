---
title: "小程序凭证取自social_client表优先于yaml且appid需多处一致"
usage_scenario:
    - "排查小程序手机号快捷登录报系统错误"
    - "核对前后端及微信测试号appid配置一致性"
    - "判断微信凭证来自数据库表还是yaml"
keywords:
    - "system_social_client"
    - "wx.miniapp.appid"
    - "微信测试号"
    - "SocialClientServiceImpl"
    - "手机号快捷登录"
    - "appid一致性"
source: "auto"
---

后端获取微信小程序凭证（换取 getPhoneNumber 的 phoneCode）的链路是双层优先级：SocialClientServiceImpl.getWxMaService(userType) 先查 system_social_client 表中 social_type=34（WECHAT_MINI_APP）、user_type=1（ADMIN）、status=0 启用的记录，命中则用表里的 client_id/client_secret；只有查不到才回退 application-*.yaml 中的 wx.miniapp.appid/secret。因此若数据库里存在历史遗留的小程序记录（如本次发现的 wx63c280fe3248a3e7），会与前端 manifest.json 及三处 yaml（server/src/main/resources/application-dev.yaml、application-prod.yaml、deploy/config/server/application-prod.yaml）中配置的正式 appid wx222eb07bdf774f1d 形成冲突，导致生产环境手机号快捷登录必然失败（微信侧 phoneCode 与 appid 绑定，用错误凭证换号会报系统错误）。账号密码登录不走该链路，不受影响。发布或排障时必须交叉核对这几处 appid 是否一致。另外 application-prod.yaml 中 wxa-code.env-version 为 develop、wxa-subscribe-message.miniprogram-state 为 developer，属开发态取值。
