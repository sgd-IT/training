---
title: "PlatementBaseApi.java 中平台API基础地址 http://111.4.80.39:8092 的定位与用途"
usage_scenario:
    - "排查平台对接失败问题时验证基础URL配置"
    - "新成员接入Platement平台时快速查阅权威基础地址"
    - "安全审计时识别对外HTTP调用的第三方服务主机"
keywords:
    - "PlatementBaseApi"
    - "111.4.80.39:8092"
    - "平台API基础地址"
    - "LuBoxiang平台"
    - "数据推送"
source: "auto"
---

## Task description
- Core requirement: identify what platform is accessible at http://111.4.80.39:8092
- Task background: the project integrates with an external platform (LuBoxiang) for data push; multiple endpoints exist (e.g., video service at 58.19.176.195:8888); this URL was observed in code but its role was unknown

## Execution process
1. Grep for '111.4.80.39' → found matches in ruoyi-common/src/main/java/com/ruoyi/common/platement/api/PlatementBaseApi.java and ruoyi-common/src/main/java/com/ruoyi/common/paho/MqttSendMessage.java
2. Read PlatementBaseApi.java → confirmed the string "http://111.4.80.39:8092" is assigned to the BASE_URL constant, used for all HTTP requests (sendPost/sendGet/sendDelete)
3. Grep for 'Platement' and 'platement' → found application.yml references confirming it as the external platform integration point
4. Concluded this is the core HTTP base URL for Platement API, used to push work tickets, hazard reports, and other operational data to the LuBoxiang platform

## Related files
- /ruoyi-common/src/main/java/com/ruoyi/common/platement/api/PlatementBaseApi.java

## Notes
- None

## Task overview
Successfully identified and verified http://111.4.80.39:8092 as the PlatementBaseApi base URL — the authoritative HTTP endpoint for pushing operational data (work tickets, hazards) to the LuBoxiang platform. It is distinct from the video service endpoint (58.19.176.195:8888).
