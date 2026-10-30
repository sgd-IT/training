---
title: "Alarm jump logic modified to prevent navigation when no associated camera is found"
usage_scenario:
    - "When modifying alarm-related navigation logic to improve user experience"
    - "When implementing defensive UI patterns for missing data"
keywords:
    - "alarm jump"
    - "no associated camera"
    - "warning message"
    - "duty-screen"
    - "jumpToAlarmScene"
source: "auto"
---

## Task description
- Core requirement: Modify the alarm jump logic so that when there is no associated camera for a device, it does not navigate to the video surveillance page but instead displays a warning message.
- Task background: The existing logic in jumpToAlarmScene would always attempt to navigate to /video-surveillance, even if no camera was found, leading to an empty page. The new logic adds a check and a user-facing warning.

## Execution process
1. Analyzed the current jumpToAlarmScene function in duty-screen/index.vue to understand the existing flow.
2. Identified the section responsible for fetching cameras and navigating to the video surveillance page.
3. Modified the code to first fetch the list of associated cameras using getCamerasByDevice.
4. Added a conditional check: if the resulting camera list is empty or undefined, display an ElMessage.warning instead of navigating.
5. Verified the ESLint compliance of the modified file.

## Related files
- /anwei-fire-remote-monitor-vue/src/views/eiot/firewatch/duty-screen/index.vue

## Notes
- None

## Task overview
Successfully modified the alarm jump logic: the jumpToAlarmScene function now checks for associated cameras before navigation. If none are found, it displays a warning message '该设备未关联摄像头，无法跳转' instead of navigating to an empty video surveillance page. ESLint verification passed with zero errors.
