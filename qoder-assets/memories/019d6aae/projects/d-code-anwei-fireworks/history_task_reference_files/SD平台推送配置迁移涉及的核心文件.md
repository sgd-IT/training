---
title: "SD平台推送配置迁移涉及的核心文件"
usage_scenario:
    - "对SD平台推送功能进行二次开发或问题排查时快速定位关键代码与配置"
    - "回归测试时验证所有相关配置文件是否已同步清理"
keywords:
    - "SD推送"
    - "配置文件"
    - "数据库驱动"
---

- /monkey-monitor/src/main/java/com/monkey/general/util/sd/entity/SDConfigEntity.java
- /monkey-monitor/src/main/java/com/monkey/general/platform/push/sd/PushingSDDataService.java
- /monkey-monitor-api/src/main/resources/application-dev.yml
- /monkey-monitor-api/src/main/resources/application-prod.yml
- /deploy/config/monitor-api/application-prod.yml
