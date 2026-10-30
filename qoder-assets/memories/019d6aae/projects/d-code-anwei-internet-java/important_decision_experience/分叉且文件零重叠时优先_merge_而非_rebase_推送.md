---
title: "分叉且文件零重叠时优先 merge 而非 rebase 推送"
usage_scenario:
    - "git push 被拒提示 fetch first 需整合远端"
    - "本地领先多个已确认 sha 的提交需推送"
    - "工作区留有刻意不提交的文件时选择整合方式"
keywords:
    - "merge整合分叉"
    - "sha保持不变"
    - "stash往返风险"
    - "零文件重叠判定"
    - "push被拒处理"
source: "auto"
---

Conclusion: 本地与 origin/master 分叉、且远端独有改动与本地提交涉及文件零重叠时，选 git merge origin/master --no-edit 后再 push，而不是 rebase。

Rationale (trade-off): merge 保留本地已有提交的 sha（便于对照此前已向用户展示过的提交清单）、不需要 stash 暂存工作区里刻意不提交的 IDE 配置与草稿文件、多一个 merge commit 的代价可接受（该仓库历史中本就存在 merge commit）；rebase 虽历史线性，但会重写全部本地 sha 并引入 stash 往返风险。

Rejected alternatives: stash push -u + rebase + stash apply + push —— 本身也是合理做法（上一次同类分叉就是这么处理的），本次放弃是因为要保住已确认过的 3 个 sha、并避免动工作区那批未提交文件；直接 push 被远端拒绝，不是可选路径。

Applicable / expiry conditions: 仅在先用 diff 证明两侧改动文件零重叠（冲突概率极低）时成立；一旦远端改动与本地提交碰同一文件、或本地含表结构同步模板这类自动合并会静默产生重复内容的文件，应改用 rebase 或逐块人工比对；若团队后续要求线性历史，则该选择需重新评估。
