---
title: "贵州JCXX企业入库按upload_company_code加provincecode52判重"
usage_scenario:
    - "判断贵州企业是否需新增入库时"
    - "编写企业数据入库去重逻辑时"
    - "排查重复企业创建问题时"
keywords:
    - "upload_company_code"
    - "provincecode=52"
    - "JCXX企业入库"
    - "新增去重跳过插入"
    - "贵州企业推送"
source: "auto"
---

贵州企业信息推送去重规则：在贵州企业数据推送场景中，若数据库中已存在相同 upload_company_code 且 provincecode=52 的记录，则不再新增企业，而是跳过插入操作并记录日志。
