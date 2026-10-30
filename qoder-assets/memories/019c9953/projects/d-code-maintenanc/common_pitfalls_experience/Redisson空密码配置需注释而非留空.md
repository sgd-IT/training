---
title: "Redisson空密码配置需注释而非留空"
usage_scenario:
    - "Spring Boot应用使用Redisson连接无密码Redis时报ERR Client sent AUTH, but no password is set"
    - "修改application.yml中password:为空后仍触发AUTH命令"
    - "本地开发环境Redis无密码但应用强制认证"
keywords:
    - "Redisson"
    - "AUTH"
    - "password注释"
    - "application-dev.yml"
---

Redisson 配置中 `password:` 留空（即 `password:`）会被解析为空字符串 `""`，仍会触发 AUTH 命令；必须将该行**完全注释掉**（如 `#password: <REDACTED>`），才能使 Redisson 跳过密码认证。需同步修改源文件 `src/main/resources/application-dev.yml` 和编译后文件 `target/classes/application-dev.yml`。（来源：SearchReplace）
