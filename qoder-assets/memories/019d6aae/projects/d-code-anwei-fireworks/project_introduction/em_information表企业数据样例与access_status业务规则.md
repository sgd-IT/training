---
title: "em_information表企业数据样例与access_status业务规则"
usage_scenario: []
keywords:
    - "em_information"
    - "access_status"
    - "GZ推送"
    - "企业数据"
source: "user"
---

项目中`em_information`表企业数据示例及业务规则：
- id=107：贵州修文县日用杂品公司，`company_code=5201237772`，`upload_company_code=520123920001`，`access_status=1`（启用，参与GZ推送）
- id=117：修文县安雄烟花爆竹贸易有限公司，`company_code=5201237775`，`upload_company_code=520123920003`，`access_status=0`（禁用，不应参与GZ推送）
`access_status=1`是企业被`getGZCompanyListFromPlatform()`查询并推送的前提条件。
