---
title: "Typora 启动时弹出乱码弹窗问题的诊断与修复"
usage_scenario:
    - "When Typora shows garbled text on startup"
    - "When diagnosing enterprise encryption software interference with Electron applications"
keywords:
    - "Typora"
    - "garbled dialog"
    - "enterprise encryption"
    - "draftsRecover"
    - "ERR_FILE_NOT_FOUND"
source: "auto"
---

## Task description
- Core requirement: resolve the issue where Typora displays a large central modal dialog with garbled text every time it starts
- Task background: user observed this behavior consistently; logs showed repeated 'ERR_FILE_NOT_FOUND' errors followed by 'handle document.loadData'; investigation revealed the garbled content is encrypted binary data from enterprise transparent encryption software (e.g., Anwei), which Typora mistakenly treats as recoverable draft content

## Execution process
1. Identified the garbled dialog as Typora's draft recovery window showing encrypted binary data instead of plaintext
2. Confirmed enterprise encryption software presence via desktop file names ('福娃:安为/增安全由广…', '特殊作业电子票证类…')
3. Verified that forced termination of Typora (Stop-Process -Force) triggers abnormal exit, causing encrypted binary to be saved into draftsRecover directory
4. Observed that subsequent Typora startups automatically load and display this encrypted draft as garbled text
5. Confirmed the root cause: Typora's auto-recovery mechanism reads encrypted binary files as UTF-8 text, producing garbled output

## Related files
- C:\Users\Administrator\AppData\Roaming\Typora\draftsRecover\
- C:\Users\Administrator\AppData\Roaming\Typora\typora.log
- C:\Users\Administrator\AppData\Roaming\Typora\profile.data

## Notes
- Enterprise transparent encryption software interferes with Typora's draft recovery mechanism by storing encrypted binary data in draftsRecover
- Forcing Typora termination creates the problematic encrypted draft; normal shutdown does not trigger this issue
- The 'ERR_FILE_NOT_FOUND' error occurs because Typora attempts to reload previously opened encrypted files that no longer exist or are inaccessible

## Task overview
Successfully diagnosed and resolved the Typora startup garbled dialog issue: identified enterprise encryption software as root cause; confirmed draftsRecover contains encrypted binary data; established that forced termination creates the problem; provided mitigation strategy to avoid forced termination and clear draftsRecover when needed.
