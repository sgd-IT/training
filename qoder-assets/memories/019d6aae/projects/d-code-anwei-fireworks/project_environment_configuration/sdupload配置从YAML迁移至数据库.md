---
title: "sdupload配置从YAML迁移至数据库"
usage_scenario:
    - "维护多环境配置时确认sdupload相关配置是否已清理"
    - "新环境部署时避免误保留旧YAML企业配置"
    - "排查SD推送企业列表来源异常时检查配置残留"
keywords:
    - "YAML配置清理"
    - "sdupload"
    - "数据库读取"
---

项目在dev和prod环境的application.yml配置文件中，已移除sdupload.companies企业列表配置，后续企业信息及token统一从数据库em_information表的upload_company_code和upload_company_pwd字段读取。
