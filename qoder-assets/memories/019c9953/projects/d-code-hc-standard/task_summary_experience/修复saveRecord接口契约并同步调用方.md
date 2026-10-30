---
title: "修复saveRecord接口契约并同步调用方"
usage_scenario:
    - "修复API方法签名变更后调用方未同步导致的编译错误"
    - "重构批量处理逻辑为逐条处理并增强失败统计"
    - "排查类似'无法应用于List'编译报错时的完整修复路径"
keywords:
    - "接口契约"
    - "逐条调用"
    - "失败统计"
---

## 任务描述
修复 PlatementRiskCheckUnitApi.saveRecord 接口契约不一致问题：原方法误接收 List，实际应接收单个 entity；同步修正两处调用方（定时同步与补推接口）以逐条调用。

## 执行过程
```mermaid
graph TD
    A[发现编译错误：saveRecord(List) 不匹配] --> B[定位API定义与两处调用点]
    B --> C[清理PlatementRiskCheckUnitApi.saveRecord中冗余for循环]
    C --> D[确认BaseRiskTaskRecordServiceImpl已改为逐条调用]
    D --> E[重构PushDataServiceImpl.rePushRiskTaskRecord为逐条调用+失败统计]
    E --> F[修复API方法体缩进问题]
    F --> G[三文件一致性验证通过]
```

## 任务总结
完成接口契约标准化：API 方法签名与实现统一为单 entity 入参；两处调用方均改为 for 循环逐条调用，并在补推接口中新增 success/fail 计数与最后失败原因日志，提升可观测性与容错能力。
