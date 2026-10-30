---
title: "Function Calling协议支持性验证方法与本仓库双通道实现现状"
usage_scenario:
    - "对接新LLM服务端时快速验证工具调用能力"
    - "排查工具调用未生效是协议层还是解析层问题"
    - "评估改用标准Function Calling替代文本标记的可行性"
keywords:
    - "Function Calling"
    - "协议验证"
    - "tools字段"
    - "tool_calls"
    - "TOOL_CALL标记"
    - "LlmClient"
    - "/ai/chat/completions"
    - "ai-analysis页面"
source: "auto"
---

验证服务端是否支持OpenAI标准Function Calling协议的方法：向/v1/chat/completions接口发送包含tools字段的POST请求，根据响应判断——若返回tool_calls字段则支持；若静默忽略tools或报错unknown parameter: tools则不支持。

## Notes
- 2026-09 复核补充（本仓库 module-ai 实测）：本项目 LlmClient.buildRequest() 确实随请求体发送标准 tools 字段、ChatRespVO 也含 tool_calls 字段，说明协议层已按 OpenAI Function Calling 对接；但 ChatController 在流式/纯透传等场景之外另用 [TOOL_CALL] 文本标记注入 system prompt 并自行解析作为工具触发通道。因此不得再笼统断言"系统未使用标准 Function Calling"，应区分：协议请求层走标准 tools/tool_calls，聊天控制层叠加了自制文本标记解析。判断某 LLM 服务端能力时本验证方法仍然有效。
