---
title: "SD平台数值字段尾数强制.01格式化实现"
usage_scenario:
    - "对接SD平台时处理数值字段尾数校验失败问题"
    - "修复因fastjson序列化导致.00结尾被拒的数值格式化逻辑"
    - "类似平台要求'非整数结尾'的数值格式化方案参考"
keywords:
    - "SD平台"
    - "尾数.01"
    - "ensureNonZeroCents"
---

## 任务描述
解决SD平台对数值字段尾数校验的特殊要求：面积（acreage）、药量（dosage）等字段必须格式化为2位小数且尾数不能为.00，至少为.01。

## 执行过程
```mermaid
graph TD
    A[需求:SD平台拒绝.00结尾] --> B[定位Warehouse实体中Double类型字段]
    B --> C[发现fastjson序列化生成.00格式]
    C --> D[添加formatTwoDecimals(String/Double)重载方法]
    D --> E[发现.00仍被拒→重构ensureNonZeroCents逻辑]
    E --> F[检测格式化结果是否以.00结尾]
    F --> G[若是则+0.01重新格式化]
    G --> H[同步应用到acreage/dosage/safetyPersonPhone等所有相关字段]
```

## 任务总结
实现SD平台兼容的数值格式化：新增ensureNonZeroCents(double)方法，先%.2f格式化，若结果endsWith(".00")则+0.01再格式化；统一替换getStoreList、getCompanyInfo等接口中acreage、dosage、safetyPersonPhone等字段的put逻辑，确保所有数值字段输出尾数≥.01。
