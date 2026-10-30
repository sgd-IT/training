---
title: "PowerShell管道捕获git中文输出乱码的GBK口径修复"
usage_scenario:
    - "git log中文提交信息显示为乱码需还原"
    - "PowerShell管道捕获命令输出编码错乱"
    - "node -e内嵌引号在PowerShell中被剥离"
keywords:
    - "git乱码"
    - "UTF-8显式解码"
    - "ProcessStartInfo"
    - "PowerShell编码"
    - "mojibake"
    - "git log -S 溯源"
source: "auto"
---

## Bug 类别
Windows 中文环境下通过工具/PowerShell 管道捕获 git log、git diff 等命令的中文输出时出现 mojibake（如"鏈嶅姟""浣滀笟涓柇"类字样）。

## 根因
提交信息在仓库中是 UTF-8 字节，而捕获链路（PowerShell [Console]::OutputEncoding，中文 Windows 默认 cp936/GBK）按 GBK 解码 UTF-8 字节，产生乱码。**乱码出现在显示层，不代表 commit 内容本身损坏**——判断仓库内容是否真的坏了，必须换一条不经过控制台解码的读取路径复核。

## 修复模式（按可靠性排序）
1. **首选：绕开控制台，显式按 UTF-8 解码后落盘再读**。本工具沙箱的 Bash fallback 会吞掉 PowerShell 自身的编码设置，此法稳定：
   `$psi=New-Object Diagnostics.ProcessStartInfo("git","log -1 --format=%B"); $psi.RedirectStandardOutput=$true; $psi.StandardOutputEncoding=[Text.Encoding]::UTF8; $psi.UseShellExecute=$false; $pr=[Diagnostics.Process]::Start($psi); $o=$pr.StandardOutput.ReadToEnd(); $pr.WaitForExit(); $o | Out-File -Encoding UTF8 .tmp\msg-check.txt`，随后用 Read 工具查看该文件即得正常中文。
2. `chcp 65001` + `git --no-pager`：在真实交互终端可用，但在 Node fallback shell 下实测仍乱码，不可依赖。
3. `git -c i18n.logOutputEncoding=GBK log --pretty="%ad__%h__%s"`：原理是让 git 主动输出 GBK 字节匹配解码口径，同样受 fallback shell 影响，仅作备选。
4. 已捕获的 mojibake 字符串用 TextDecoder('gbk') 再解码会得到双重乱码，此路不通；`-c i18n.commitEncoding=GBK` 对本场景无效（仓库内本来就是 UTF-8）。
5. PowerShell 单引号包裹的 `node -e "..."` 内嵌双引号会被转义剥离导致脚本失败，稳定做法是写成 .js 文件执行。
6. 附带坑：`Get-Content $p -Encoding UTF8` 在某些被编辑过的 Java 文件上返回空行数组，改用 `[System.IO.File]::ReadAllText($p) -split "\r?\n"` 取行更可靠。

## 关联取证手法
判断某段可疑代码（如 `if (Boolean.TRUE)` 推送拦截块）是何时引入：`git log -S "<独有字符串>" -- <file>` 返回空 + `git diff` 中该行以 `+` 开头 ⇒ 该改动从未入库，是工作区手工残留；再配合 `(Get-Item $f).LastWriteTime` 定位添加时间。

## 适用边界
适用于 a) 中文 Windows + PowerShell/非交互 shell 捕获任意命令的 UTF-8 中文输出；b) 本工具沙箱的 Bash fallback（此时只能用第 1 种方法）。不适用于纯 UTF-8 终端（如 WSL/Git Bash 设了 UTF-8 locale），那里应保持默认。
