---
title: "Git 提交不包含 .idea 等 IDE 本地配置"
usage_scenario:
    - "准备 git 提交整理待提交文件时"
    - "工作区存在 .idea 等 IDE 配置修改时"
    - "推荐提交计划征求用户确认时"
keywords:
    - "idea exclusion"
    - "IDE config"
    - "commit scope"
    - "IDE本地配置"
source: "user"
---

Git 提交时不包含 .idea 等 IDE 本地配置文件——这类文件修改保留在工作区即可，不进入任何提交。项目 .gitignore 已声明忽略 .idea，仅因历史遗留才被 git 跟踪，用户明确表示这类边角提交没有必要。
