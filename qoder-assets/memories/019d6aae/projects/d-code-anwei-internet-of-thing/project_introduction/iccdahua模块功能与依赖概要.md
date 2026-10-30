---
title: "iccdahua模块功能与依赖概要"
usage_scenario: []
keywords:
    - "iccdahua"
    - "视频拉流"
    - "RSA鉴权"
    - "Redis缓存"
    - "SSL忽略"
---

iccdahua模块是大华ICC智能监控平台集成模块，核心能力为视频流拉取，实现四步链路：鉴权认证（RSA+OAuth2 Token）、设备详情查询、通道选择、实时视频流地址获取；依赖RedisUtils缓存Token、MonkeyAssert校验参数，并需Apache HttpClient支持SSL忽略。
