---
title: "SD平台dosage字段JSON格式规范"
usage_scenario:
    - "实现或修改向SD平台发送dosage字段的逻辑时"
    - "排查dosage小数位校验失败问题时"
    - "设计其他数值型字段（如storeroomArea、longitude）的序列化方式时"
keywords:
    - "SD平台"
    - "dosage"
    - "JSON数字"
    - "无引号"
    - "字段格式"
---

SD平台对接文档明确要求dosage字段必须以JSON数字格式发送（无引号），禁止使用字符串格式（如"dosage":"28260.00"），否则视为小数位非法。
