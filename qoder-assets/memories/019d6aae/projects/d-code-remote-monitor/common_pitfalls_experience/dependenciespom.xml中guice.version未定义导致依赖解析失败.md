---
title: "dependencies/pom.xml中guice.version未定义导致依赖解析失败"
usage_scenario:
    - "Maven多模块项目编译报'程序包xxx不存在'但源码和pom依赖均存在"
    - "IDE中import红色报错且Grep确认包路径存在"
    - "检查dependencies/pom.xml时发现未定义的${xxx.version}占位符"
keywords:
    - "guice.version"
    - "dependencies.pom.xml"
    - "Maven BOM"
    - "依赖解析失败"
source: "auto"
---

Maven构建失败导致程序包不存在的编译错误，根本原因是`dependencies/pom.xml`中第604行引用了未定义的属性`${guice.version}`，破坏BOM依赖解析。解决方案：注释或删除该guice依赖块（来源：Grep）
