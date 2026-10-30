---
title: "企业名称数据源统一为em_information表"
usage_scenario:
    - "修改企业相关配置项时确定其数据来源"
    - "排查企业名称显示异常时验证是否走数据库字段"
    - "新接入企业字段时避免重复配置到YAML"
keywords:
    - "企业名称"
    - "em_information"
    - "company_name"
    - "配置去重"
    - "数据源统一"
---

企业名称（company-name）不再从application-dev.yml等配置文件读取，统一从数据库em_information表的company_name字段获取，确保数据源唯一性和一致性。
