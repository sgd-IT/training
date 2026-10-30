---
title: "Maven构建与多模块配置"
usage_scenario:
    - "进行项目编译、打包或配置多环境时参考"
keywords:
    - "Maven"
    - "JDK 21"
    - "多模块"
    - "Profile"
    - "构建配置"
source: "init"
---

构建工具：Maven

Java版本：21 (默认), 支持25

Maven配置：
- 使用flatten-maven-plugin统一版本号
- 编译插件：maven-compiler-plugin (release 21)
- 跳过测试打包：maven.test.skip=true

多环境Profile：
- local: 本地开发环境
- dev: 默认开发环境
- prod: 生产环境

模块结构：
- ruoyi-admin: Web服务入口
- ruoyi-common: 通用模块
- ruoyi-extend: 扩展模块
- ruoyi-modules: 业务模块
- ruoyi-api: API模块
