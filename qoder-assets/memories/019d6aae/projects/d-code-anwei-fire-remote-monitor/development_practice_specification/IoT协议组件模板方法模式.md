---
title: "IoT协议组件模板方法模式"
usage_scenario: []
keywords:
    - "模板方法"
    - "协议组件"
    - "ThingComponent"
source: "auto"
---

IoT协议组件体系采用模板方法模式：抽象基类ThingComponent定义统一的消息路由骨架（如serviceInvoke、propertyGet/Set等钩子方法），各具体协议组件（GB/T 26875、四信私有协议、MQTT等）仅需实现协议差异化逻辑，公共能力（组件注册、设备路由、消息上报）全部复用。
