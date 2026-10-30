---
title: "frontend-visual-craft 视觉工艺 skill 建设与融合判定"
usage_scenario:
    - "再次迭代视觉工艺类 skill 的规则与配方"
    - "判断某能力应新建还是并入现有 skill"
    - "把外部方法论沉淀为可复用 skill 时"
keywords:
    - "frontend-visual-craft"
    - "skill融合判定"
    - "内置skill不可编辑"
    - "视觉工艺清单"
    - "暗色字重补偿"
source: "auto"
---

## Task description
- Core requirement: 把一段视频里讲的 5 条前端视觉工艺（微明度色阶分层、暗色字重补偿、流式打字晕染、数字等宽与排版保护、动效分性格）浓缩成可复用的 skill，并判断能否与现有 skills 融合。
- Task background: 工作区 d:\code\remote-monitor；已有用户级 frontend-interaction-design（139 行 SKILL.md + recipes/checklist/platform-conventions），内置 frontend-design 负责视觉风格方向但归属 IDE 打包资源。

## Execution process
1. 融合对象排查：列目录确认 ~/.qoder/skills/ 无 frontend-design；逐级搜索 AppData Roaming/Local、Program Files、D:\Tool\Qoder IDE\resources\app\resources、~/.qoder/shared_client/bin/<版本>，均无独立目录，判定其打包进 CLI bundle，改动会被版本升级覆盖 → 排除"并入 frontend-design"。
2. 读 frontend-interaction-design/SKILL.md 与 recipes.md：其 description 明确"不管配色、字体与视觉风格"，recipes 为纯交互实现无重叠 → 排除"并入该 skill"。
3. AskUserQuestion 一次问清三项：落地方式（新建独立 skill）、存放层级（用户级）、是否写 uni-app 小程序端降级方案（要，独立章节）。
4. 新建 C:\Users\Administrator\.qoder\skills\frontend-visual-craft\SKILL.md（133 行）：五条规则各带"为什么/落地/翻车点"，含适用范围判断、提示词模板、审查模式模板、七条硬红线。
5. 新建同目录 recipes.md（316 行）：可复制代码片段 + 端侧适配表 + 参数速查表（每个数值标注取值理由）。
6. 更新 frontend-interaction-design/SKILL.md：description 补上 frontend-visual-craft，开头加三方分工说明，形成 design 定方向 / visual-craft 管精度 / interaction 管摆放的互指闭环。
7. 对视频内容做三处纠偏后写入 skill：暗色 wght -40 仅对可变字体生效且小字会糊（改为 ≥20px 降一档）；text-wrap: pretty 对中文无效（改用 balance + 容器宽度）；逐字动画补齐整段重渲染、总时长超 3s、中英混排断词三个坑的解法（--i 取模、chunk ≤24、Intl.Segmenter）。

## Related files
- /C:/Users/Administrator/.qoder/skills/frontend-visual-craft/SKILL.md
- /C:/Users/Administrator/.qoder/skills/frontend-visual-craft/recipes.md
- /C:/Users/Administrator/.qoder/skills/frontend-interaction-design/SKILL.md

## Notes
- PowerShell 非交互式 shell 下 Get-Content 统计中文文件行数严重失真（133 行报 78、316 行报 83），Get-ChildItem 的 ** 递归通配直接失败；文件行数与内容判断必须用 Read/Grep。
- 判断"能否融合某个 skill"的决定性依据是它落在哪一层（用户级/项目级/内置/插件缓存），不是内容对口程度；内置 skill 内容最对口也改不了。
- 新 skill 需下次会话才被扫描进可用列表，本轮无法自测召回。

## Task overview
已完成：frontend-visual-craft 用户级 skill 建成（SKILL.md 133 行 + recipes.md 316 行，均低于 500 行上限），并在 frontend-interaction-design 中建立分工互指；融合结论为不并入任何现有 skill 而是新建承接。视频原始说法中的三处已在 skill 内修正而非照抄。实际效果待后续会话用真实页面跑审查模式验证。

## Notes
- 【待验证项已有结论（2026-10-26 取证）】上述"实际效果待后续会话用真实页面跑审查模式验证"已可判定为：至今未用真实页面跑过。本轮扫描 C:\Users\Administrator\.qoder\projects\**\transcript\*.jsonl（163 个文件、61.3MB）全量检索，frontend-visual-craft 命中数为 0，而同期 hand-typing-training 命中 129 次/6 会话，说明零命中并非记录缺失所致。后续如需启用该 skill，应主动在真实页面上跑一轮审查模式，不能默认它已在生效。
