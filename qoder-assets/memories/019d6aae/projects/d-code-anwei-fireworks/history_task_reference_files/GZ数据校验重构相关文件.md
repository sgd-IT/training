---
title: "GZ数据校验重构相关文件"
usage_scenario:
    - "后续对GZDataValidator新增校验类型时快速定位其调用入口"
    - "回归测试GZ数据推送功能时重点检查这4个文件的协作逻辑"
    - "审计GZ模块校验职责是否清晰分层"
keywords:
    - "GZ校验"
    - "GZDataValidator"
    - "PushingGZDataService"
---

- /monkey-monitor/src/main/java/com/monkey/general/util/gz/util/GZDataValidator.java
- /monkey-monitor/src/main/java/com/monkey/general/util/gz/util/GZDataFilter.java#L24-L42
- /monkey-monitor/src/main/java/com/monkey/general/platform/push/gz/PushingGZDataService.java
- /monkey-monitor/src/main/java/com/monkey/general/platform/push/gz/SetGZUploadInfo.java#L501-L578
