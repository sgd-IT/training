---
title: "social_credit_scope字段优先级规则"
usage_scenario:
    - "处理企业基础信息（JCXX）JSON生成逻辑时"
    - "编写或重构SocialCreditScopeConverter相关调用逻辑时"
    - "审查access_status校验及数据推送字段映射逻辑时"
keywords:
    - "social_credit_scope"
    - "兜底逻辑"
    - "字段优先级"
    - "编码转换"
source: "auto"
---

social_credit_scope字段如果本身有值，则直接对其进行编码转换，不得兜底使用business_license_scope字段的值。
