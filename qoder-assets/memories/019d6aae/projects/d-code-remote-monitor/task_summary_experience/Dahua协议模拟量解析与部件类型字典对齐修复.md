---
title: "Dahua协议模拟量解析与部件类型字典对齐修复"
usage_scenario:
    - "When debugging why telemetry_universal_raw/value remain null despite correct ADU parsing"
    - "When aligning component type names with GB/T 26875 protocol document table 2-1"
keywords:
    - "Dahua协议"
    - "模拟量解析"
    - "部件类型字典"
    - "GBT26875"
    - "DahuaAduParser"
source: "auto"
---

## Task description
- Core requirement: fix `telemetry_universal_raw=null` and `telemetry_universal_value=null` for all 1708 analog objects; add missing component type 86 ("输出模块") and 132 ("湿度传感器") to the dictionary; verify centerAddr config is set.
- Task background: DahuaAduParser’s `decodeAnalog` appends `_0` suffix to keys (`data_analogRaw_0`), but GbtDataReporter reads `data_analogRaw`; component type mapping in `getComponentTypeName` lacked 86/132 per protocol doc; `centerAddr` was unconfigured in UDP JSON.

## Execution process
1. `GbtDataReporter.java`: updated lines 357–358 to read `entry.get("data_analogRaw_0")` and `entry.get("data_analogValue_0")` instead of `data_analogRaw/data_analogValue`.
2. `DahuaAduParser.java`: added `case 86: return "输出模块"; case 132: return "湿度传感器";` to `getComponentTypeName` switch.
3. Verified `DahuaComponentTypeConstants.java` already matched protocol doc — no change needed.
4. Confirmed `centerAddr` is a configuration field requiring manual setup in UDP component JSON; no code change required.

## Related files
- /iot-component-tcp-remote-monitor/src/main/java/com/enjoyiot/eiot/component/ff/service/GbtDataReporter.java
- /iot-component-dahua-common/src/main/java/com/enjoyiot/eiot/component/dahua/common/parser/DahuaAduParser.java

## Notes
- Initially guessed wrong component type names (86="气体灭火控制器", 132="消防电源监控"); corrected after verifying protocol document images.
- `centerAddr` is a deployment-time configuration, not a code defect — must be set by user in UDP JSON.

## Task overview
Successfully fixed analog null issue by aligning key suffix usage between parser and reporter; completed component type dictionary alignment per GB/T 26875 protocol document table 2-1; confirmed centerAddr is a configuration item requiring manual setup — all verified via compilation and protocol doc cross-check.
