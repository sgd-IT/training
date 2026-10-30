---
title: "Java 21 + Spring Boot 3.4.1 技术栈"
usage_scenario: []
keywords:
    - "Java 21"
    - "Spring Boot 3.4.1"
    - "Lombok"
    - "MapStruct"
    - "annotationProcessorPaths"
---

**开发语言与运行环境**: Java 21

**核心框架与库**：
- Spring Boot 3.4.1（含 `spring-boot-configuration-processor`）
- Lombok 1.18.36（用于编译期代码生成）
- MapStruct 1.6.3（用于对象映射）

**构建特性**：
- 使用 `maven-compiler-plugin` 配置多注解处理器路径（Lombok + MapStruct + Spring Boot Processor）
- 编译参数启用 `-parameters`（支持Spring Boot 3.2+参数名发现）
