---
title: "maven-compiler-plugin中--add-exports参数与JDK8不兼容"
usage_scenario:
    - "JDK8环境下Maven编译报错'无效的标记: --add-exports'"
    - "根POM配置了compilerArgs但子模块编译失败且提示不支持的JDK参数"
    - "迁移JDK8项目时发现原有JDK9+专用编译参数需清理"
keywords:
    - "--add-exports"
    - "JDK8"
    - "maven-compiler-plugin"
    - "pom.xml"
source: "auto"
---

Maven `maven-compiler-plugin` 中使用 `--add-exports` 参数会导致 JDK 8 编译报错 `java: 无效的标记: --add-exports`，因为该参数是 JDK 9+ 模块化系统引入的特性。解决方案：在 `pom.xml` 的 `<plugin>` 配置中彻底删除 `<compilerArgs>` 及其子项（包括 `<arg>--add-exports</arg>` 和 `<arg>java.base/sun.misc=ALL-UNNAMED</arg>`）。（来源：Grep, Read）
