---
title: "GBT26875 TCP component architectural comparison and activation mechanism"
usage_scenario:
    - "When debugging why a specific TCP component is handling device traffic"
    - "When configuring or enabling a new TCP component for protocol support"
keywords:
    - "GBT26875"
    - "TCP component"
    - "component activation"
    - "protocol routing"
    - "eiot_component"
source: "auto"
---

## Task description
- Core requirement: explain why TCP connections are established from iot-component-tcp-gbt26875-ff instead of iot-component-tcp-remote-monitor, and detail the differences between these two components
- Task background: the project has two GBT26875 TCP components with different capabilities; understanding their distinction is critical for protocol support and system configuration

## Execution process
1. Analyzed the core identity of both components: iot-component-tcp-gbt26875-ff uses type 'GBT26875-Fourfaith-tcp' and [四信-GBT2675] log prefix, while iot-component-tcp-remote-monitor uses type 'GBT26875-tcp' and [TCP协议] log prefix
2. Identified functional differences: iot-component-tcp-remote-monitor is the 'enhanced version' with full Dahua protocol v1.04 support (parsers, handlers,下行 services), whereas iot-component-tcp-gbt26875-ff is the 'pure version' focused solely on standard GBT26875 implementation
3. Determined the activation mechanism: components are activated based on database configuration in the eiot_component table; the component with status=1 and a valid config (host/port) is started via ThingComponent.config() polling
4. Confirmed the current state: iot-component-tcp-gbt26875-ff is enabled in the database and listening on its configured port, while iot-component-tcp-remote-monitor is likely disabled or unconfigured
5. Verified the device routing mechanism: devices are routed to components via Redis keys (str:iot:device:router:{productKey}:{deviceName}) based on the component type stored in the database

## Related files
- /iot-component-tcp-gbt26875-ff/src/main/java/com/enjoyiot/eiot/component/ff/service/FourfaithGbtTcpComponent.java
- /iot-component-tcp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/ff/service/GbtTcpComponent.java
- /module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/component/ComponentManager.java
- /module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/api/component/ComponentApiImpl.java

## Notes
- None

## Task overview
Successfully completed the architectural comparison: identified the key functional difference (Dahua protocol support vs pure GBT26875), clarified the database-driven activation mechanism (eiot_component table status/config), and confirmed the current operational state (GBT26875-Fourfaith-tcp is active and handling traffic).
