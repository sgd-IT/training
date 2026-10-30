---
title: "SD接口dosage字段转JSON数字格式"
usage_scenario:
    - "对接外部平台时需将Java数值字段强制序列化为JSON数字（非字符串）"
    - "FastJSON等序列化器无法直接控制字段类型时的后处理方案"
    - "类似字段如storeroomArea、longitude等需同样处理时的参考模式"
keywords:
    - "dosage"
    - "JSON数字"
    - "__NUM__标记"
---

## 任务描述
解决 SD 平台接口要求 dosage 字段为 JSON 数字类型（无引号）、保留两位小数的问题。

## 执行过程
```mermaid
graph TD
    A[需求: dosage 必须是 JSON 数字] --> B[发现 FastJSON 序列化为字符串 "dosage":"28260.00"]
    B --> C[尝试正则提取日志失败：Java 字符串转义错误]
    C --> D[重构方案：生成侧加 __NUM__ 前缀]
    D --> E[修改 SetSDUploadInfo.java：dosage 值设为 "__NUM__" + fmt2DecimalStr]
    E --> F[修改 RequestSDMethod.java：用 replaceAll 去引号+去前缀]
    F --> G[日志验证：修复前/后 dosage 格式正确]
```

## 任务总结
成功实现 dosage 字段 JSON 数字化：在 SetSDUploadInfo.java 中对 dosage 插入 __NUM__ 前缀，在 RequestSDMethod.java 中通过正则 replaceAll("\"__NUM__([\\d.]+)\"", "$1") 转换为无引号数字，确保最终发送为 "dosage":28260.00。
