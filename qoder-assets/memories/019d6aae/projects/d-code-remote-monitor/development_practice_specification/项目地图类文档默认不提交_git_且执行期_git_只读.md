---
title: "项目地图类文档默认不提交 git 且执行期 git 只读"
usage_scenario:
    - "生成项目地图或索引类文档后决定是否提交"
    - "执行需读仓库但不应改仓库的梳理任务"
    - "为新项目安装可跨仓复用的技能"
keywords:
    - "地图文档不入git"
    - "未跟踪状态保留"
    - "git 只读边界"
    - "用户级技能目录"
    - "提交前确认清单"
source: "auto"
---

项目地图类产出（AGENTS.md、docs/INDEX.md、速查索引、ADR 等由 project-map 流程生成的文件）默认不纳入 git 提交：生成后一律留在工作区未跟踪/未提交状态，是否入库、何时入库由用户决定；技能运行期间 git 命令全程只读（log/diff/status/show），禁止 add/commit/push/stash/reset/checkout。技能本体同样安装在工作区之外的用户级目录，不在任何 git 仓库内，也不建项目级副本。需要提交时先列清单经用户确认，遵循 Conventional Commits 并排除 profiles.active、manifest appid、临时日志类文件。
