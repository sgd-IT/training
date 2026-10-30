---
title: "修复推送JSON子字段数组结构以匹配平台要求"
usage_scenario:
    - "对接外部平台时校验并修正JSON序列化结构（对象vs数组）"
    - "排查因空集合初始化方式错误导致的API兼容性问题"
    - "类似PlatementTicketNewApi的OpenAPI推送逻辑改造"
keywords:
    - "JSON序列化"
    - "空数组"
    - "平台兼容"
---

## 任务描述
修正 PlatementTicketNewApi 中 videos/sensors/certificates 等字段的初始化方式，使其推送格式严格匹配园区平台要求的 JSON 结构（子字段必须为数组 `[]`，而非空对象 `{}`）。

## 执行过程
```mermaid
graph TD
    A[用户反馈平台只收 {} 不收 []] --> B[误判需求：将所有字段改为 new JSONObject()]
    B --> C[用户提供完整示例 JSON]
    C --> D[识别关键矛盾：示例中 videos/sensors 等字段实际为 [{...}] 数组格式]
    D --> E[纠正错误：全部改回 new ArrayList<>()]
    E --> F[确认 operatorInfo 分隔符、主对象结构等其余字段均符合示例]
```

## 任务总结
成功修复推送结构：`videos`/`sensors`/`certificates`/`signatureDtos`/`steps`/`tools`/`approves` 字段统一初始化为 `new ArrayList<>()`，确保无数据时序列化为 `[]`，有数据时为 `[{}]`，完全对齐平台示例；同时明确 `operatorInfo` 多人分隔符为英文逗号，非分号。
