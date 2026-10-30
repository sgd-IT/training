---
title: "沙箱终端下 git 提交的编码与字节级校验要点"
usage_scenario:
    - "沙箱终端执行git提交"
    - "中文提交信息写入与校验"
    - "终端脚本文件创建"
keywords:
    - "沙箱乱码"
    - "提交信息文件"
    - "git cat-file commit"
    - "hex字节核验"
    - "commit -F"
    - "显示层失真"
source: "auto"
---

## 使用场景
在本工作区通过沙箱终端执行 git 提交（含中文提交信息、新增文件）时。

## 使用方式
1. 提交信息优先由 IDE 写入工具直接创建为消息文件（本轮实测：Write 出的 _commit_*.txt 经 node 读原始 Buffer 验证就是 UTF-8，git commit -F 引用后 commit 对象内也存为 UTF-8），无需绕道终端层写文件；[IO.File]::WriteAllText(路径, 文本, [Text.UTF8Encoding]::new($false)) 只是备选写法。
2. 乱码只发生在「显示通道」：PowerShell 控制台、Out-File 重定向落盘、node execSync 以 encoding:'utf8' 读命令输出这三条通道都会把 UTF-8 字节按 GBK 误解而显乱码。git add/commit 本身不受影响，不要因终端回显乱码就误判提交内容损坏或去重写历史。
3. 断言提交信息真实入库编码的唯一可靠通道是原始字节：`node -e` 用 execFileSync('git',['cat-file','commit','HEAD'],{encoding:'buffer'}) 取 commit 对象，跳过首个空行后打印 message 的 hex——出现 e5/e6/e9 三字节前缀即为 UTF-8（例 e5 80 bc =「值」）。git show <rev>:<path> 检查关键 ASCII 标记（如 package、class、ALTER TABLE）确认 blob 正确。

## 注意事项
需要 PowerShell -File 执行的脚本仍建议由终端层创建或用 node .mjs 承载（IDE 写入工具创建的 .mjs 可被 node 正常读取执行）；但纯文本消息文件（供 git commit -F 消费）用 IDE 写入工具创建已被验证可行。git commit 的提交信息优先用 -F 文件方式而非 -m 内联。
