---
title: "Git 分批提交工作流与 Conventional Commits 提交信息规范"
usage_scenario:
    - "一次会话累积多个功能改动需要提交时"
    - "用户要求分批提交或推送时"
    - "跨前后端双仓库同步提交时"
keywords:
    - "原子化分批提交"
    - "Conventional Commits"
    - "rebase推送流程"
    - "提交排除项"
    - "远端名约定"
source: "user"
---

提交粒度：按逻辑单元原子化拆分，一个提交只含一个主题；跨仓库改动（后端 anwei-fire-remote-monitor 与移动端 anwei-fire-remote-monitor-mobile）按依赖顺序各自成批提交。提交信息：Conventional Commits 格式「type(scope): 中文描述」，type 与 scope 用英文小写（feat/fix/refactor/chore/docs，如 feat(alert)、refactor(ai)），描述用中文；此前「纯中文大白话、无 type(scope) 英文前缀」的要求已废止。流程：git reset 清空暂存区后按顺序逐批 add+commit，每个提交的清单与信息先经用户确认；全部提交完停在本地，不主动 push；用户要求推送时先 fetch，再 pull --rebase --autostash，冲突解决并编译通过后 push。排除项：application.yaml 的 profiles.active 环境行、manifest.json 的 appid 调试值、_m*.txt 临时文件不进提交。后端仓库远端名为 origin，移动端仓库远端名为 anwei（非 origin）。
