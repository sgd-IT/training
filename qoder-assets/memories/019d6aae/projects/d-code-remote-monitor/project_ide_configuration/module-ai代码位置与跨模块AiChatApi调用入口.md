---
title: "module-ai代码位置与跨模块AiChatApi调用入口"
usage_scenario:
    - "新建或盘点AI模块文件时确定代码位置"
    - "定位告警AI研判服务与结果VO代码"
    - "其他模块需调用大模型能力时找入口"
keywords:
    - "module-ai-biz"
    - "LlmClient"
    - "AlarmAnalysisService"
    - "AlarmAnalysisRespVO"
    - "chatOnce"
    - "大模型研判"
source: "auto"
---

AI数据服务的Service接口和实现均在 module-ai-biz 中(module-ai-biz/src/main/java/com/enjoyiot/module/ai/service/)，DTO 定义在 module-ai-api/src/main/java/com/enjoyiot/module/ai/dto/，Controller 在 module-ai-biz/controller/admin/。Python AI 通过 HTTP 接口调用数据，不在 api 模块放 Service。

## Notes
- 2026-09 复核补充：module-ai-biz 的 service/ 目录下除数据服务外还有大模型相关组件——LlmClient.java（LLM HTTP 客户端，配置项 ai.llm.*）、AiToolService.java（AI 工具注册与执行）；api 目录下有 AiChatApiImpl.java（供其他模块进程内单轮调用）。盘点 AI 能力时不能只看 AiDataController，工具调用链路的入口在 controller/admin/ChatController.java。
