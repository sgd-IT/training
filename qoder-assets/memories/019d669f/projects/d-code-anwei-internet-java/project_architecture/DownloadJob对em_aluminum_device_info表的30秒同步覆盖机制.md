---
title: "DownloadJob对em_aluminum_device_info表的30秒同步覆盖机制"
usage_scenario:
    - "When diagnosing why manual local database changes to em_aluminum_device_info are not persistent"
    - "When designing long-term solutions for device mapping state management that must survive the sync cycle"
keywords:
    - "DownloadJob"
    - "em_aluminum_device_info"
    - "30秒同步"
    - "saveOrUpdate"
    - "覆盖机制"
source: "auto"
---

The project's data synchronization architecture has a critical invariant: the local em_aluminum_device_info table is updated every 30 seconds by DownloadJob, which calls DownloadServiceImpl.updateData(). This method performs a saveOrUpdate() operation on each row, meaning any manual local change to a row (e.g., setting data_status=1) will be overwritten by the next sync if the corresponding cloud record has a different value (e.g., data_status=0). This is the root cause of persistent data_status corruption.
