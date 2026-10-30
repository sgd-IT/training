---
title: "线性领先的分支同步用 ff-only 快进而非 cherry-pick"
usage_scenario:
    - "功能分支提交需同步到主干分支时"
    - "判断重复提交还是快进合并时"
    - "多分支仓库推送前核对分支关系时"
keywords:
    - "fast-forward 快进"
    - "cherry-pick 取舍"
    - "线性分支同步"
    - "git merge ff-only"
    - "分支差异侦查"
source: "auto"
---

结论：当一个功能分支相对主干「完全线性领先」（`git log main..feature --oneline` 有提交，而 `git log feature..main --oneline` 为空）时，若要把该分支上的提交同时体现在主干上，优先用 `git checkout main; git merge --ff-only feature` 快进，而不是 cherry-pick 或在主干重新提交一次。

理由/取舍：快进让两个分支指向同一个 commit，历史零重复、无双份哈希，后续互相合并也不会产生冗余冲突；`--ff-only` 自带安全阀——一旦两分支真正分叉就直接失败而不生成 merge commit，迫使回到人工判断。

被否决的替代方案：cherry-pick 同样内容到主干。它本身合理（适用于已分叉、或只想要部分提交的场景），但在纯线性场景下会制造两个内容相同、哈希不同的提交，日后互相合并易混乱。

适用条件：必须先侦查确认主干独有提交为 0，并确认工作区脏改动不阻塞 checkout/merge；操作前用 `git branch -vv` 看清各分支与其远程跟踪关系，末了切回用户原检出分支。失效条件：主干已前进出现分叉，或团队要求保留双分支独立演进（此时改用 cherry-pick 或正式 merge commit）。
