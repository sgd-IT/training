---
title: "PowerShell中使用分号;替代&&执行多命令"
usage_scenario:
    - "在Windows PowerShell中执行cd后立即运行git命令报ParserError"
    - "终端提示InvalidEndOfLine或ParentContainsErrorRecordException"
keywords:
    - "PowerShell"
    - "分号"
    - "命令分隔"
    - "ParserError"
---

在 Windows PowerShell 中，`&&` 连接符不被支持，会导致解析错误（ParserError: InvalidEndOfLine 或 ParentContainsErrorRecordException）；应改用分号 `;` 分隔多条命令，例如：`cd d:\code\maintenanc\anwei-fire-maintenance; git status --short`。注意：此语法仅适用于 PowerShell，Bash 等 Shell 使用 `&&` 而非 `;` 实现命令链式执行。
