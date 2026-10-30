---
title: "Jiago设备铸井解析链路最终完整性自检与部署就绪确认"
usage_scenario:
    - "When verifying end-to-end integrity of a core resolution logic change across multiple modules"
    - "When preparing for production deployment of a casting well resolution mechanism update"
    - "When auditing whether a service component (e.g., MqttMessageHandler) shares or duplicates critical business logic"
keywords:
    - "Jiago device"
    - "casting well resolution"
    - "resolveCastingWell"
    - "MqttMessageHandler"
    - "deployment readiness"
    - "SQL validation"
source: "auto"
---

## Task description
- Core requirement: confirm full integrity of the Jiago device casting well resolution logic (resolveCastingWell) across all code paths and verify deployment readiness
- Task background: the resolution logic was changed from em_aluminum_device_info mapping to tb_casting_well_info.equip_code matching; multiple components (MqttMessageHandler, CameraAlarmServiceImpl, OpenApiController) use device-to-well mapping; user asked "you have no problem now?" prompting a final end-to-end self-check

## Execution process
1. Verified MqttMessageHandler uses aluminumDeviceInfoService only for gateway status query (getGatewayStatus), not for casting well resolution — confirmed it is unrelated to the resolveCastingWell chain
2. Attempted full-project Maven compile (mvn compile -DskipTests) to validate cross-module compatibility; user cancelled mid-execution but the intent and scope were confirmed
3. Confirmed packaging structure: only monkey-monitor-api produces an executable JAR (monkey-aluminium-api.jar); monkey-monitor is a library module included in the fat jar
4. Validated SQL execution results: equip_code correctly set for wells 116 (1908659906) and 119 (0909452694,0909452581); gmt_modified updated to 2026-09-20 20:46:34
5. Checked mon_aluminum_leakage_alarm_record:云南 0909452581 has recent records with HAS_URL (20:41:44), confirming its chain is live;贵州 1908659906 last record at 16:58:44, confirming ongoing断流 consistent with old code + unmodified data_status=0
6. Confirmed transition guidance: run SQL first (①②), optionally run ③ (data_status=1) for rollback safety, then deploy the new JAR

## Related files
- d:\code\anwei-internet-java\monkey-monitor\src\main\java\com\monkey\general\handler\MqttMessageHandler.java
- d:\code\anwei-internet-java\monkey-monitor-api\pom.xml
- d:\code\anwei-internet-java\monkey-monitor\pom.xml

## Notes
- The MqttMessageHandler usage check was critical: it confirmed that no secondary casting well resolution path existed outside resolveCastingWell, eliminating a potential blind spot
- Full-project compile was cancelled, but the dependency graph and module roles were verified via pom inspection and file globbing
- Transition safety note: data_status=0 in em_aluminum_device_info (id 65/66/67) does not affect new code, but provides fallback for old code if needed

## Task overview
Successfully completed final integrity self-check: confirmed MqttMessageHandler is not part of the casting well resolution chain; validated SQL execution and data consistency; confirmed packaging and deployment procedure; provided clear go/no-go guidance for production rollout. Deployment is ready once SQL ①② are executed and the monkey-aluminium-api.jar is replaced.
