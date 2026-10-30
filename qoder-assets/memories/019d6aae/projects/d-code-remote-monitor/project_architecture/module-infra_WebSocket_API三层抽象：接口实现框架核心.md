---
title: "module-infra WebSocket API三层抽象：接口/实现/框架核心"
usage_scenario:
    - "When adding new cross-module WebSocket functionality and deciding where to define the interface"
    - "When debugging why a WebSocket-related bean injection fails and verifying layer boundaries"
    - "When onboarding new developers to understand the project’s framework encapsulation strategy"
keywords:
    - "WebSocketSenderApi"
    - "module-infra-api"
    - "WebSocketMessageSender"
    - "API分层"
    - "跨模块API"
source: "auto"
---

The project implements a three-layer WebSocket abstraction:
- Interface: `WebSocketSenderApi` defined in `module-infra-api/src/main/java/com/enjoyiot/module/infra/api/websocket/WebSocketSenderApi.java`, providing default JSON serialization methods for business modules.
- Implementation: `WebSocketSenderApiImpl` in `module-infra-biz/src/main/java/com/enjoyiot/module/infra/api/websocket/WebSocketSenderApiImpl.java`, annotated with `@Component`, which delegates all calls to the framework's `WebSocketMessageSender`.
- Framework core: `com.enjoyiot.framework.websocket.core.sender.WebSocketMessageSender` in `framework/spring-boot-starter-websocket/src/main/java/com/enjoyiot/framework/websocket/core/sender/WebSocketMessageSender.java`, providing the actual sending logic across 5 transport modes (local/redis/rocketmq/kafka/rabbitmq).
