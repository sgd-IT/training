---
title: "index.lock残留致git写操作静默失效与暂存假空读数"
usage_scenario:
    - "大批量git add后暂存计数为零"
    - "git restore或checkout无效果需查锁"
    - "判断git失败是真报错还是输出被吞"
keywords:
    - "index.lock僵尸锁"
    - "git暂存静默失效"
    - "ls-files --cached判据"
    - "Measure-Object假空"
    - "git进程命令行归属"
    - "dubious ownership"
source: "auto"
---

Bug class: 一次大批量 `git add`（数百文件）被中断后残留 `.git/index.lock`，导致其后所有 git 写操作（add / restore / checkout）**静默失效**——命令返回"成功"但暂存计数仍为 0、工作区修改依旧显示 M；叠加 PowerShell 管道统计的假空读数，容易被误判成"目录本身被忽略所以 add 无效"而走向错误结论。

Root cause: 两层机制。① git 每次要改索引都要独占 `.git/index.lock`，锁存在时新命令直接放弃写入（真实报错走 stderr，易被 `2>$null`、管道或大量 LF/CRLF warning 淹没），前一次未跑完的 add 崩溃/超时即留下僵尸锁。② 本环境 `git diff --cached --name-only | Measure-Object -Line` 会返回 0（编码/管道吞输出），不能证明暂存为空；同一条命令里 `git add` 与紧随其后的统计之间也没有完成性保证。另外沙箱终端换用户上下文后 git 还会报 dubious ownership 而使 checkout 整体失败，令分支状态与预期不符。

Fix pattern: 判定暂存是否生效一律用 `git ls-files --cached <dir>/`（必要时配合 `git ls-tree -r <branch> --name-only` 取权威清单再逐个 Test-Path 比对缺失数），不要用管道接 Measure-Object。发现写操作无效果时按序诊断：`Test-Path .git/index.lock` → `Get-CimInstance Win32_Process -Filter "Name='git.exe'"` 看命令行，确认存活进程只是 IDE 的只读命令（rev-parse / log / reflog，不持写锁）后才删除锁文件并重跑 add；若确有活跃写进程则等待而非删锁。checkout 报 pathspec 不存在时先 `git branch -a` 核对实际分支名（可能被用户在 IDE 改名），再用 `git log <branch> --oneline` 比对顶部提交确认归属。dubious ownership 用逐条命令注入 GIT_CONFIG_COUNT / GIT_CONFIG_KEY_0=safe.directory / GIT_CONFIG_VALUE_0 绕过，不改全局配置。

Reusable lesson: 不要凭"管道统计为 0"就断言 git add 未生效或被 .gitignore 拦截，因为该读数在本环境不可信、真因往往是 index.lock 残留，结果是错怪忽略规则并去改动 .gitignore 而问题依旧；必须以 `git ls-files --cached` 为准并在删锁前先确认无活跃写进程。适用于 Windows + 非交互 shell（Node fallback / 沙箱）下的大批量暂存与分支切换操作；不适用于交互式终端中单文件的常规 add（无锁残留风险，直接执行即可）。
