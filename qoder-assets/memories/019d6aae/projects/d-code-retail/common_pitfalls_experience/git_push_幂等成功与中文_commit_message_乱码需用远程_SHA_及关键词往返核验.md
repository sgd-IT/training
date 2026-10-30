---
title: "git push 幂等成功与中文 commit message 乱码需用远程 SHA 及关键词往返核验"
usage_scenario:
    - "验证 git push 是否真的成功时"
    - "查看git log确认中文提交内容时"
    - "PowerShell 下中文提交信息疑似乱码需核实存储时"
keywords:
    - "git push Everything up-to-date"
    - "git ls-remote SHA 比对"
    - "git log 中文乱码核验"
    - "git log --grep 往返验证"
    - "Console OutputEncoding UTF8"
source: "auto"
---

Bug class: git 命令输出在 PowerShell 下的误读（两类同源问题）

1) push 结果误读：'Everything up-to-date' 表示远程 ref 已与本地指向同一提交，是幂等成功而非失败；需用 `git ls-remote origin <branch>` 比对返回 SHA 与 `git rev-parse <branch>` 确认。不适用于本地落后远程的场景（那时出现 'Updates were rejected'）。

2) 中文提交信息"看似乱码"：PowerShell 输出管道按 GBK(cp936) 解码 git 写出的 UTF-8 字节，导致终端/工具回显为乱码，但 git 内部以 UTF-8 存储且正确。核验方法：设置 `[Console]::OutputEncoding=[Text.Encoding]::UTF8` 后重看，或更可靠地用 `git log --grep="<中文关键词>" --oneline` 做往返匹配——搜索词与提交词走同一编码链路，能精确命中即证明存储无误。禁止仅凭回显乱码就 amend 或重写历史。

注意：不要用 cmd 重定向绕过（终端为 PowerShell 时工具会拒绝 CMD 语法而报错）；也不要把 git 输出经 Out-File -Encoding utf8 再读回当作字节级验证——那只是二次转码，不构成证据。
