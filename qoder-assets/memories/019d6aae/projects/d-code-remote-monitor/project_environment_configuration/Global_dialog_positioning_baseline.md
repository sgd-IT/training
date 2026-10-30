---
title: "Global dialog positioning baseline"
usage_scenario:
    - "When adding or modifying dialogs in the project"
    - "When troubleshooting inconsistent dialog positioning across different components"
    - "When implementing vertical centering for viewing dialogs"
keywords:
    - "dialog positioning"
    - "global baseline"
    - "Element Plus"
    - "align-center"
    - "max-height safety"
source: "auto"
---

The project uses a global dialog positioning baseline defined in styles/index.scss: --el-dialog-margin-top: max(56px, 10vh); This provides a consistent top margin for all dialogs across different screen sizes (56px minimum for small screens, scaling up to 10vh for larger screens). For dialogs using align-center, additional safety measures are implemented: the overlay is set to display: flex with max-height: calc(100vh - 112px), and the dialog body has min-height: 0 and overflow-y: auto to prevent content overflow issues.
