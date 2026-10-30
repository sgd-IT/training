---
title: "HikVisionUtil初始化失败因hikang.url配置不全"
usage_scenario:
    - "Spring Boot应用启动报URI is not absolute且堆栈指向HikVisionUtil.init()"
    - "海康云API集成时RestTemplate构造URI失败"
    - "application-dev.yml中hikang相关配置仅部分存在"
keywords:
    - "HikVisionUtil"
    - "URI is not absolute"
    - "application-dev.yml"
    - "hikang.url"
---

Spring Boot 应用中 `HikVisionUtil` 初始化失败报 `java.lang.IllegalArgumentException: URI is not absolute`，根本原因是 `application-dev.yml` 中 `hikang.url` 下缺少必要子配置项（`token`、`group`、`peopleUrl`、`closeUrl`），导致代码中 `new URI(…)` 构造空或相对路径字符串。必须补全以下4项绝对URI：`token: https://api2.hik-cloud.com/oauth/token`、`group: https://api2.hik-cloud.com/api/v1/mq/consumer/group2`、`peopleUrl: https://api2.hik-cloud.com/api/v1/mq/consumer/messages`、`closeUrl: https://api2.hik-cloud.com/siot/video/api/standardStreams/actions/disableLiveAddress`（来源：应用启动日志 + Read/SearchReplace）
