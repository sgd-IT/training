---
title: "Spring Boot与Spring Security版本约束"
usage_scenario:
    - "新增或修改安全配置时选择正确的API写法"
    - "排查Security相关编译错误时确认版本兼容性"
    - "新成员搭建环境时了解框架版本限制"
keywords:
    - "Spring Boot 2.1.5"
    - "Spring Security 5.1.x"
    - "版本兼容"
---

项目使用Spring Boot 2.1.5，对应Spring Security 5.1.x版本，不支持requestMatchers(lambda)新API，需使用旧版匹配器组合方式。
