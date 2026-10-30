---
title: "Git blob 混合行尾使 autocrlf 归一化失效导致编辑后全文件假 diff"
usage_scenario:
    - "git diff 突然显示整个文件被改动时"
    - "提交前排查行尾符噪声与假差异"
    - "编辑后出现大量非预期增删行时"
keywords:
    - "混合行尾"
    - "core.autocrlf"
    - "ls-files --eol"
    - "ignore-cr-at-eol"
    - "全文件假diff"
source: "auto"
---

Bug class: git diff / git add 输出异常膨胀为"整个文件被重写"（数千行增删），掩盖真实改动

Root cause: 仓库中该文件的 blob 行尾是混合状态（例：duty-screen/index.vue 的 HEAD blob 实测 2528 个 LF + 9 个 CRLF）。git 只在 blob 行尾一致时才做 CRLF↔LF 自动归一化；一旦为 mixed，归一化被放弃并按字节比较。而 core.autocrlf=true 会把工作区 checkout 成纯 CRLF，于是工作区与 blob 逐行不同。此前 git status 显示"干净"仅靠 stat/mtime 缓存维持，文件一旦被编辑，缓存失效即暴露全文件假 diff。`.gitattributes` 若只对构建产物设 `-text`（如 public/park/v007/**），对 src 下的源文件不生效，无法保护。

Fix pattern: ① 用 `git ls-files --eol <path>` 看 i/ 列（i/mixed 即命中）；② 用 `git diff --ignore-cr-at-eol --stat` 得到忽略行尾后的真实内容差异作为基准；③ 用 node 脚本把"目标内容 + 纯 LF 行尾"整体写回文件（内容取工作区最新版，行尾统一 LF），使工作区字节与 blob 归一化结果一致；④ 再做一次 `git add` → `git diff --cached --stat` 实验（随后 `git reset -q -- <path>` 撤销暂存）确认暂存 diff 同样干净，然后才提交。提交后 blob 行尾归一，地雷永久拆除。

Reusable lesson: Don't treat a huge git diff as real content change or try to hand-craft the commit, because a mixed-EOL blob plus autocrlf makes every edit look like a full-file rewrite; instead verify with `git ls-files --eol` + `git diff --ignore-cr-at-eol --stat`, then normalize the file to pure LF before committing. Applies when editing files in a repo with core.autocrlf=true whose .gitattributes lacks rules for source files; does not apply when the blob is already uniform LF and .gitattributes pins text handling explicitly.
