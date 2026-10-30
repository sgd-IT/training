---
title: "code-conciseness-check与ponytail按战术审计与战略YAGNI分层"
usage_scenario:
    - "评估两个skill功能重叠是否合并"
    - "选择代码简洁度审查用哪个skill"
    - "设计新增skill的职责边界"
keywords:
    - "skill职责边界"
    - "ponytail子技能"
    - "过度工程审查"
    - "代码冗余检查"
    - "skill合并决策"
source: "auto"
---

Conclusion: code-conciseness-check 与 ponytail 保持独立、不合并；两者按"代码能不能写得更干净"vs"这段代码/特性该不该存在"分层。

Rationale (trade-off): code-conciseness-check 是战术层清单式审计（重复代码块、过长方法、冗余条件表达式、可简化写法），产出带严重度分级的结构化报告，由用户显式要求触发；ponytail 是战略层 YAGNI 哲学（任何编码任务自动介入或 /ponytail 显式触发，输出对话式并直接动手改）。合并会让报告格式限制 ponytail 自由度、或让 ponytail 哲学冲淡清单精确度，且两者触发场景不同。

Rejected alternatives: ①并入 ponytail —— ponytail 已有 review/audit/debt/gain 子技能，职责已细拆，再塞入会模糊其边界；②弃用 code-conciseness-check 全交给 ponytail-review —— 被否是因为仍需要系统化、带严重度分级的审查报告这一形态；重叠部分确实存在（都找"不必要的复杂性"），但 ponytail-review 指向过度工程（重造标准库轮子、提前抽象、死灵活性），code-conciseness-check 指向模式冗余。

Applicable / expiry conditions: 当仍需一份正式的简洁度分级审查报告时成立；若此后实际只用 ponytail-review 且不再索取分级报告，应重新评估并考虑弃用 code-conciseness-check。适用于本地 skills 集合的职责划分与合并讨论；不适用于单个 skill 内部的实现细节。
