---
title: "GBT26875 TCP组件端口冲突诊断与pom.xml依赖修复方案"
usage_scenario:
    - "When diagnosing why a device connects to an unexpected port despite correct database configuration"
    - "When resolving multi-module TCP component conflicts in a microservice architecture"
keywords:
    - "GBT26875"
    - "TCP组件"
    - "端口冲突"
    - "pom.xml"
    - "模块依赖"
source: "auto"
---

## Task description
- Core requirement: explain why the device connected to port 18888 instead of the configured 8103, and resolve the multi-component TCP port conflict
- Task background: three GBT26875 TCP components exist in the project; the device was connecting to 18888 while user configured 8103; the root cause is a server pom.xml dependency misconfiguration that keeps the old module active while excluding the new one

## Execution process
1. Identified three TCP modules: iot-component-tcp-gbt26875 (old), iot-component-tcp-remote-monitor (new/enhanced), iot-component-tcp-gbt26875-ff (Fourfaith)
2. Verified getType() returns for each module: old and new both return "GBT26875-tcp", Fourfaith returns "GBT26875-Fourfaith-tcp"
3. Checked database eiot_component table: id=8 (GBT26875-tcp) has status=1 and config.port=8103
4. Confirmed running components via logs: old module's GbtTcpVerticle on 18888, Fourfaith module's FourfaithGbtTcpVerticle on 8103
5. Analyzed server/pom.xml: old module dependency still present, new module dependency missing
6. Determined root cause: old module remains active due to pom.xml inclusion; new module not deployed due to missing pom.xml entry; port 18888 persists because old module's stopServer() is asynchronous and fails when binding to 8103 (already occupied by Fourfaith)
7. Solution: remove old module dependency and add new module dependency in server/pom.xml

## Related files
- /server/pom.xml

## Notes
- None

## Task overview
Successfully diagnosed and resolved the TCP component port conflict: identified the three GBT26875 TCP modules and their activation mechanisms; confirmed the database configuration matches the user's intent (port 8103); traced the actual port 18888 to the old module's lingering presence due to pom.xml misconfiguration; provided the precise fix: remove old module dependency and add new module dependency in server/pom.xml.
