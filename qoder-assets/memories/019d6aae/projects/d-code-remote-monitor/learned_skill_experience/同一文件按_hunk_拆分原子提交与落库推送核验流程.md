---
title: "同一文件按 hunk 拆分原子提交与落库推送核验流程"
usage_scenario:
    - "单文件混装多需求改动需按主题拆提交"
    - "校验暂存内容是否精确无跨主题泄漏"
    - "终端乱码或吞输出环境下核验提交与推送真实成败"
keywords:
    - "hunk拆分提交"
    - "git apply --cached"
    - "跨文件依赖核验"
    - "commit -F消息文件"
    - "ls-remote字节核验"
source: "auto"
---

## Skill description
当一个物理文件里混装了多个需求/主题的改动，需要按需求拆成独立提交时，用「git diff 提取目标 hunk → Node 脚本重算 new-side 行号生成局部补丁 → git apply --cached 精确暂存 → 双向关键词校验 → commit」的可复现编排；拆分前还须证明该笔单独 checkout 能编译通过，提交后须以字节级硬证据确认入库与推送真实成功。

## Execution process
1. 拆分前置——跨文件依赖核验：`git grep -n <符号名> HEAD -- <扩展名>` 读提交基线的签名与调用点，再用不带 HEAD 的同命令读工作区，输出两者差异位置清单，据此把"调用方已改、被调方未改"的被依赖文件补进同一批次，避免拆出编译不过的孤立提交。
2. `git diff -- <file>` 导出该文件完整 diff，识别每个 hunk 属于哪个需求主题（可按 hunk 内新增行的关键词区分，如按钮移动含 handleNavCommand、项目名前置含 alarm-tenant）。
3. 用 Node 脚本从完整 diff 中挑出目标 hunk，重新计算 patch 头部的 new-side 起始行号（首个保留 hunk 的 new_start = old_start；后续 hunk 的 new_start = 前一保留 hunk 的 new_end + (当前 old_start - 前一保留 hunk 的 old_end)），生成只含目标 hunk 的局部补丁文件。
4. `git apply --cached --check <patch>` 先干跑校验补丁可干净套用到索引；通过后 `git apply --cached <patch>` 只改索引不动工作区。
5. 双向校验暂存内容：`git diff --cached | Select-String` 对「应含关键词」计数须 >0、「不应含关键词」计数须 =0，确认无跨主题泄漏。同时核对 `diff --cached --stat` 的行数量级与预期 hunk 数一致。
6. 模拟校验：add → 查看暂存差异统计 → reset 撤销，在不产生提交的前提下暴露该笔会带出什么；每笔前核对暂存清单与预期完全一致。
7. 提交第一个主题后，同文件剩余 hunk 用 `git add <file>` 整体加入即为第二个主题；如需再拆则重复步骤 3-5。最后一次拆分完成后剩余 diff 恰好等于目标主题时，可直接整文件 `git add`，无需再生成补丁。
8. 提交信息落盘：写入工作区内 UTF-8 无 BOM 临时文件，用 `git commit -F <文件>` 读取；命令行内联传中文会被 shell 代码页破坏。覆盖式写多个消息文件须先分配互不冲突的文件名避免读写竞态。
9. 消息字节核验：PowerShell 终端直接打印 git log 常因 GBK 解码显示乱码误导判断，改用 Node spawnSync（不经 shell 管道）以 buffer 捕获子进程原始输出，比对 `log --format=%B` 原始 Buffer 与消息文件 trim 后逐字节；不一致才处理编码，乱码仅为显示层现象不作失败依据。
10. 推送判定：不依赖报错文本，以三重硬证据判定 —— ① 「旧sha..新sha 分支 -> 分支」区间行；② `status -sb` 无 ahead/behind；③ `git ls-remote` 远端 sha 对比本地 `rev-parse HEAD`。确认为沙箱 EPERM（exit=null、error 含 EPERM、stdout/stderr 皆空）后才带全权限重试同一命令；出现"无跟踪信息"补 `branch --set-upstream-to` 而非重推；rebase 过历史须声明旧 sha 已失效以远端为准。

## Notes
- 关键坑：局部补丁若不改 old-side 而只删中间 hunk，new-side 行号必须重算，否则 git apply 会偏移失败；--check 干跑是必做前置。
- 与「零上下文补丁改配置行」(git apply --cached --unidiff-zero) 是不同用法：本技能针对正常多行 hunk 的主题拆分，不需 --unidiff-zero。
- 全程不触碰工作区文件，撤销只需 git restore --staged <file>。
- 脚本内的 repo 根路径要作为参数传入而非写死：同一文件可能拆给多次提交（本项目 AlertRecordMapper 依次用 _mapper_item1/_item5/_item8.patch 分三次入账），且 vue 仓与后端仓是两个不同目录，写死会导致在另一仓跑出 0 hunk 的空补丁而不报错。
- 补丁行尾必须是 LF 且逐字来自 git diff 导出的原始字节：core.autocrlf=true 仓库中 blob 侧为 LF，手敲/编辑器写成 CRLF 的补丁经 apply --cached 会把整个暂存 blob 转成 CRLF，commit 表现为全文件几百上千行假差异；--ignore-whitespace 虽能让 --check 通过但掩盖不了该污染，干跑通过≠安全，异常时用 git ls-files --eol 诊断并以 git reset 回退重造补丁。
- PowerShell 管道（Out-String / Out-File / 2>&1 重定向）会破坏 diff 中文与空白，导出补丁和核对暂存内容都应走 Node 读原始字节写 UTF-8 无 BOM 文件的方式。
- 只对比工作区两个文件的当前内容而不取 HEAD 版本，会拆出编译不过的批次或造成错误合并，必须先拿到 HEAD 侧证据再定批次划分。
- 依赖 shell 管道回显判断 push 成败 causes 静默失败被误判为成功，必须以 sha 区间行 + status -sb 计数 + ls-remote 对照三重硬证据判定。
- 用 Get-Content 读 UTF-8 日志 causes 中文按 GBK 解码乱码进而误判内容损坏，must 改用按 UTF-8 解码方式读取。

## Skill overview
成功将 duty-screen（需求6+7混装）与 monitoring-center（需求2+6混装）拆为两个独立原子提交并推送 origin master；后续又将单文件混装三需求的 AlertRecordMapper 按 hunk 分三次入账（租户过滤／摄像头去重／月报统计），每步均做含/不含双向关键词校验，零误合。另一次 duty-screen 拆分因手写 CRLF 补丁污染暂存 blob 导致首笔提交被 reset 回退，改按 LF 原始字节补丁重做后入账成功。
