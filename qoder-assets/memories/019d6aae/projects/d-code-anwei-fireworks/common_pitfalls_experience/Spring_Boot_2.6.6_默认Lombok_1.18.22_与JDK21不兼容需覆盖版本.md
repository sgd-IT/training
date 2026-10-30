---
title: "Spring Boot 2.6.6 默认Lombok 1.18.22 与JDK21不兼容需覆盖版本"
usage_scenario:
    - "JDK21下编译老SpringBoot项目javac崩溃"
    - "NoSuchFieldError JCoint报错定位"
    - "通过覆盖lombok.version解决编译失败"
keywords:
    - "lombok 1.18.22"
    - "JCImport qualid 报错"
    - "JDK21 编译崩溃"
    - "spring-boot 2.6.6"
    - "lombok.version 覆盖"
source: "auto"
---

JDK 21 环境下 Maven 编译 Spring Boot 2.6.6 项目时 javac 崩溃：Fatal error compiling: java.lang.NoSuchFieldError: Class com.sun.tools.javac.tree.JCTree$JCImport does not have member field 'com.sun.tools.javac.tree.JCTree qualid'。

根因：Spring Boot 2.6.6 的依赖管理将 lombok 锁定为 1.18.22（项目 pom 未显式指定版本时继承该版本），而 Lombok < 1.18.30 不支持 JDK 21 的 javac 内部 API 变更。

修复模式：在项目 pom 的 <properties> 中覆盖 <lombok.version>1.18.46</lombok.version>，实测 JDK 21 + Spring Boot 2.6.6 编译通过；不要使用 1.18.22~1.18.29 区间版本。

适用边界：Spring Boot 2.6.x/2.7.x 等由 parent 管理旧版 lombok 的项目在 JDK 21 下编译时适用；JDK 8/11 编译环境无此问题。
