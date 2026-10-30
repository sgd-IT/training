---
title: "本机 pnpm 命令需通过 pnpm.cmd 调用"
usage_scenario:
    - "在终端执行前端 pnpm 命令"
    - "非交互 shell 调用 pnpm 报错时"
    - "排查 PowerShell 脚本执行策略问题"
keywords:
    - "pnpm.cmd"
    - "ExecutionPolicy"
    - "PowerShell 脚本禁止"
    - "前端命令调用"
    - "pnpm 版本"
source: "auto"
---

本机（Windows）+ PowerShell 5.1 环境下，执行策略禁止运行 D:\nodejs\pnpm.ps1，直接调用 pnpm 会报 SecurityError/UnauthorizedAccess（PSSecurityException）。非交互 shell 或脚本中调用 pnpm 时应改用 pnpm.cmd，或使用 cmd /c pnpm。已验证环境：node v22.23.2、pnpm v11.22.0。
