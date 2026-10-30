---
title: "四相报警接口result字段类型兼容性改造"
usage_scenario:
    - "集成第三方API时响应体result字段类型不固定（对象/字符串混用）"
    - "修复因FastJSON反序列化失败导致的服务崩溃"
    - "优化业务异常日志级别避免error日志污染"
keywords:
    - "result类型兼容"
    - "FastJSON手动解析"
    - "日志级别降级"
---

## 任务描述
解决四相报警接口因result字段在成功时为SxAlarmResult对象、失败时为字符串而导致的FastJSON反序列化异常问题。

## 执行过程
```mermaid
graph TD
    A[问题:反序列化异常] --> B[定位SxAlarmResponse和SxAlarmService]
    B --> C[将SxAlarmResponse.result类型改为Object]
    C --> D[在SxAlarmService中新增Object→JSON→SxAlarmResult手动解析逻辑]
    D --> E[将token过期等业务异常的日志级别从error降为warn]
```

## 任务总结
通过将响应体result字段统一声明为Object，并在服务层手动解析，兼容了API返回结果的动态类型；同时将token失效等可预期业务异常日志降级为warn，避免污染error日志。该方案适用于下游API响应结构不稳定（success/fail返回不同数据类型）的集成场景。
