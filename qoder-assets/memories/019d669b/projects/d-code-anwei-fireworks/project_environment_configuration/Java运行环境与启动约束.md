---
title: "Java运行环境与启动约束"
usage_scenario: []
keywords:
    - "JDK 8"
    - "Spring Boot"
    - "微服务"
    - "java -jar"
---

**运行环境要求**: 
- JDK版本: `1.8`（由`maven.compiler.source/target=8`及`spring-boot-dependencies 2.1.5.RELEASE`反向确认）
- 无显式运行时环境变量或启动命令，但模块结构（如`monkey-service`）表明为Spring Boot微服务架构，预期通过`java -jar`或`mvn spring-boot:run`启动
