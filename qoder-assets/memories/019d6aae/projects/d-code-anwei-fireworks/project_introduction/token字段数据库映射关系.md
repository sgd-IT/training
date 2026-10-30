---
title: "token字段数据库映射关系"
usage_scenario:
    - "排查企业推送失败原因时定位token来源"
    - "新增类似认证字段映射时参考已有模式"
    - "维护企业信息表字段含义文档时"
keywords:
    - "token"
    - "upload_company_pwd"
    - "数据库映射"
    - "SD推送"
source: "auto"
---

项目中`token`字段映射至数据库`em_information.upload_company_pwd`字段，用于山东平台认证密码，该映射已在`PushingSDDataService.forEachCompany()`方法中实现。
