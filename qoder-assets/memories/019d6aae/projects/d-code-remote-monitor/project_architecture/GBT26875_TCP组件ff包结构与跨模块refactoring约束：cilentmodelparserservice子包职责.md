---
title: "GBT26875 TCP组件ff包结构与跨模块refactoring约束：cilent/model/parser/service子包职责"
usage_scenario:
    - "When adding new GBT26875 protocol features across modules"
    - "When refactoring existing GBT26875 protocol logic (e.g., vendor abstraction)"
keywords:
    - "GBT26875"
    - "TCP component"
    - "ff package"
    - "cilent"
    - "model"
    - "parser"
    - "service"
    - "refactoring"
source: "auto"
---

The GBT26875 protocol implementation resides in the 'ff' package (com.enjoyiot.eiot.component.ff.*), shared across multiple modules including iot-component-tcp-remote-monitor and iot-component-tcp-gbt26875-ff. Its sub-package structure defines key extension points: 'cilent' for client-side communication logic, 'model' for configuration and domain objects, 'parser' for binary frame decoding/encoding, 'parser/handler' for protocol-specific handlers, and 'service' for business orchestration. Refactoring (e.g., Fourfaith→Gbt) must update files across all these sub-packages consistently.
