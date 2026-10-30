---
title: "Maven依赖版本管理策略"
usage_scenario:
    - "添加新依赖或排查版本冲突时参考统一管理的依赖列表"
keywords:
    - "dependencyManagement"
    - "版本锁定"
    - "Jackson安全修复"
    - "Spring Cloud"
source: "init"
---

依赖管理策略：
- 使用 dependencyManagement 统一管理所有子模块依赖版本
- 核心版本锁定：Spring Boot 2.1.5.RELEASE (通过platform-bom Cairo-SR7间接管理), Spring Cloud Greenwich.SR6
- 安全修复：显式升级 jackson-databind 至 2.9.10.8
- 特殊版本说明：为兼容ES 6.4.3，spring-boot-dependencies 强制指定为 2.1.5.RELEASE
- 主要依赖包括：JWT (jjwt 0.9.0), 支付SDK (微信/支付宝), 云服务SDK (腾讯/阿里/七牛)
