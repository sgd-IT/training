---
title: "CallMcpTool要求arguments为JSON对象而非字符串"
usage_scenario:
    - "调用CallMcpTool时返回cannot unmarshal string错误"
    - "构造MCP工具调用参数时不确定arguments字段的数据类型"
keywords:
    - "CallMcpTool"
    - "JSON参数"
    - "arguments格式"
    - "MCP调用"
---

CallMcpTool工具调用失败，错误信息为`json: cannot unmarshal string into Go struct field CallMcpToolArgs.arguments of type map[string]interface {}`，表明arguments参数被传入了字符串而非JSON对象。正确调用格式必须为：`{"tool_name": "mysql_query", "arguments": {"query": "SELECT ..."}}`，其中arguments字段值必须是合法JSON对象（非字符串）。来源：CallMcpTool
