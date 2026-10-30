---
title: "findTransferDeviceId recursive parent lookup and jumpToAlarmScene conditional navigation"
usage_scenario:
    - "When modifying or debugging the alarm jump logic"
    - "When implementing new features that rely on device hierarchy or camera association"
keywords:
    - "findTransferDeviceId"
    - "jumpToAlarmScene"
    - "device hierarchy"
    - "camera association"
    - "recursive lookup"
source: "auto"
---

The project's alarm jump logic is governed by two core functions:
1. `findTransferDeviceId`: This function determines the target device for remote control. It first checks if the alert device itself is a 'transfer' device. If not, it recursively traverses up to 5 levels of the device's parent hierarchy (using `parentId`) to find the first ancestor that is a 'transfer' device. If no such device is found within 5 levels, it returns null.
2. `jumpToAlarmScene`: This function handles the navigation after an alarm is handled. It first attempts to find a transfer device using `findTransferDeviceId`. If found, it navigates to `/remote-console`. If not found, it fetches associated cameras using `getCamerasByDevice`. If cameras are found, it navigates to `/video-surveillance`; if no cameras are found, it displays a warning message '该设备未关联摄像头，无法跳转'.
