---
title: "Excel枚举字段双向转换规范"
usage_scenario:
    - "新增Excel导入功能时配置枚举字段映射"
    - "修复导入时枚举值解析失败问题"
    - "审查现有Excel导入代码是否符合双向转换要求"
keywords:
    - "Excel导入"
    - "readConverterExp"
    - "枚举映射"
    - "position字段"
source: "auto"
---

Excel导入时，枚举型字段（如position）必须使用@Excel注解的readConverterExp属性配置双向转换规则，例如'0=带班领导,1=值班人员'，确保导出显示中文、导入识别中文并转为对应数字值。
