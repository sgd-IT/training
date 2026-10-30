---
title: "JAR包生产环境日志输出保障"
usage_scenario: []
keywords:
    - "JAR包"
    - "生产环境"
    - "日志框架"
source: "user"
---

项目在生产环境以JAR包方式运行时，需确保日志框架（如SLF4J+Logback）正确集成并输出到指定文件，避免仅依赖System.out.println导致日志丢失。
