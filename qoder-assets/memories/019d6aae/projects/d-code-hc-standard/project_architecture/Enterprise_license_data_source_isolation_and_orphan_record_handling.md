---
title: "Enterprise license data source isolation and orphan record handling"
usage_scenario:
    - "排查企业管理证照数据不一致"
    - "改造资质证书管理页面"
    - "确认营业执照唯一数据源"
keywords:
    - "enterprise license"
    - "base_contractor"
    - "contractorCert"
    - "data source isolation"
    - "orphan records"
source: "auto"
---

企业管理模块的营业执照只有一份数据，存在 base_contractor.file_path（企业信息表单的必填上传项）；base_contractor_cert（资质证书表）从来不会由企业保存流程自动写入，两者之间没有任何同步逻辑。因此"企业管理 → 资质证书管理"页（base/contractorCert/index.vue，隐藏路由 /base/contractorCert/index/:id）只按 contractor_id 查 base_contractor_cert，页面里的营业执照永远不会出现：测试库该表 0 条，生产库仅 4 条且其中 2 条（营业执照、安全生产许可证）挂在已不存在的 contractor_id=1038 下成为孤儿记录。2026-09-12 已按方案 C 改造：该页顶部固定以只读方式展示企业主表营业执照（图片 + 统一社会信用代码，调 getContractor 取回），表格继续只维护其他资质证书；未做数据补齐，也没有企业保存时 upsert 证书记录的逻辑。若后续要营业执照出现在证书表里，需要走方案 B（企业新增/修改时 upsert 营业执照证书记录 + 补历史数据）。
