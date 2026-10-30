---
title: "PowerShell中mysql密码含@导致参数截断"
usage_scenario:
    - "Windows下mysql命令因密码含@而报错command not found或认证失败"
    - "PowerShell执行含特殊字符的shell命令时参数被意外截断"
keywords:
    - "PowerShell"
    - "mysql密码"
    - "@符号"
    - "参数截断"
---

## Usage Scenario
Description: 在Windows PowerShell中执行多条命令时避免解析错误
Examples: 执行cd后立即运行git命令报ParserError | 终端提示InvalidEndOfLine或ParentContainsErrorRecordException | Windows下mysql命令因密码含@而报错command not found或认证失败 | PowerShell执行含特殊字符的shell命令时参数被意外截断 | 在Qoder沙箱中执行mysql命令时PowerShell反复崩溃报top=-2错误 | 需要在Windows沙箱环境稳定执行数据库CLI命令

## Usage Method
PowerShell: 将整个`-e`参数用单引号包裹，或切换至cmd.exe环境执行；多条命令间使用分号 `;` 分隔，而非 `&&` 连接符；在Qoder沙箱中应绕过PowerShell，直接在Bash环境中运行mysql命令

## Notes
- 在 Windows PowerShell 中，`&&` 连接符不被支持，会导致解析错误（ParserError: InvalidEndOfLine）；应改用分号 `;` 分隔多条命令
- 在Windows PowerShell中执行mysql命令时，若密码含特殊字符（如`@`），PowerShell会提前截断参数，导致认证失败；应将整个`-e`参数用单引号包裹
- 在 Qoder 沙箱环境中，使用 PowerShell 执行 mysql 命令时，PSReadLine 会因控制台缓冲区问题频繁抛出 `System.ArgumentOutOfRangeException: 参数名: top, 实际值是 -2` 异常，导致命令无法执行；应绕过 PowerShell，直接在 Bash 环境中运行 mysql 命令
