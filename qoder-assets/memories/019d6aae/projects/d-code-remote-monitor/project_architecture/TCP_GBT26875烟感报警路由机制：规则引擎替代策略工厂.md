---
title: "TCP GBT26875烟感报警路由机制：规则引擎替代策略工厂"
usage_scenario:
    - "When extending TCP alarm handling logic"
    - "When debugging why TCP smoke alarms do not appear in the alert center"
    - "When adding new TCP device types that require custom alert routing"
keywords:
    - "TCP GBT26875"
    - "rule engine"
    - "alarm strategy"
    - "eiot_rule_info"
    - "eiot_alert_config"
source: "auto"
---

AlarmSourceType.TCP_GBT26875 enum exists in the codebase, but there is no corresponding strategy implementation class. Real TCP-based smoke fire alarms are handled not through the alarm strategy factory, but via the rule engine: eiot_rule_info (id=34) defines the listener and eiot_alert_config (id=18) defines the action, forming a direct routing path from TCP component to alert table.
