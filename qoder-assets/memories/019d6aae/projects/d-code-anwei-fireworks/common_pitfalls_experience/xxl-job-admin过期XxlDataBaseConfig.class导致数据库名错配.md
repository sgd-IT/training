---
title: "xxl-job-admin过期XxlDataBaseConfig.class导致数据库名错配"
usage_scenario:
    - "xxl-job-admin启动报Unknown database且配置文件中数据库名与错误不符"
    - "IDEA中项目启动异常但代码里找不到对应类定义"
    - "清理构建产物解决因class残留引发的运行时行为异常"
keywords:
    - "xxl-job-admin"
    - "mvn clean"
    - "过期class"
    - "数据库连接错误"
---

xxl-job-admin 启动时报 `Unknown database 'mysql_fireworks'`，根本原因是源码中 `XxlDataBaseConfig.java` 已被删除，但 `target/classes/com/xxl/job/admin/core/conf/XxlDataBaseConfig.class` 过期编译产物仍存在，被 IDEA 加载执行，其 `init()` 方法硬编码连接 `mysql_fireworks` 数据库。解决方案：进入模块根目录执行 `mvn clean` 彻底清除 target 目录（命令：`cd d:\code\anwei-fireworks\xxl-job-admin && mvn clean -q`）。（来源：Bash）
