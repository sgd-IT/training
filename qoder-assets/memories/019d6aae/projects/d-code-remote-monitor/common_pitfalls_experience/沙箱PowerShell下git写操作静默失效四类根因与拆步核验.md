---
title: "沙箱PowerShell下git写操作静默失效四类根因与拆步核验"
usage_scenario:
    - "PowerShell执行git上游区间查询或提交推送"
    - "长命令串联后确认git操作是否真落库"
    - "git diff输出异常巨大无法读取"
keywords:
    - "PowerShell @{u}哈希解析"
    - "PSReadLine崩溃静默丢失"
    - "git diff行尾符膨胀"
    - "2>/dev/null重定向失败"
    - "拆步git log核验"
source: "auto"
---

Bug class: 沙箱 PowerShell 下 git 命令执行失败或"看似执行实则未生效"

Root cause: 四条独立机制——① `@{u}..HEAD` 中的 `@{...}` 被 PowerShell 当作哈希字面量解析，报语法错误使上游区间查询失效；② 单条命令串接过多 `>> file 2>&1` 重定向且回显内容超长时，会触发 PSReadLine 崩溃，整批 git add/commit 实际未落库（事后 log 里根本查不到目标提交）；③ 工作区文件行尾符(CRLF/LF)与 git blob 不一致时，`git diff`/`git show` 输出异常膨胀（本例达 156KB、约 5000 行），远超 Read 工具上限而无法直接查看；④ PowerShell 不支持 Unix 的 `2>/dev/null`，会把 `/dev/null` 当路径（报 out-file D:\dev\null 错），导致该步及其后依赖它的判定结果全部不可信。

Fix pattern: 含特殊字符的 git 参数一律加引号（如 `"@{u}..HEAD"`）；丢弃 stderr 用 `2>&1` 重定向到文件而非 `2>/dev/null`；把 add/commit/push 拆成多条短命令分步执行、每步后立即用 `git log --oneline` / `git status -s` 核验是否真落库，不信任单次长串联的"完成"表象；diff 过大时改用 node 脚本比对工作区与 `git show <rev>:<path>` 的行尾符计数来诊断，而非硬读膨胀文件。

Reusable lesson: Don't 依赖一条超长 PowerShell 串联命令一次性完成提交并假定它成功，也不要写 `2>/dev/null`，因为 PSReadLine 崩溃与非法重定向会让操作静默丢失而终端仍返回提示符；must 拆步执行、每步后用独立 git log/status 复核真实状态。适用于沙箱非交互式 PowerShell 下所有 git 写操作与含 `@{}` 等易被 shell 误解析的参数场景；不适用于交互式终端或已确认逐条回显成功的短命令。中文输出的编码乱码问题与 bash 风格语法不兼容问题分别由独立记忆覆盖，不在本条范围。

## Notes

- 【工作区"消失的改动"核验】提交后若发现某个此前标记为 M 的文件从 git status 中消失（本例为 ApiAccessLogDO.java 的 @KeySequence 误改），不要默认是本次提交误带所致——先用 commit 的 files changed 计数排除，再用 Grep 读取该文件当前内容确认实际值：实测两次均为用户/IDE 在会话间隙自行还原，属正常状态，结论应记录为"已还原、不再是待处理项"而非猜测。
