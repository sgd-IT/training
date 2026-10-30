---
title: "Lombok与高版本JDK不兼容导致编译失败"
usage_scenario:
    - "Maven编译报IllegalAccessError且涉及lombok.javac.apt.LombokProcessor"
    - "JDK17及以上环境下Lombok编译失败"
    - "IDE能编译但命令行mvn compile失败且错误含jdk.compiler模块"
keywords:
    - "Lombok"
    - "JDK版本兼容"
    - "IllegalAccessError"
    - "maven编译失败"
---

在 JDK 17+ 环境下使用旧版 Lombok（如 <1.18.30）会导致 Maven 编译失败，错误为 `java.lang.IllegalAccessError: class lombok.javac.apt.LombokProcessor cannot access class com.sun.tools.javac.processing.JavacProcessingEnvironment because module jdk.compiler does not export com.sun.tools.javac.processing`。解决方案：将 `pom.xml` 中的 Lombok 版本升级至 `1.18.30` 或更高（如 `<version>1.18.34</version>`），并确保 `maven-compiler-plugin` 的 `source` 和 `target` 与 JDK 版本一致（如 `<source>17</source><target>17</target>`）。（来源：Bash）
