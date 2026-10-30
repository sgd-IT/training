---
title: "漏铝设备data_status总是变成0的根因诊断：云端下发同步覆盖机制"
usage_scenario:
    - "Diagnosing why manual database changes to em_aluminum_device_info are not persistent"
    - "Understanding the cloud-to-local data synchronization mechanism in the anwei-internet-java project"
keywords:
    - "data_status"
    - "DownloadJob"
    - "DownloadServiceImpl"
    - "cloud-to-local sync"
    - "em_aluminum_device_info"
source: "user"
---

## Task description
- Core requirement: diagnose why the data_status of Jiago leak-alarm devices always reverts to 0 after being manually set to 1, causing device unavailability.
- Task background: The user observed that manually setting data_status=1 in the local database was immediately reverted to 0. Previous memories identified em_aluminum_device_info as the mapping table and data_status=1 as the activation flag, but did not explain the persistent overwrite mechanism.

## Execution process
1. Code tracing: Identified DownloadJob (runs every 30 seconds) as the local polling task that fetches incremental data from the cloud.
2. Code tracing: Confirmed DownloadServiceImpl.updateData() is called by DownloadJob; it processes the downloaded zip and performs saveOrUpdate() on each row of em_aluminum_device_info.
3. Root cause conclusion: The local database's em_aluminum_device_info is overwritten by the cloud's version during each sync cycle. If the cloud record has data_status=0, any local change is lost.
4. Verification: This explains why manual local updates are transient — they are only effective until the next DownloadJob run.

## Related files
- d:\code\anwei-internet-java\monkey-monitor-api\src\main\java\com\monkey\general\data\download\DownloadJob.java
- d:\code\anwei-internet-java\monkey-monitor-api\src\main\java\com\monkey\general\data\download\DownloadServiceImpl.java
- d:\code\anwei-internet-java\monkey-monitor-api\src\main\java\com\monkey\general\modules\mon\JiagoVideoLeakageController.java

## Notes
- None

## Task overview
Successfully diagnosed the root cause of the persistent data_status=0 issue: the DownloadJob's cloud-to-local synchronization mechanism (via DownloadServiceImpl.updateData()) overwrites local changes to em_aluminum_device_info. This means any manual local fix is temporary and will be reverted on the next sync unless the cloud record is also updated.

## 订正（2026-09-20 架构变更后）
resolveCastingWell 第二级已改为匹配 tb_casting_well_info.equip_code，不再读取 em_aluminum_device_info，“data_status 被同步覆盖 → 漏铝设备断流”的因果链不再成立。云端→本地同步覆盖机制本身未变；漏铝设备适配改由在目标铸造井录入 equip_code 承担。
