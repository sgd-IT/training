---
title: "FastJSON数值精度丢失的JSON字符串后处理修复"
usage_scenario:
    - "修复因FastJSON等JSON库序列化导致的BigDecimal/Double小数位丢失问题"
    - "SD或其他第三方平台对JSON数字字段有严格小数位校验时的兼容性修复"
    - "在JSON已序列化为字符串但尚未发送前进行字段精度修正"
keywords:
    - "FastJSON"
    - "小数位修复"
    - "JSON后处理"
---

## 任务描述
修复SD平台`dosage`字段小数位校验失败问题：FastJSON将BigDecimal(28260.00)序列化为数字28260.0，导致SD平台校验"需精准2位小数"失败。

## 执行过程
```mermaid
graph TD
    A[日志分析] --> B[定位问题：JSON数字类型丢失末尾零]
    B --> C[错误尝试：将dosage转String传入JSONObject]
    C --> D[回退：恢复BigDecimal传入，避免破坏JSON结构]
    D --> E[正确定位：在RequestSDMethod.uploadData中JSON序列化后、加密前插入后处理]
    E --> F[实现fixDosageDecimalPlaces正则修复：覆盖整数→.00、1位→补0、2位→不变]
```

## 任务总结
通过在RequestSDMethod.uploadData中添加JSON字符串级正则后处理，精准修复dosage字段小数位：匹配`"dosage":N`→`"dosage":N.00`，`"dosage":N.M`→`"dosage":N.M0`，确保SD平台接收到符合要求的数字格式（如`"dosage":28260.00`）。
