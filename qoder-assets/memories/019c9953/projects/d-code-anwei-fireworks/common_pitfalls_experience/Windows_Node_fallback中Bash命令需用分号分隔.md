---
title: "Windows Node fallback中Bash命令需用分号分隔"
usage_scenario:
    - "在Windows上执行含cd的Git命令时报ParserError或InvalidEndOfLine"
    - "使用Node fallback执行Bash命令时命令链失败"
keywords:
    - "Windows"
    - "Node fallback"
    - "PowerShell"
    - "Bash命令分隔"
---

在Windows环境下通过Node fallback执行Bash命令时，PowerShell无法识别`&&`作为命令分隔符，会报错`InvalidEndOfLine`。必须改用分号`;`连接多条命令，例如：`cd d:\code\anwei-fireworks; git status`（来源：Bash）
