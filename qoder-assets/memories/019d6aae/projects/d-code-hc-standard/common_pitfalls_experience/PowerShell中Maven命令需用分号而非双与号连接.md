---
title: "PowerShell中Maven命令需用分号而非双与号连接"
usage_scenario:
    - "在Windows环境下通过终端执行mvn命令失败并提示ParserError或InvalidEndOfLine"
    - "PowerShell中链式执行cd和mvn命令时报语法错误"
keywords:
    - "PowerShell"
    - "Maven"
    - "命令连接符"
source: "auto"
---

在Windows PowerShell中执行Maven命令时，不能使用'&&'连接命令，应改用';'分隔（如'cd path; mvn compile'）。否则会报'InvalidEndOfLine'解析错误。（来源：Bash）
