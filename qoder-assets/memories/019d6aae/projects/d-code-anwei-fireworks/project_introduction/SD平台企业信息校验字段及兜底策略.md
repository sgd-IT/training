---
title: "SD平台企业信息校验字段及兜底策略"
usage_scenario:
    - "对接SD平台时处理企业信息字段校验失败"
    - "新增或修改企业信息接口时确保字段完整性"
    - "排查SD平台数据同步失败原因"
keywords:
    - "SD平台"
    - "企业信息校验"
    - "字段兜底"
    - "socialCreditIssuingAuthority"
    - "safetyPerson"
source: "auto"
---

SD平台企业信息校验要求以下字段非空：socialCreditIssuingAuthority（统一社会信用代码发证机关）、safetyPerson（安全负责人）、safetyPersonPhone（安全负责人电话）、deleted（删除标志）。对应兜底策略为：socialCreditIssuingAuthority为空时使用businessLicenseIssuingAuthority；safetyPerson二级兜底（getSafetyPerson→getPersonInCharge→"负责人"）；safetyPersonPhone为空时兜底"00000000000"；deleted字段新增，getDeleteFlag为空时默认0。
