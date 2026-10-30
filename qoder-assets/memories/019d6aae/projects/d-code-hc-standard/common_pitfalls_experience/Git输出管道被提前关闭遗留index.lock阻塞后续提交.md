---
title: "Git输出管道被提前关闭遗留index.lock阻塞后续提交"
usage_scenario:
    - "git报index.lock File exists无法继续"
    - "对git输出做截断式管道过滤前"
    - "判定孤儿锁能否安全删除"
keywords:
    - "index.lock孤儿锁"
    - "管道提前关闭"
    - "git add中断"
    - "Get-Process持锁判断"
    - "ExitCode误判"
source: "auto"
---

Bug class: Git 索引锁 index.lock 孤儿文件阻塞后续所有 git 写操作

Root cause: 给 `git add`（含 `--dry-run`）套上会提前终止的上游消费者管道（如 `| Select-Object -First N`、`| Measure-Object`）时，PowerShell 会在读完 N 条后关闭管道，git 进程被中断在写 `.git/index.lock` 的中途，锁文件残留。此后任何 `git add` / `git commit` 都报 "Unable to create '.git/index.lock': File exists"，且该失败命令返回 ExitCode 1 容易被误判为仓库损坏。

Fix pattern: 先确认无进程持有再删锁 —— `Get-Process git` 无输出 + 核对 `.git/index.lock` 的 LastWriteTime 与刚才中断的时刻吻合，即可安全删除该 lock 文件；随后把验证命令改为"一次性捕获全部输出到变量/文件后再做过滤统计"（例如 node 里 `execFileSync('git', ['add','--dry-run', <path>])` 取全量 stdout 再自行 split 计数），避免让 git 的输出流被提前关闭。

Reusable lesson: Don't pipe git's own output into a consumer that stops reading early because it leaves an orphan index.lock that blocks every later git command; instead capture the full output first and filter afterwards, and when index.lock appears always verify no live git process before deleting it. Applies to any git command whose stdout is piped through head/First/Measure-style truncating consumers in PowerShell or Node fallback shells; does not apply when git finishes normally (no lock left) or when a real long-running git/IDE process still holds the lock — in that case wait instead of deleting.
