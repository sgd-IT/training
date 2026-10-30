---
title: "SD平台企业信息字段映射统一与空值兜底"
usage_scenario:
    - "对接第三方监管平台时处理必填字段空值校验失败"
    - "重构数据映射逻辑以统一数据源并减少重复判空"
    - "同类企业信息同步模块（如应急、消防等平台）的字段兜底策略参考"
keywords:
    - "SD平台"
    - "字段映射"
    - "空值兜底"
    - "统一数据源"
---

## 任务描述
修复SD平台企业信息上传校验失败问题，统一负责人字段（principalPerson/safetyPerson）和电话字段（principalPersonPhone/safetyPersonPhone）的数据源，并对所有必填字段添加空值兜底逻辑。

## 执行过程
```mermaid
graph TD
    A[SD平台报错：safetyPerson/safetyPersonPhone/socialCreditIssuingAuthority/deleted为空] --> B[首轮修改：电话统一用getMobile()，安全负责人兜底getPersonInCharge()]
    B --> C[错误仍存在→分析剩余4项空字段]
    C --> D[次轮修改：socialCreditIssuingAuthority兜底businessLicenseIssuingAuthority；principalPerson/principalPersonPhone/safetyPerson/safetyPersonPhone增加字符串非空判断+默认值；新增deleted字段映射]
    D --> E[用户确认后提出更简需求：负责人/电话全部统一来源]
    E --> F[终轮修改：提取personName/personPhone公共变量，四字段全部复用]
```

## 任务总结
在SetSDUploadInfo.java中完成SD平台字段映射重构：1）统一负责人字段来源为getPersonInCharge()，电话字段来源为getMobile()；2）对6个必填字段（含新增deleted）均实现空值兜底（默认值或备用字段）；3）通过提取公共变量消除重复判断，提升可读性与可维护性。
