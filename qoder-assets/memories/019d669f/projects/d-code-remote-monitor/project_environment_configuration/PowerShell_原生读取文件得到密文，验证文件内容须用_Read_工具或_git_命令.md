---
title: "PowerShell 原生读取文件得到密文，验证文件内容须用 Read 工具或 git 命令"
usage_scenario:
    - "验证工作区文件内容是否正确"
    - "PowerShell 读文件结果异常时排查"
    - "合并/拉取后核验文件完整性"
keywords:
    - "透明加密"
    - "密文读取"
    - "PowerShell文件API"
    - "88-7D-1C"
    - "文件验证方式"
source: "auto"
---

该开发机装有文件系统透明加密层：git.exe 与 IDE(Qoder) 进程读取文件时可透明解密，但 PowerShell 原生文件 API（Get-Content、[IO.File]::ReadAllBytes/ReadAllText、Select-String -Path）读到的是密文（文件头特征 88-7D-1C，内容乱码），其行数与内容均不可信（例如 2500+ 行的文件被读成 816 行乱码）。验证工作区文件内容的正确方式：使用 Read 工具（IDE 视角）或 git 命令（show/cat-file/diff/grep，git 进程可解密）；禁止用 PowerShell 直接读取文件内容做判断，PowerShell 仅用于运行 git、构建等命令。
