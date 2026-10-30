---
title: "PowerShell的-m中文参数使git提交信息存为GBK乱码"
usage_scenario:
    - "在PowerShell中含中文提交信息执行git commit"
    - "提交记录落库后显示乱码需判存储损坏"
    - "批量提交前选择中文message传入通道"
keywords:
    - "git commit -m中文"
    - "GBK编码错解"
    - "node封装git"
    - "提交信息存储损坏"
    - "UTF-8绕过编码层"
source: "auto"
---

Bug class: PowerShell 中文参数传入 git 命令导致提交信息存储为乱码
Root cause: PowerShell 将中文参数以系统代码页（GBK）编码传给 git.exe，而 git 默认按 UTF-8 解析和存储，导致字节序列错解。
Fix pattern: 避免在 PowerShell 中直接用 -m 参数传递中文；改用 node 脚本调用 git（如 `node -e "require('child_process').execSync('git commit -m ...')"`），利用 Node.js 的 UTF-8 字符串处理能力绕过 PowerShell 编码层。
Reusable lesson: Don't 用 PowerShell 的 `-m` 参数直接传中文提交信息，因为其 GBK 编码与 git 的 UTF-8 存储不兼容，会导致乱码；instead 用 node 脚本封装 git 命令。适用于所有需要在 Windows PowerShell 环境中执行含中文参数的 git 操作的场景；不适用于 WSL 或已配置好 UTF-8 的 PowerShell 7+ 环境。
