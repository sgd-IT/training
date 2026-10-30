---
title: "Spring Boot profile误配导致Redis连接超时"
usage_scenario: []
keywords:
    - "Spring Boot"
    - "profile"
    - "Redis连接超时"
    - "application-prod.yml"
---

Spring Boot应用启动时若`spring.profiles.active`设为`prod`，则会加载`application-prod.yml`中的配置（如Redis地址`172.16.0.7:6379`），该地址通常为内网IP，本地开发环境无法访问，导致连接超时。应根据运行环境正确设置active profile。（来源：错误堆栈+配置文件搜索）
