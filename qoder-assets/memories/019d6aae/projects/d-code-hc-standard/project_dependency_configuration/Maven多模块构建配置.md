---
title: "Maven多模块构建配置"
usage_scenario:
    - "了解项目的构建方式、模块划分及依赖管理策略"
keywords:
    - "Maven"
    - "多模块"
    - "dependencyManagement"
    - "阿里云镜像"
source: "init"
---

构建与依赖管理：
- 构建工具: Maven
- 项目结构: 多模块聚合项目 (ruoyi-admin, ruoyi-framework, ruoyi-system, ruoyi-quartz, ruoyi-generator, ruoyi-common)
- 依赖管理: 使用 `<dependencyManagement>` 统一版本控制
- 仓库配置: 使用阿里云 Maven 镜像 (`https://maven.aliyun.com/repository/public`)
