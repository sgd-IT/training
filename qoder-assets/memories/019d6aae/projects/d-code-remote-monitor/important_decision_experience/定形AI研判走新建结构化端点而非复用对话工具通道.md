---
title: "定形AI研判走新建结构化端点而非复用对话工具通道"
usage_scenario:
    - "为固定输入场景接入大模型输出"
    - "评估复用对话接口还是新建AI端点"
    - "决定AI结论是否写入业务表"
keywords:
    - "结构化AI端点"
    - "TOOL_CALL工具循环"
    - "LlmClient超时重载"
    - "AI结论不落业务表"
    - "内存TTL加force重算"
source: "auto"
---

Conclusion: 为"对单条告警做工整化研判"这类需求，新建定形结构化端点（POST /ai/analysis/alarm/{orderId}，返回固定 VO），底层复用 LlmClient 并自定义带读超时的 chatOnce 重载；不复用对话接口 /ai/chat/completions，也不绕道 AiChatApi 门面。

Rationale (trade-off): 数据集已由代码确定（工单字段+现成 alarm-assist 证据+历史计数），不需要模型自主找数据；对话通道会自动注入工具提示词并最多跑 5 轮 [TOOL_CALL] 文本协议循环，输出是自由文本，前端要正则剥标记、失败面大且延迟不可控；面板只有几十 px，必须拿到分数数组+结论枚举+一段建议的定形结果。调用方本身就在 module-ai-biz 内，与 AiChatApiImpl 同层，再注入自己的 Api 属于同层绕圈，且该门面固定 temperature/maxTokens、无超时参数，无法压到研判需要的低温短超时。

Rejected alternatives: ①复用 /ai/chat/completions 并在 prompt 里要求 JSON——省一个后端类，但受工具循环污染、解析不稳、等待最长 120s；②把结论写进 fire_alarm_order.ai_analysis/ai_suggestion 两列做长期缓存——列本就闲置可用，但模型输出属非权威展示信息，落业务表要先定保留期与重算策略，故先用内存 TTL+force 强制重算；③让后端 HTTP 调自己的对话接口——多绕一层网络与鉴权，明确否定。

Applicable / expiry conditions: 在"输入集固定、输出需定形、UI 空间受限"的 AI 接入场景成立（报表/月报同类）；若未来要做"用户自由提问、由模型自己决定查哪些数据"的能力，应回到 ChatController 的工具通道并把自制 [TOOL_CALL] 换成原生 tools 参数；若研判结论要用于审计或跨端复用，则需重启落库方案并先补齐重算与过期规则。适用于本仓库 module-ai 与前端面板接入；不适用于外部 Python AI 经 /ai/data/** 拉数的链路。
