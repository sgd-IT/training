---
title: "work-log技能按职业画像改造并产出AI评测黄金集"
usage_scenario:
    - "再次调整work-log记录规则或门槛时"
    - "为AI项目建评测集与写工作记录时"
    - "修改work-log目录外文件前确认安全写法"
keywords:
    - "work-log改造"
    - "AI工程档门槛"
    - "黄金集模板"
    - "bad case编码"
    - "透明加密写坏"
source: "auto"
---

## Task description
- Core requirement: 根据用户职业画像（1 年经验、后端转全栈、目标跳槽、已有 Spring AI + RAG + 工具调用 + 工厂切模型）改造自定义 skill work-log，并把现有 Spring AI 项目落成正式记录与评测口径。
- Task background: 原 work-log skill 是通用全栈口径，技术沉淀偏好偏物联网/并发，AI 部分完全空白；数据目录为跨项目共享的 d:\code\remote-monitor\work-log\，该目录受企业透明加密保护。

## Execution process
1. SKILL.md 两轮改造：新增「个人定位」章节（后端深度 > 前端独立交付 > AI 工程化；项目上限 3 个后收窄为主推 2 个；时间投入优先级）；重写技术沉淀四条主线；加入 AI 工程档硬性门槛（评测/可靠性/成本至少占两项且有真实场景，否则只进 01 不进 06 和简历池）；模板新增 AI 应用类型与可选【评测与成本】块；标签体系新增「前端深水区」「AI 专项」。
2. reference.md 同步：技能框架插入 AI 工程分类；熟练度规则加"1 年经验不写精通、熟练最多 2 项"；量化指标补 AI 行；常见坑补"AI 玩具档描述""背八股""虚标段位"。
3. examples.md 新增示例 9：工单 AI 分派 Agent 完整记录 + 玩具档 vs 工程档简历句对比。
4. 新建 01-项目/Spring AI RAG 知识库助手.md（40 行）：按新模板写入，标注为"01 底稿（玩具档→工程档升级清单）"，未写入 06/04。
5. 新建 02-专题/AI项目评测与bad case口径.md（149 行）：黄金集 12 字段、50 条配比（事实问答 20/流程制度 15/工具调用 10/边界拒答 5）、9 项核心指标、15 类 bad case 编码（R1-R3/G1-G3/T1-T4/C1/B1-B2/P1/L1）、第八节出题与填表指引含 9 条自检清单。
6. 新建 02-专题/AI评测黄金集-50条模板.csv（52 行 = 表头 + RAG-001~050 + 空行），预填 5 条示例并预置分类与难度分布。
7. 更新 00-履历总表.md：追加第 18 行 AI 项目记录索引。
8. 全部文件写后用 Read 回读校验通过。

## Related files
- C:/Users/Administrator/.qoder/skills/work-log/SKILL.md
- C:/Users/Administrator/.qoder/skills/work-log/reference.md
- C:/Users/Administrator/.qoder/skills/work-log/examples.md
- D:/code/remote-monitor/work-log/01-项目/Spring AI RAG 知识库助手.md
- D:/code/remote-monitor/work-log/02-专题/AI项目评测与bad case口径.md
- D:/code/remote-monitor/work-log/02-专题/AI评测黄金集-50条模板.csv
- D:/code/remote-monitor/work-log/00-履历总表.md

## Notes
- 两次写坏事故同源：d:\code 下文件对 PowerShell 呈密文，AppendAllText 追加 UTF-8 导致 00 尾部乱码；后续用 ReadAllText/Replace/WriteAllText 做"读改写"又把 01/02 整份写坏。唯一可靠路径是 Write 生成工作区内完整明文临时文件 + Copy-Item 整文件覆盖 + Read 回读校验；此结论已写入 SKILL.md「落盘提示」。
- SearchReplace 对 .tmp 下的临时文件也可能因加密层返回内容不匹配，改用更短的唯一子串锚点可成功。
- 用户反馈 CSV 在编辑器里"看着散"，原因是空字段写成 ""，属预期形态；后续可能需求是 xlsx 版或代填 50 条。

## Task overview
已完成：work-log 三个文件按画像重排并落地 AI 工程档门槛；Spring AI 项目已落成 01 底稿 + 评测口径 + 50 条黄金集模板 + 索引行，全部回读校验通过；按门槛判定该项目暂不进 06 和简历池，待补齐评测与成本数据后再升级。
