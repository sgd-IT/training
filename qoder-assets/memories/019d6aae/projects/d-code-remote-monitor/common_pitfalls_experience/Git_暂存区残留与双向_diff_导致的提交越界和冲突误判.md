---
title: "Git 暂存区残留与双向 diff 导致的提交越界和冲突误判"
usage_scenario:
    - "在混杂暂存状态的仓库提交单个文件"
    - "推送前评估本地与远端改动是否重叠"
    - "误提交多文件后安全回退重做"
keywords:
    - "pathspec提交"
    - "暂存区残留"
    - "reset soft回退"
    - "merge-base单向diff"
    - "rebase冲突预判"
source: "auto"
---

Bug class: Git 提交范围失控与改动重叠误判（两类同源陷阱）

Root cause:
① commit 默认提交整个暂存区快照而非"刚 add 的文件"。仓库里可能长期残留其他任务/他人的已暂存文件（git status 索引侧显示 A 或 AM），此时 `git add <目标文件>; git commit` 会把这些无关文件一起写进提交，且提交成功后才发现文件数与预期不符。
② `git diff --name-only HEAD origin/master` 是**双向**差异，既包含"远端有本地没有"，也包含"本地自己的提交远端没有"，因此自己刚改的文件必然出现在列表里，据此判断 rebase 冲突风险会得出"两边都改了这个文件"的错误结论。

Fix pattern:
① 单文件粒度提交一律用 pathspec 形式 `git commit -F <消息文件> -- <单个路径>`：它只按指定路径的工作区内容建提交，不触碰索引其余部分，可安全绕过残留的已暂存文件。误提交后用 `git reset --soft HEAD~1`（仅移动 HEAD，非破坏性）回退再按 pathspec 重做；不要用 --hard。
② 判断远端侧真正新增的改动，必须先取共同祖先再单向比对：`git diff --name-only $(git merge-base HEAD origin/master) origin/master`，然后与本地未提交文件清单求交集，交集为空即可直接 rebase。

Reusable lesson: Don't rely on `git add <file>` followed by a bare `git commit` to scope a commit because pre-staged files from other tasks get silently swept in; instead use the pathspec form of commit. And don't size up rebase conflict risk with a two-dot `git diff HEAD origin/master` file list because your own commits pollute it; instead diff from the merge base toward origin only. Applies to committing in shared worktrees that may hold unrelated staged changes, and to planning any pull/rebase/stash sequence; does not apply to repositories verified clean via `git status --porcelain` before adding, or to intentionally atomic multi-file commits where staging has been explicitly reviewed.
