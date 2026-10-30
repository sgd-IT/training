---
title: "git pull无tracking信息时需显式指定远程分支"
usage_scenario:
    - "git pull失败提示no tracking information"
    - "当前分支未设置upstream但需拉取特定远程分支"
keywords:
    - "git pull"
    - "tracking branch"
    - "origin prod"
source: "auto"
---

执行 `git pull` 报错 'There is no tracking information for the current branch' 时，需显式指定远程分支，例如：`git pull origin prod`（来源：Bash）
