---
title: "省厅平台dosage字段小数位适配方案"
usage_scenario:
    - "对接强格式校验第三方平台（如政务、监管系统）时保障数值字段小数位精度"
    - "FastJSON序列化行为不可控时，通过JSON字符串后处理修复数值格式"
    - "排查和修复因JSON序列化导致的数字精度丢失问题"
keywords:
    - "dosage"
    - "省厅平台"
    - "小数位适配"
    - "JSON数字格式"
---

## 任务描述
适配省厅平台对dosage字段小数位的特殊解析规则（将'.00'识别为'.0'导致校验失败），确保发送JSON中dosage值的小数部分始终为'.01'。

## 执行过程
1. SetSDUploadInfo中dosage赋值改为：`String formatted = String.format("%.2f", value); formatted = formatted.replace(".00", ".01");`
2. RequestSDMethod中fixDosageDecimalPlaces()使用正则`"dosage":"(\d+\.\d{2})"` → `"dosage":$1`移除引号，确保输出为JSON数字格式。
3. 最终数据流：Double→"28260.00"→"28260.01"→"dosage":28260.01，满足省厅平台校验要求。
