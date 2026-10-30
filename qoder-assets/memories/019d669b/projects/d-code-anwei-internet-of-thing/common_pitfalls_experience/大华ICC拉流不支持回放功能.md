---
title: "大华ICC拉流不支持回放功能"
usage_scenario: []
keywords:
    - "大华ICC"
    - "回放功能"
    - "CamerasController"
---

大华ICC拉流（pullType == 1）在回放接口 `/getHuifang` 中被显式排除，日志明确提示'大华ICC暂不支持回放功能'，且 `IccDeviceService` 接口未定义任何录像回放方法。（来源：CamerasController.java）
