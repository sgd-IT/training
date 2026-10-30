---
title: "PowerShell中目录递归列出文件名的正确命令"
usage_scenario:
    - "在PowerShell中执行dir命令报PositionalParameterNotFound错误"
    - "需要在Windows PowerShell中递归列出指定目录下所有文件名"
keywords:
    - "PowerShell"
    - "dir"
    - "Get-ChildItem"
    - "递归列出"
---

PowerShell中不能使用cmd风格的`dir /s /b`命令，会报错`PositionalParameterNotFound`。正确命令为`Get-ChildItem -Path "路径" -Recurse -Name`（来源：Bash）
