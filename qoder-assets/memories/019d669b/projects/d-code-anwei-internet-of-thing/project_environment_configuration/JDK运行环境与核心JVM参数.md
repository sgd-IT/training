---
title: "JDK运行环境与核心JVM参数"
usage_scenario: []
keywords:
    - "JDK 1.8"
    - "JVM参数"
    - "Spring Boot调试"
---

项目使用JDK 1.8运行，路径为D:\Tool\jdk\jdk1.8\bin\java.exe；关键JVM参数包括：-XX:TieredStopAtLevel=1（禁用C2编译器优化）、-noverify（跳过字节码验证）、-Dspring.output.ansi.enabled=always（启用ANSI彩色日志输出）。
