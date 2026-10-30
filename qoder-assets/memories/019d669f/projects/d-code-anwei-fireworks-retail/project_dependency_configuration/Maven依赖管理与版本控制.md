---
title: "Maven依赖管理与版本控制"
usage_scenario:
    - "添加新依赖或排查依赖冲突时参考版本信息"
keywords:
    - "Maven"
    - "依赖管理"
    - "Spring Boot 4.1"
    - "MyBatis-Plus"
    - "Sa-Token"
source: "init"
---

依赖管理：通过Maven的dependencyManagement统一管理版本。

核心依赖版本：
- Spring Boot: 4.1.0
- MyBatis-Plus: 3.5.17
- Sa-Token: 1.45.0
- Redisson: 4.6.1
- Hutool: 5.8.47
- SpringDoc: 3.0.3
- Lock4j: 2.2.7
- WarmFlow: 1.8.9
- AWS SDK: 2.48.1

自定义BOM依赖：
- cn.hutool:hutool-bom
- cn.dev33:sa-token-bom
- org.dromara:ruoyi-common-bom

第三方集成：JustAuth (三方登录), sms4j (短信), mail-api (邮件), liteflow (规则引擎)。
