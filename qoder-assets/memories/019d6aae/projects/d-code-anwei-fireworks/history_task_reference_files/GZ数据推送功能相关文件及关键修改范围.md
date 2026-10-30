---
title: "GZ数据推送功能相关文件及关键修改范围"
usage_scenario:
    - "GZ推送功能二次开发或缺陷修复时快速定位核心文件"
    - "回归测试时重点验证access_status、经纬度、企业去重等逻辑的代码位置"
keywords:
    - "GZ推送"
    - "AlramInfoServiceImpl"
    - "PushingGZDataService"
    - "SetGZUploadInfo"
    - "RequestGZMethod"
    - "UploadGZScheduledJob"
source: "auto"
---

- /monkey-monitor/src/main/java/com/monkey/general/modules/em/service/impl/AlramInfoServiceImpl.java#L319-L319
- /monkey-monitor/src/main/java/com/monkey/general/platform/push/gz/PushingGZDataService.java#L140-L1052
- /monkey-monitor/src/main/java/com/monkey/general/platform/push/gz/SetGZUploadInfo.java#L47-L680
- /monkey-monitor/src/main/java/com/monkey/general/util/gz/util/RequestGZMethod.java#L203-L215
