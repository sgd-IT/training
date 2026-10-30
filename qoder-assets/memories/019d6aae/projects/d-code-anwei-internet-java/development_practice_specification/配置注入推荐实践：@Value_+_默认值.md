---
title: "配置注入推荐实践：@Value + 默认值"
usage_scenario:
    - "新增配置项时选择注入方式"
    - "重构配置读取逻辑以减少类数量"
    - "排查配置缺失导致空指针时确认默认值策略"
keywords:
    - "@Value"
    - "配置默认值"
    - "配置注入"
    - "Spring Boot"
---

在Spring Boot项目中，推荐使用`@Value`注解直接注入配置项，并在冒号后提供合理默认值（如`@Value("${loulv.companyName:安为物联网科技有限公司}")`），避免为简单配置新建`@ConfigurationProperties`类，提升代码简洁性与可维护性。
