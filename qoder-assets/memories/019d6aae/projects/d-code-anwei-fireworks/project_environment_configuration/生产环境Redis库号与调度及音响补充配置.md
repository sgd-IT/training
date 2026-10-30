---
title: "生产环境Redis库号与调度及音响补充配置"
usage_scenario:
    - "新环境部署时补齐prod专属参数"
    - "排查调度或IP音响播报异常时核对参数"
    - "配置迁移时作为基准版本参考"
keywords:
    - "prod补充配置"
    - "Redis database2"
    - "XXL-JOB调度中心"
    - "IP音响stop-time"
    - "WebSocket凭证"
source: "auto"
---

项目生产环境（prod）标准配置补充项（数据库/Redis/MQTT/WebSocket/第三方API/开关等基础连接信息以"线上正式环境配置"为准，此处不重复）：
- Redis使用database 2
- XXL-JOB调度中心地址 http://111.230.42.212:8989/xxl-job-admin，执行器端口9999
- IP音响启用（isopen=true），服务地址127.0.0.1:10011，停止播放间隔 stop-time=15000毫秒
- WebSocket配套 MapId、AppKey、AccessKey 均为 '1234567890'
- MQTT账号 fireworks / Anwei@321
- 广西、广东、山东、贵州、云南等烟花爆竹数据上报地址及凭证按对接方提供配置；青天平台、海康云、JT808服务端口等第三方集成地址与密钥已固化
