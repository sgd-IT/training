---
title: "AI模型技术栈：千问微调私有模型"
usage_scenario:
    - "评估Spring AI等框架兼容性时确认底层模型能力"
    - "对接新AI工具链时判断Function Calling等高级协议支持情况"
    - "模型升级或替换时作为基准参照"
    - "排查AI对话异常时定位是否为微调模型特有行为"
keywords:
    - "千问"
    - "Qwen"
    - "微调模型"
    - "私有大模型"
    - "OpenAI兼容接口"
source: "auto"
---

项目AI服务调用的是公司基于千问（Qwen）系列模型微调训练的私有大模型，通过OpenAI兼容接口（/v1/chat/completions）提供服务。
