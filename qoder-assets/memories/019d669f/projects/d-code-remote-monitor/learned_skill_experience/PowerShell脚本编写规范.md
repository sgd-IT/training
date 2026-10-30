---
title: "PowerShell脚本编写规范"
usage_scenario:
    - "编写需在CI/CD中稳定运行的PowerShell部署脚本"
    - "编写需被其他语言进程调用的PowerShell工具脚本"
keywords:
    - "PowerShell"
    - "命令链式执行"
    - "UTF-8"
    - "脚本规范"
    - "编码安全"
source: "auto"
---

## Goal
编写可维护、可移植、跨环境一致的PowerShell脚本，避免编码错误、管道中断和隐式类型转换问题。

## Steps
Step 1: 命令链式执行 — 使用`&&`或`-and`替代分号`;`连接多条命令，确保前序失败时后续不执行；对关键步骤显式检查`$LASTEXITCODE`或`$?`；如需兼容旧版PowerShell（< 7.0），使用`if ($?) { ... } else { exit 1 }`结构。
Step 2: UTF-8文件读写 — 使用`Get-Content -Encoding UTF8`和`Set-Content -Encoding UTF8`；避免`Out-File`（默认BOM）；如需BOM-free UTF-8，用`[System.Text.UTF8Encoding]::new($false)`配合`[System.IO.File]::WriteAllText()`；在CI/CD中统一设置`$PSDefaultParameterValues['*:Encoding'] = 'UTF8'`以全局覆盖默认行为。

## Notes
- 使用分号`;`连接命令导致前序失败后仍继续执行，造成状态污染，必须改用逻辑运算符或显式错误检查
- `Out-File`默认写入UTF-8 with BOM，被其他工具（如Python `open()`）误读为非UTF-8，必须指定`-Encoding UTF8`或改用底层API
- PowerShell 5.1及以下版本`-Encoding UTF8`仍可能写入BOM，必须用`[System.IO.File]::WriteAllText()`绕过
