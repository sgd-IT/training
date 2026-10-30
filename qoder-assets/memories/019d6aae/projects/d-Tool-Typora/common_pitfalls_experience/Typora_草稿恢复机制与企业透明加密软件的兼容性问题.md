---
title: "Typora 草稿恢复机制与企业透明加密软件的兼容性问题"
usage_scenario:
    - "When Typora shows garbled text after forced termination"
    - "When diagnosing compatibility issues between Electron apps and enterprise encryption"
keywords:
    - "Typora"
    - "enterprise encryption"
    - "draftsRecover"
    - "garbled dialog"
    - "forced termination"
source: "auto"
---

Bug class: Electron application draft recovery mechanism misinterprets encrypted binary as UTF-8 text
Root cause: Enterprise transparent encryption software (e.g., Anwei) stores encrypted binary data in Typora's draftsRecover directory; Typora's auto-recovery logic reads this binary as UTF-8, producing garbled output.
Fix pattern: Avoid forced termination of Typora; if draftsRecover contains encrypted files, manually clear the directory or configure the encryption software to exclude Typora's user data path.
Reusable lesson: Don't force-terminate Typora when enterprise encryption is active because it triggers abnormal exit and saves encrypted binary into draftsRecover, causing garbled dialogs on next startup; instead shut down gracefully or configure encryption exclusions. Applies when enterprise transparent encryption software is installed and Typora is used; does not apply to standalone Typora installations without encryption software.
