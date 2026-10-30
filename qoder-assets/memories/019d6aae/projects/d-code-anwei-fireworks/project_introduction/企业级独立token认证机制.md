---
title: "企业级独立token认证机制"
usage_scenario:
    - "设计或审查SD平台认证流程时确认token作用域"
    - "排查认证失败问题时排除全局token误用可能"
    - "新增企业接入时确保配置专属token而非复用"
keywords:
    - "独立token"
    - "企业级认证"
    - "无全局token"
---

项目采用企业级独立token认证机制，不存在全局默认token；每个企业必须配置专属10位token，推送时严格使用对应企业的token进行RSA加密认证。
