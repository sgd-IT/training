---
title: "PowerShell下git中文输出乱码需以cat-file原始hex字节判定存储层"
usage_scenario:
    - "查看或汇总含中文的git提交记录时"
    - "Windows终端git输出乱码需判存储还是显示"
    - "批量抓取多仓库提交后落盘汇总"
    - "重定向得到空文件疑似无提交时"
keywords:
    - "git log中文乱码"
    - "PowerShell输出编码"
    - "git cat-file原始字节"
    - "hex判定UTF8与GBK"
    - "Node buffer转utf8落盘"
    - "Console OutputEncoding"
source: "auto"
---

Bug class: Windows PowerShell 终端里执行 git log 查看含中文的提交信息/文件路径时输出乱码；批量抓取多仓库当日提交记录、按提交内容写工作总结时，只需可读文本而无需断言仓库字节真伪。

Root cause: 控制台输出编码与 git 输出的 UTF-8 字节不匹配（GBK 解释 UTF-8），属展示层问题；git 仓库内数据本身完好，既非命令失败也非提交信息损坏。

Fix pattern（按判定成本从轻到重）:
① 首选「原始字节 + hex」一步定论：node 用 execFileSync('git',['cat-file','commit',rev],{encoding:'buffer'}) 取 commit 对象，跳过首个空行后打印 message 的 hex。hex 纯 ASCII 不受代码页影响：出现 e5/e6/e9 三字节前缀即 UTF-8（如 e5 80 bc =「值」），d6/b5 类双字节即 GBK 存储。此步即可裁决"存储 vs 显示"，成本最低。
② 需要可读中文文本（汇总、写总结、逐条比对说明）时：不要用管道或 Out-File 落盘，改写 Node 脚本以 encoding 'buffer' 取 git stdout，toString('utf8') 后 fs.writeFileSync(path, text, 'utf8') 落 UTF-8 文件，再用 Read 工具分段读取。这是本环境唯一稳定产出可读中文的通道。
③ 转存文件不可靠：`... | Out-File -Encoding utf8 f.txt` 后 Read 仍乱码——Out-File 只决定写文件编码，不决定 PowerShell 如何解码 git stdout，必须先 `[Console]::OutputEncoding = [System.Text.Encoding]::UTF8` 再重定向才可能正确。`Out-File -Encoding ASCII` 更糟：中文字节被整体丢弃使文件变空，Read 读出零行却看不出原因，极易误判为"该仓库当天无提交"。
④ node execSync/spawnSync 显式传 encoding 'utf8' 也不可靠：走 shell 管道会被按系统代码页二次解码，中文照样乱码；需文本比对时用 encoding:'buffer' 取原始 Buffer，再与 Buffer.from(期望中文,'utf8') 做相等比较。
⑤ chcp 65001 前置救不了：在 node 脚本里把 stdout 先经 chcp 包装再以 buffer/hex 读，实测仍拿不到可读中文——代码页切换不影响已被 PowerShell 父进程解码过的流；不要在此路径继续消耗轮次，直接回到 ①/②。

Reusable lesson: Don't 依赖 stdout 或普通文本通道判读仓库字节真伪，也不要因三条通道全乱码就认定提交信息损坏去重写历史；must 用 git cat-file 取原始 Buffer 打 hex 按 UTF-8/GBK 字节特征定性，需要文本则一次性走 ② 的稳定通道，不在 Out-File 各编码、chcp、cmd 包装之间反复试错。适用于沙箱非交互式 PowerShell 下所有含中文的 git 只读查询与文本取证；不适用于纯 ASCII 输出场景，也不适用于需实时流式查看日志的场合。推送结果核验见「git push 结果须以 sha 区间行与 status -sb 计数判定而非报错文本」。

## Notes
- 【前置解码成对】Out-File -Encoding utf8 只决定写文件编码，不决定 PowerShell 如何解码 git stdout；管道前不先设 `[Console]::OutputEncoding = [System.Text.Encoding]::UTF8`，落盘内容本身就是错的（GBK 误解后再存 UTF-8，Read 出来照样乱码），"先设解码、再重定向"必须成对出现。
- 【cmd /c 包装不可用】本沙箱终端类型为 PowerShell，`cmd /c "git ... > file"` 会被直接拒绝（报 Terminal Shell Type is PowerShell），导致该步及依赖它的判定全部失效；改用 PowerShell 原生语法或 node 脚本。
- 【空文件 ≠ 无提交】PowerShell 重定向（裸 `>`、Out-File -Encoding ASCII）产出空文件时不要断定当天无提交——很可能是中文被编码通道吞掉的假阴性；若同一时间窗已有乱码回显证明存在提交，即可确认是通道问题，应改走 ①/② 复核。
- 【PowerShell -File 脚本同样不可靠】含中文的 .ps1 交给 powershell -ExecutionPolicy Bypass -File 执行时，脚本自身也会被按非 UTF-8 解码而报解析错误；承载逻辑优先用 node .cjs，脚本内容保持纯 ASCII。
- 【显示层 vs 存储层】直接把 git 中文输出经终端重定向落盘再查看，会把实际正确的内容显示成乱码，进而误判存储损坏而错误重写历史；必须先经 buffer 原始字节核验再决定是否修复。
