---
title: "Alarm order detail dialog footer UI refinement"
usage_scenario:
    - "When refining the UI of the alarm order detail dialog footer"
    - "When fixing inconsistent button visibility logic between list cards and detail dialogs"
keywords:
    - "alarm-order"
    - "detail dialog"
    - "button visibility"
    - "isOpenStatus"
    - "UI refinement"
source: "auto"
---

## Task description
- Core requirement: Fix four UI issues in the alarm-order detail dialog footer: add isOpenStatus guard to '确认火警' and '确认设备故障' buttons, remove 'btn-cancel' class, and rename '确取视频' to '调取视频'
- Task background: The detail dialog footer had inconsistent button visibility logic between list cards and the dialog, leading to already-closed alarms being re-openable; alignment and text were also outdated.

## Execution process
1. alarm-order/index.vue: Add isOpenStatus(selectedAlarm?.status) && condition to the v-if guard of the '确认火警' button
2. alarm-order/index.vue: Add the same isOpenStatus condition to the v-if guard of the '确认设备故障' button
3. alarm-order/index.vue: Remove the 'btn-cancel' class and its associated CSS rule to restore right-aligned grouping
4. alarm-order/index.vue: Rename the text '确取视频' to '调取视频'
5. Verification: grep confirmed no leftover 'btn-cancel' or '确取视频'; sass compiled successfully; vue-tsc showed only pre-existing errors

## Related files
- /src/views/eiot/firewatch/alarm-order/index.vue

## Notes
- None

## Task overview
Successfully fixed all four UI issues in the alarm-order detail dialog footer: added missing isOpenStatus guards to prevent re-opening closed alarms, removed obsolete styling, and updated text. All verifications passed.
