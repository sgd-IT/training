---
title: "TCP/UDP协议组件日志规范优化"
usage_scenario:
    - "当调试协议解析问题时，需要快速定位设备发送内容与平台响应"
    - "当评估日志可读性并进行日志规范升级时"
keywords:
    - "TCP日志"
    - "UDP日志"
    - "日志规范"
    - "解析结果日志"
    - "设备上报"
source: "auto"
---

## Task description
- Core requirement: 简化并标准化TCP/UDP协议组件的日志输出，使其更直观易读，聚焦于设备发送内容、解析结果及平台响应
- Task background: 当前日志存在冗余（如双括号前缀）、格式不一致（hex输出不统一）、信息缺失（解析结果未记录）等问题；用户明确要求关注“设备发送了什么”、“解析后得到什么”、“平台响应了什么”

## Execution process
1. DahuaAduParser.java: 新增parse()方法的解析结果摘要日志，对每个解析对象生成可读性摘要（如“事件 subType=1(烟雾报警) alarmType=2”）
2. GbtTcpComponent.java: 简化日志前缀为[TCP]，使用←/→符号区分设备与平台流向，统一hex格式为%02X，并在关键入口点（如processDahuaPacket、handleDahuaRegister）添加结构化日志
3. GbtCommandSender.java: 在sendGbtPacket()中添加平台下发日志，清晰显示CMD、serial和长度
4. UdpComponent.java: 同步简化日志前缀为[UDP]，添加handleDeviceReport入口日志，并在reportRawData()中补充上报日志
5. 所有改动均经过编译验证

## Related files
- /module-eiot/iot-components/iot-component-dahua-common/src/main/java/com/enjoyiot/eiot/component/dahua/common/parser/DahuaAduParser.java
- /module-eiot/iot-components/iot-component-tcp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/ff/service/GbtTcpComponent.java
- /module-eiot/iot-components/iot-component-tcp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/ff/service/GbtCommandSender.java
- /module-eiot/iot-components/iot-component-udp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/udp/service/UdpComponent.java

## Notes
- None

## Task overview
Successfully optimized TCP/UDP protocol component logging: simplified prefixes, standardized hex format, added parsed result summaries, and clarified device-to-platform flow with ←/→ markers; all changes compiled successfully.

## 补充说明
- 后续水听器等设备类组件的日志改造沿用本条方法，并额外以「后期可追溯」为验收标准：除前缀与流向规范外，每条日志须携带可唯一定位设备与单次上报的上下文字段（设备编号、报文流水号、采样时间戳），异常分支须完整输出堆栈与入参配置值，仅打印描述性文本不足以满足追溯要求。
