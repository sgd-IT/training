---
title: "MQTT协议版本能力：客户端锁死3.1.1，内置broker兼容v3/v5"
usage_scenario:
    - "评估对接MQTT v5设备或平台的能力边界"
    - "排查MQTT连接协议版本不匹配问题"
    - "判断系统主动publish能否连v5 broker"
keywords:
    - "vertx-mqtt"
    - "MQTT 3.1.1"
    - "MqttClientImpl"
    - "netty-codec-mqtt"
    - "协议版本协商"
source: "auto"
---

消防远程监控后端(anwei-fire-remote-monitor)的 MQTT 协议版本能力经反编译 vertx-mqtt 4.5.13 + netty-codec-mqtt 4.1.116 字节码验证：
- 作为 MQTT 客户端时写死 MQTT 3.1.1：io.vertx.mqtt.impl.MqttClientImpl.doConnect 构建 CONNECT 报文为 new MqttConnectVariableHeader("MQTT", 4, ...)，协议级别 4 = 3.1.1；MqttClientOptions 无任何协议版本开关。故 emqx 组件(连外部 EMQX)与规则引擎 MqttClientLink 主动发起的会话一律 3.1.1，无 v5 客户端能力。
- 内置 broker(iot-component-mqtt)不锁版本：MqttServerConnection.handleConnect 把 CONNECT 里的 version() 原样存入 MqttEndpointImpl.protocolVersion，全类无"必须==4"校验；MqttEndpointImpl 有 protocolVersion>=MQTT_5.protocolLevel() 的双版本分支。netty MqttVersion 枚举认识 MQIsdp/3、MQTT/4、MQTT/5，即设备端用 3.1/3.1.1/5.0 都能连入 broker。
- 前端调试页 MqttClient.vue 用 mqtt.js ^4.3.7，未配 protocolVersion，走默认 4(=3.1.1)。
适用：评估能否对接 v5 设备/broker 时。注意当前消防业务主线(大华UDP/GBT26875 TCP)未启用 MQTT，此差异暂无实际影响。
