---
title: "Spring Security 5.1.x不支持requestMatchers lambda写法"
usage_scenario:
    - "Spring Boot 2.x项目中Security配置编译报requestMatchers方法无法应用"
    - "升级Spring Security版本前需兼容旧版API写法"
    - "使用AntPathRequestMatcher组合多路径时需避免lambda语法"
keywords:
    - "Spring Security 5.1"
    - "requestMatchers"
    - "OrRequestMatcher"
    - "AntPathRequestMatcher"
---

Spring Security 5.1.x（对应 Spring Boot 2.1.5）不支持 `requestMatchers` 的 lambda 写法（如 `matcher -> matcher.antMatchers(...)`），该API自 Spring Security 5.8+ 引入。必须改用 `OrRequestMatcher` 组合多个 `AntPathRequestMatcher` 实现路径匹配，例如：`http.requestMatcher(new OrRequestMatcher(new AntPathRequestMatcher("/api/lyxl/**"), new AntPathRequestMatcher("/lyxl/**")))`。（来源：Java代码分析）
