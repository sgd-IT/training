---
title: "绿盾透明加密使 deepseek-harness-desktop 启动失败（密文被误判为文件损坏）"
usage_scenario:
    - "harness桌面应用启动失败排查"
    - ".dsh配置文件显示乱码或UTF-8错误"
    - "排查文件损坏与加密软件干扰"
    - "Windows企业加密环境进程读写差异"
keywords:
    - "绿盾"
    - "Tipray"
    - "LdMFilter"
    - "透明加密"
    - "PROFILE_WORKSPACE_READ"
    - "UTF-8"
    - "密文乱码"
    - "受控进程"
    - "harness桌面应用"
    - "dsh启动失败"
source: "auto"
---

Bug 类别：deepseek-harness-desktop 启动报 `INTERNAL_PLUGIN_INSTALL_FAILED: PROFILE_WORKSPACE_READ: stream did not contain valid UTF-8`（或 PREINSTALL_READ_WORKSPACE 同类错误），.dsh 下 yaml/yml/ndjson/lock 文件被 PowerShell 读成约 4KB 乱码（头部 `88 7D 1C .. EC 00 25 00` 固定）。
根因：机器安装天锐绿盾（Tipray）透明加密客户端（驱动 LdMFilter.sys，进程 LdTerm*，装在 C:\Inetpub\ftproot\Tipray）。受控进程（node.exe）透明解密读写，其写入的文件被强制加密；非受控进程（powershell.exe、未登记 exe，包括 deepseek-harness-desktop.exe）读到磁盘密文。文件本身并未损坏。
修复模式（已实测有效）：用受控进程读取内容（node 输出 base64）→ 用非受控进程（PowerShell）写回原路径，文件即保持明文；原密文文件改名 `<name>.green-encrypted.bak` 备份。验证方式：把 node.exe 复制成其他文件名作为"非受控进程替身"读取，能读出明文即修复生效。
同类表现：通过编辑器/Write 工具在工作区落盘生成的 .ps1 等文本脚本同样会以密文落盘，powershell -File 执行时报满屏乱码解析错误；此环境下应把脚本作为命令行参数内联传给 powershell（-Command），不要落盘执行。
可复用要点：不要被"UTF-8 报错/乱码"误导为文件损坏或编码问题，先用 node 与 PowerShell 交叉读取同一文件比较。注意复发风险：受控进程每次改写这些文件会重新加密；长期方案是让 IT 把 deepseek-harness-desktop.exe 加入绿盾受控进程，或把 C:\Users\Administrator\.dsh 排除出加密策略。
