---
title: "Maven构建与多模块配置"
usage_scenario:
    - "了解项目的Maven构建流程和多模块结构"
keywords:
    - "Maven"
    - "多模块"
    - "阿里云镜像"
    - "dependencyManagement"
source: "init"
---

构建工具: Maven
模块结构: ruoyi-admin, ruoyi-framework, ruoyi-system, ruoyi-quartz, ruoyi-generator, ruoyi-common
仓库配置: 使用阿里云镜像 (https://maven.aliyun.com/repository/public)
编译配置: source/target 1.8, encoding UTF-8
依赖管理: 通过 dependencyManagement 统一版本控制
