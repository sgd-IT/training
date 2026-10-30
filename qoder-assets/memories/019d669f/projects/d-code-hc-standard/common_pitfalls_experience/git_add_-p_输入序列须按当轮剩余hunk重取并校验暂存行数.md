---
title: "git add -p 输入序列须按当轮剩余hunk重取并校验暂存行数"
usage_scenario:
    - "同一文件多逻辑点拆成多个提交"
    - "add -p 后提交为空或内容异常"
    - "统计本次会话提交数量"
keywords:
    - "git add -p"
    - "hunk选择序列"
    - "diff --cached校验"
    - "空提交"
    - "rev-list计数"
source: "auto"
---

Bug class: 用管道喂给 `git add -p` 的 y/n 选择序列与文件实际 hunk 数不匹配，导致目标改动被跳过、提交内容为空或错装。

Root cause: `git add -p` 按"当前工作区剩余 hunk 的顺序"逐个提问，而不是按事先记下的 diff 快照。两个已验证的失配来源：① 同一文件的前序 hunk 已被上一个提交取走，剩余 hunk 数量变少，沿用旧长度序列会使最后一个提问落到错误的 hunk（实测把唯一待提交的 WHERE 条件 hunk 用 'n' 跳过，commit 得到 "no changes added to commit"）；② PowerShell 数组经管道传入时元素个数与提问次数不严格一一对应，多余输入会被丢弃、不足时交互挂起。此外统计类命令同样有编码陷阱：PowerShell 管道里 `git log --oneline | Measure-Object -Line` 因编码/分页只统计到部分行（实测把 34 个提交报成 14）。

Fix pattern: 每次 add -p 之前先用 `git diff -- <file> | Select-String "^@@"` 重新列出当前 hunk 头与顺序，按新序号设计 y/n 序列；紧跟 `git diff --cached --stat` 校验暂存的增删行数是否等于所选 hunk 之和，再执行 commit。一旦发现某文件只剩单个待提交 hunk，直接 `git add <file>` 整体暂存即可，不必再走 add -p。计数一律用 `git rev-list --count <range>`。

Reusable lesson: 不要凭上一轮看到的 diff 快照推导 add -p 输入序列，因为前序提交会改变剩余 hunk 编号而使序列整体错位并把改动漏提交或错装进别的提交；必须每轮重取 hunk 清单并用暂存统计做闭环校验。适用于同文件多逻辑点需按最小粒度拆提交的场景（Mapper.xml、Vue 页面、含多常量的配置类）；不适用于整文件属于同一逻辑点的改动（直接 git add 文件即可）。
