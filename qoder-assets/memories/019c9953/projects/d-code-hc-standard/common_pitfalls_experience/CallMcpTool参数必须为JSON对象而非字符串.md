---
title: "CallMcpTool参数必须为JSON对象而非字符串"
usage_scenario:
    - "调用CallMcpTool时报failed to parse CallMcpTool arguments: json: cannot unmarshal string into Go struct field"
    - "需要向MCP工具传递结构化参数时不确定JSON格式"
keywords:
    - "CallMcpTool"
    - "JSON参数"
    - "map解析"
---

CallMcpTool 工具要求 arguments 参数必须是 map[string]interface{} 类型的 JSON 对象，不能传入字符串。正确调用示例：{ "arguments": { "key": "value" } }（来源：CallMcpTool）
