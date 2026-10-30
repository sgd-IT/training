---
title: "PowerShell照搬bash的&&分隔符与head管道导致整条命令失败"
usage_scenario:
    - "Windows终端连写cd与git命令报解析错误"
    - "截取git diff超长输出时管道分页失败"
    - "命令报错后判断是真无数据还是语法不支持"
keywords:
    - "&&分隔符解析错误"
    - "head管道不可用"
    - "Select-Object -First替代"
    - "分号连写命令"
    - "PowerShell与bash差异"
source: "auto"
---

Bug class: Windows PowerShell 环境下沿用 bash 风格命令导致整条命令失败

Root cause: 本机默认 shell 是 PowerShell（非 bash），存在两处语法不兼容：① 语句分隔符不是 &&，写成 `cd <dir> && git log ...` 会直接报解析错误而使整条命令不执行（正确写法是 `cd <dir>; git log ...`）；② 不存在 head / tail 等 Unix 分页工具，`git diff ... | head -500` 报 "head is not recognized"，管道左侧的命令实际未产出可用结果。

Fix pattern: 多命令连写一律改用分号分隔；需要截取长输出时不在命令行分页，改用 `Select-Object -First N` 替代 head，或将输出重定向落盘后再分段读取。

Reusable lesson: Don't 在 PowerShell 会话里照搬 bash 的 && 连写和 head 管道，因为整条命令会静默失败或被误读为"目标仓库没有提交/文件没有差异"；instead 用分号分隔命令、用 Select-Object 截取输出。适用于本机等所有以 PowerShell 为默认 shell 的终端调用；不适用于明确运行于 WSL / Git Bash / Linux 的会话。
