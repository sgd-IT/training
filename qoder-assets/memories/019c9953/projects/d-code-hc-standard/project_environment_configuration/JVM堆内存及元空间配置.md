---
title: "JVM堆内存及元空间配置"
usage_scenario:
    - "部署应用时配置JVM启动参数"
    - "排查OOM问题时确认内存配置是否合理"
    - "新环境搭建时设置基础运行参数"
keywords:
    - "JVM"
    - "Xmx"
    - "Xms"
    - "MetaspaceSize"
    - "JAVA_OPTS"
    - "启动参数"
---

项目JVM启动参数配置为：`-Xms512m -Xmx2g -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=512m`。可通过 `ruoyi-admin/run.bat` 启动，或在IDEA的Run/Debug Configurations中VM options设置。
