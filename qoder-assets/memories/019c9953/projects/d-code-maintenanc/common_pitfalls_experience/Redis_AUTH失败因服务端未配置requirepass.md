---
title: "Redis AUTH失败因服务端未配置requirepass"
usage_scenario:
    - "Spring应用连接Redis报ERR Client sent AUTH, but no password is set"
    - "本地Redis服务运行正常但应用无法认证连接"
    - "项目配置了redis.password但Redis未启用密码验证"
keywords:
    - "Redis"
    - "requirepass"
    - "AUTH"
    - "redis.conf"
---

Redis客户端发送AUTH命令但服务端未设置密码，导致连接拒绝。根本原因是项目配置了password: <REDACTED>，而Redis服务未启用密码认证。解决方案：编辑Redis配置文件（如redis.windows.conf或redis.conf），添加requirepass <REDACTED>，并重启Redis服务。（来源：Bash）
