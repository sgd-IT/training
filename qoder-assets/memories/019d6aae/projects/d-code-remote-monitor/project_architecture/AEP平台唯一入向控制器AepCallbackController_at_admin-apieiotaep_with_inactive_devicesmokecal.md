---
title: "AEP平台唯一入向控制器AepCallbackController at /admin-api/eiot/aep with inactive /device/smoke/callback"
usage_scenario:
    - "When verifying AEP integration endpoints for security review or API documentation"
    - "When diagnosing why AEP device commands fail to execute"
    - "When onboarding new developers to the AEP integration architecture"
keywords:
    - "AEP"
    - "AepCallbackController"
    - "/admin-api/eiot/aep"
    - "/device/smoke/callback"
    - "outbound client"
source: "auto"
---

The project has exactly one AEP inbound controller: AepCallbackController, mounted at /admin-api/eiot/aep. It exposes only one active endpoint: POST /product/smoke/callback. The device-level endpoint /device/smoke/callback is commented out and inactive. There are no AEP outbound HTTP clients or openapi configuration (no appKey/appSecret/baseUrl found anywhere in the codebase), meaning AEP smoke device remote control (downlink) is currently non-functional.
