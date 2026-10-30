---
title: "module-ai 跨模块调用契约与自制 TOOL_CALL 工具体系结构事实"
usage_scenario:
    - "业务模块接入大模型时确定pom依赖方向与通道选型"
    - "往AI工具体系新增或修改工具时定位落点"
    - "排查AI对话未触发工具调用或流式接口不支持工具"
keywords:
    - "module-ai-api契约"
    - "LlmClient共享抽取"
    - "chatOnce与TOOL_CALL双通道"
    - "AiToolService七个工具"
    - "禁止直依ai-biz"
source: "auto"
---

本仓库大模型能力的跨模块接入结构事实：

- 共享客户端：LlmClient 从 module-ai-biz 抽出，供 ChatController 与其他业务调用方复用，避免各调用方重复实现配置读取与 HTTP 调用。
- 接口契约：在 module-ai-api 定义 AiChatApi（含 chatOnce 单轮生成方法），由 module-ai-biz 提供 AiChatApiImpl 并以 @Service 暴露，遵循本仓库既有的"api 定义接口 + biz 实现"跨模块约定（同 TenantApi、ProductApi）。AiChatApiImpl 供其他模块进程内单轮调用，不注入工具提示词。
- 依赖方向约束：业务模块（如 module-eiot-biz）只依赖 module-ai-api 做进程内调用，禁止直接依赖 module-ai-biz —— 后者在本仓库仅被 server 引用，直依会造成重依赖。
- 自制 TOOL_CALL 工具体系：module-ai-biz/.../ai/service/AiToolService.java 以 TOOL_DEFINITIONS 静态 Map 注册工具（当前 7 个：get_device_status、get_today_alarm、get_realtime_alarms(limit)、get_alarm_trend(days)、get_device_category、get_risk_distribution、get_dashboard_overview），底层数据全部复用 module-eiot firewatch 的 FireDashboardService，即 AI 工具本质是大屏统计能力的包装。协议由 ChatController.injectToolPrompt() 把工具清单与格式写进 system prompt，约定模型输出 [TOOL_CALL]{"name":"...","arguments":{...}}[/TOOL_CALL] 标记，再由 parseToolCalls() 解析、executeTool() 执行、结果以 user 消息回填上下文，最多循环 5 轮。并非 OpenAI 标准 Function Calling。
- 通道边界：只有 /ai/chat/completions（非流式）支持工具调用；/ai/chat/completions/stream（SSE）为纯透传，不具备工具能力。报表类已确定聚合数据的场景应走单轮 chatOnce，不应走多轮 TOOL_CALL 通道。
- 另有一类"给 AI 用"但不属于工具体系的接口：AiDataController，供外部 Python AI 经 HTTP 拉取告警/设备明细。

适用边界：判断任一业务模块接入大模型时的模块归属、pom 依赖方向与单轮/多轮通道选型。不适用：纯前端调用 /ai/chat/completions 的场景；也不涉及提示词写法与失败兜底策略（见"报表类AI接入与PDF生成的复用及兜底规范"）与外部 Python AI 数据查询链路细节。
