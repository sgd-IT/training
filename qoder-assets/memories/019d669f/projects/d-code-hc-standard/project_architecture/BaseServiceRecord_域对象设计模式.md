---
title: "BaseServiceRecord 域对象设计模式"
usage_scenario:
    - "When adding a new Platement sub-feature requiring local storage and platform synchronization"
    - "When designing a new domain object that must follow the existing Platement conventions"
keywords:
    - "BaseServiceRecord"
    - "domain object"
    - "Platement module"
    - "audit fields"
    - "Excel annotation"
source: "auto"
---

The BaseServiceRecord domain object follows the established Platement module pattern: it extends BaseEntity for audit fields (createBy/createTime/updateBy/updateTime/remark), uses @Excel annotations for import/export, and includes @NotBlank constraints on mandatory fields (thirdPartyUnitId, serviceTarget). It is designed to be persisted in the local database and synchronized to the platform via the PlatementServiceRecordApi.
