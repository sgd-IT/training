---
title: "SD推送模块全数据库驱动映射关系"
usage_scenario:
    - "维护SD推送功能时确定配置与数据库字段对应关系"
    - "新增企业推送时确认需填写的数据库字段"
    - "迁移或重构配置中心时参考现有数据库驱动模式"
keywords:
    - "SD推送"
    - "upload_company_code"
    - "数据库驱动"
    - "YAML移除"
---

SD推送模块的企业编码字段 `company-code` 映射数据库 `em_information.upload_company_code` 字段，所有企业信息（包括企业编码、企业名称、token）均从 `em_information` 表获取，YAML 配置中已完全移除 `sdupload.companies` 节点。
