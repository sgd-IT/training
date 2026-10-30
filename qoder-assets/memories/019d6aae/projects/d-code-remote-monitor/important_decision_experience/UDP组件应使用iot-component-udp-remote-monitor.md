---
title: "UDP组件应使用iot-component-udp-remote-monitor"
usage_scenario:
    - "当为大华消防协议设备选择UDP接入组件时"
    - "当评估是否需要为通用UDP组件扩展特定协议解析能力时"
keywords:
    - "UDP组件"
    - "iot-component-udp-remote-monitor"
    - "大华消防协议"
    - "协议解析"
source: "auto"
---

Conclusion: UDP组件应使用iot-component-udp-remote-monitor模块而非通用的iot-component-udp模块。
Rationale (trade-off): iot-component-udp-remote-monitor内置完整的大华消防协议解析能力（DahuaHandler、parseUniversalDataFrame等），而通用版iot-component-udp仅提供JSON/HEX/ASCII/UWB等兜底解析，无法处理GBT26875帧。
Rejected alternatives: 继续使用通用版iot-component-udp并为其添加大华解析逻辑——被拒绝，因为这会增加模块复杂度且与现有成熟方案重复。
Applicable / expiry conditions: 当设备使用大华消防协议v1.04时适用；当设备协议切换为其他协议（如Fourfaith）时，需重新评估组件选择。
