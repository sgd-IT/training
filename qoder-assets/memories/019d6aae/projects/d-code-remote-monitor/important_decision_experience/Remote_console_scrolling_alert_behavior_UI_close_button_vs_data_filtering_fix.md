---
title: "Remote console scrolling alert behavior: UI close button vs data filtering fix"
usage_scenario:
    - "When deciding how to address persistent UI elements caused by underlying data issues"
    - "When balancing immediate UI fixes against root-cause data fixes"
keywords:
    - "scrolling alert"
    - "UI close button"
    - "data filtering"
    - "remote console"
    - "engineering trade-off"
source: "auto"
---

Conclusion: Choose between adding a '×' close button to the remote console's scrolling alert or fixing the underlying data issue where 'slightly later' status is not excluded from the unprocessed list.
Rationale (trade-off): Adding a UI close button provides immediate user relief and stops the alert from persisting; fixing the data issue at the source ('slightly later' status should be excluded from the unprocessed list) prevents the problem from recurring and ensures consistency across all interfaces (duty-screen popups and remote-console alerts).
Rejected alternatives: A third option of changing the display scope of the scrolling alert to only show alerts related to the current alarm device was considered but not chosen as the primary solution.
Applicable / expiry conditions: This decision holds while the 'slightly later' status is used in the system; it must be re-evaluated if the status semantics change or if the alert display logic is refactored.

## Notes
- 实施进展（后续会话）：选项 A 已落地于 remote-console/index.vue —— 小卡加"×收起"，收起后本次会话静默（页面内存态 + 记录已见过的告警 id），轮询继续，检测到未见过的 id 时自动重弹并把轮播索引定位到那条新告警，刷新复位。
- 语义边界：收起只静默"已见过的那批"，并不修复 handle_status=1 被计入未处置回捞的数据侧根因；该项（原 B）与小卡显示口径（原 C）仍未拍板，不能因为 A 已上线就认为问题已根治。
