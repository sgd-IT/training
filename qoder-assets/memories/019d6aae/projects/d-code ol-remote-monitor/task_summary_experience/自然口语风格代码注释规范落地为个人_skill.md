---
title: "自然口语风格代码注释规范落地为个人 skill"
usage_scenario:
    - "再次创建或调整用户自定义 skill 时"
    - "编写或评审代码注释需要引用该规范"
    - "排查 skill 未自动触发的兜底方案"
keywords:
    - "code-comment-style"
    - "个人skills目录"
    - "SKILL.md约定"
    - "代码注释规范"
    - "create-skill流程"
source: "auto"
---

## Task description
- Core requirement：把用户提供的"代码注释规范（自然口语风格）"文本做成一个可复用的 skill。
- Task background：用户已有 5 个自定义 skill 存放在个人技能目录，统一使用 YAML frontmatter（name + description）、中文正文，并在开头带【项目名·skill名】输出标注行；本次需与既有约定对齐后新建第 6 个。

## Execution process
1. 盘点现有资产：读取 code-conciseness-check、decision-driven-coding、work-log 三个 SKILL.md 与 qoder-backup\清单与恢复说明.md，确认目录位置、frontmatter 写法、标注行约定，以及 decision-driven-coding 已占用 `[决策点]/[决策锚点]` 结构化注释格式。
2. 向用户确认三个设计点：技能名称、存放位置（个人 vs 项目）、触发时机。用户对名称项回复"这个是要每一次生成注释时候都要调用到的"，即强调必须每次生成注释都自动生效。
3. 创建 `C:\Users\Administrator\.qoder\skills\code-comment-style\SKILL.md`（124 行），description 写死"每次生成、补充、修改或评审代码注释时都必须应用本 skill"，并把"注释是废话/看不懂/太啰嗦"列为触发词。
4. 内容结构：保留用户六条做法原文与正反例（解释"为什么"、长逻辑提前剧透、坑位标记带情绪、复杂计算分步解说、TODO 具体可追溯、控制密度），另加三节——写注释前自问三句、检查/改写既有注释的工作流与输出格式、语言与格式约定。
5. 与 decision-driven-coding 划清边界：结构化决策注释走那边格式，本规范管其余日常注释。
6. 同步更新用户行为类记忆中"已写入标注行的 skill 清单"，追加 code-comment-style。

## Related files
- C:\Users\Administrator\.qoder\skills\code-comment-style\SKILL.md

## Notes
- 用户用自由文本回答选择题时，回答内容可能对应的是另一个维度（本次对"名称"的回复实际是关于触发时机的要求），需要识别真实意图并把要求落到正确字段（写进 description 强化触发），而不是机械按题目字面处理。
- 工作区根目录 d:\code ol\remote-monitor 本身不是 git 仓库，放在根目录或 ~\.qoder\ 下的产物不受版本控制；qoder-backup 快照记录的是"6 个用户级 skills"，新增后需手动同步才一致。
- 若后续发现该 skill 未被自动触发，可在 ~\.qoder\rules\ 补一条规则兜底（与 git-commit 规则同理）。

## Task overview
已完成：code-comment-style skill 落地在个人技能目录，所有项目通用，命名沿用推荐的 kebab-case 风格，触发范围覆盖生成/补充/修改/评审注释四种场景，内容与既有 skill 约定完全对齐。遗留可选项：是否把新 skill 同步进 qoder-backup 快照与 zip 包，等用户决定。
