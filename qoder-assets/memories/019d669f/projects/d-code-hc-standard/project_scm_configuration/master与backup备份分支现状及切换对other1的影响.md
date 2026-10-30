---
title: "master与backup备份分支现状及切换对other1的影响"
usage_scenario:
    - "在master与备份分支间切换前评估文件风险"
    - "需要找回被切分支删除的other1源码"
    - "向origin推送master或备份分支时确认落点"
keywords:
    - "origin远程地址"
    - "backup备份分支"
    - "other1分支级跟踪"
    - "切分支删除工作区文件"
    - "dubious ownership"
    - "worktree隔离"
source: "auto"
---

hc-standard 仓库的分支与远程现状（2026-09-24 实测）：

- 远程 origin：http://192.168.250.252:10102/r/gongmao/hc-standard.git；master 已推送至 28cfa77（区间 037fb6f..28cfa77），本地不再领先。
- 存在专用备份分支 `backup`（最初创建名为 backup-20260924-with-app，后被用户在 IDE 中重命名为 backup），已作为新分支推送并设置 upstream origin/backup。该分支内容 = master 全部提交 + 「移除根 .gitignore 中 /other1/ 行」的 chore 提交 + other1（APP 端 Android 工程）503 个源码与 gradle 配置文件 + 从 master cherry-pick 过来的后续提交。
- 结构性约束：master 仍保留 /other1/ 忽略规则且不跟踪 other1，而 backup 跟踪它——因此在 master ↔ backup 之间切换分支时，git 会删除（切回 master）或重建（切到 backup）工作区的 other1 源文件，可能伴随 unable to unlink 警告导致部分文件残留缺失。恢复手法：在 master 上执行 `git checkout backup -- other1/` 再 `git restore --staged other1/`，可把文件取回磁盘而不重新入库；核对完整性用 `git ls-tree -r backup --name-only` 过滤 other1/ 后逐个 Test-Path 统计缺失数。如需长期并行使用两份代码，应改用独立 worktree 而非来回切分支。
- 沙箱终端下 git 可能报 "detected dubious ownership in repository"（仓库属主与进程用户不一致），按命令注入环境变量 GIT_CONFIG_COUNT=1 / GIT_CONFIG_KEY_0=safe.directory / GIT_CONFIG_VALUE_0=D:/code/hc-standard 即可绕过，无需修改全局或仓库 git 配置。
