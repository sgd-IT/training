---
title: "UDP/TCP协议组件日志前缀与日志级别规范"
usage_scenario:
    - "为TCP/UDP组件标准化日志前缀时"
    - "决定协议组件各类事件的日志级别"
    - "诊断协议解析问题需统一日志输出时"
keywords:
    - "log prefix"
    - "TCP协议"
    - "UDP协议"
    - "日志级别"
    - "设备上下线日志"
source: "auto"
---

UDP/TCP protocol components must use standardized log prefixes: `[UDP协议]` for UDP components, `[TCP协议]` for TCP components, and `[解析器]` for Dahua Common parsers. Important events (service startup, device online/offline) must be logged at INFO or ERROR level; frequent events (raw packet receipt, frame parsing) must be logged at DEBUG level. Device lifecycle events (online/offline) must be logged at INFO level to enable debugging.
