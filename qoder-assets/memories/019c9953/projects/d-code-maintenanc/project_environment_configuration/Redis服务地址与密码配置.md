---
title: "Redis服务地址与密码配置"
usage_scenario:
    - "本地启动项目前验证Redis服务状态和密码配置一致性"
    - "新环境部署时配置Redis服务密码"
    - "排查Redis连接失败问题时比对客户端与服务端认证配置"
keywords:
    - "Redis"
    - "localhost:6379"
    - "ruoyi123"
    - "Redisson"
    - "AUTH错误"
---

项目Redis服务运行在localhost:6379，要求密码为ruoyi123；应用通过Redisson客户端连接，若服务端未设置密码而配置文件中指定了password，则会报错'ERR Client sent AUTH, but no password is set'
