---
title: "Information与Personnel实体字段映射关系"
usage_scenario: []
keywords:
    - "Information"
    - "Personnel"
    - "companyCode"
    - "uploadCompanyCode"
    - "数据映射"
source: "user"
---

项目数据模型约定：Information实体的companyCode字段映射数据库em_information表的company_code列（企业主编码），uploadCompanyCode字段映射upload_company_code列（省平台上传编码）；Personnel实体的companyCode字段映射em_personnel表的company_code列，用于关联Information主编码。
