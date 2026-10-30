---
title: "firewatch 模块 DO 类统一存放路径"
usage_scenario:
    - "When adding new firewatch domain entities"
    - "When refactoring firewatch persistence logic"
    - "When onboarding developers and explaining firewatch module structure"
keywords:
    - "firewatch"
    - "DO"
    - "package"
    - "dataobject"
    - "firewatch module"
source: "auto"
---

The firewatch module's data object (DO) classes are all located under the Java package `com.enjoyiot.module.eiot.dal.dataobject.firewatch` in the `module-eiot-biz` module. This path is the canonical location for all firewatch-related domain entities, including FirePropertyDO, FireDutyScheduleDO, FirePatrolTaskDO, FireAlarmOrderDO, and others. New DO classes for the firewatch module must be placed here to maintain consistency and discoverability.
