---
title: "前端交互设计skill（按钮布局与可达性）建设"
usage_scenario:
    - "再次迭代该交互设计skill的规则与清单"
    - "新建用户级skill时沿用目录与风格约定"
    - "评审页面按钮位置与触控可达性时"
keywords:
    - "frontend-interaction-design"
    - "按钮布局skill"
    - "拇指热区"
    - "Fitts定律"
    - "交互评审清单"
source: "auto"
---

## Task description
- Core requirement: 把用户提供的「前端交互/按钮布局」方法论（平台惯例对齐、拇指热区可达性、Fitts 定律、位置+样式双重编码、就近原则、高频翻车场景）沉淀为一个可复用的 skill。
- Task background: 工作区 d:\code\remote-monitor 为 uni-app 移动端 + Element Plus PC 后台 + Java 后端；用户已有 7 个用户级 skills（code-comment-style、work-log、project-map 等），需沿用其命名与写作风格。

## Execution process
1. 现状盘点：列出 C:\Users\Administrator\.qoder\skills\ 与项目 .qoder\skills\ 的目录，读取 code-comment-style、code-conciseness-check 两个 SKILL.md，确认风格为中文正文、kebab-case name、输出开头标注【项目名·skills名】。
2. AskUserQuestion 一次问清三个方向，用户选定：设计与评审双模式、全端覆盖（移动/PC Web/平板折叠）、存放个人目录。
3. 新建 C:\Users\Administrator\.qoder\skills\frontend-interaction-design\ 四件套：SKILL.md（双模式分流 + 七步摆位决策 + 硬红线 + 评审报告模板）、platform-conventions.md（iOS/Android/Web/小程序惯例对照、RTL、桌面端视线流差异）、checklist.md（12 组场景检查项，每项带判定阈值与 P0/P1/P2 默认分级）、recipes.md（uni-app flex 底栏与安全区、键盘高度顶起、自定义 TabBar 安全区、Element Plus 弹窗 footer 顺序、表格 fixed="right" 操作列、ElMessageBox 危险确认）。
4. 自查修正两处：checklist 中「触控目标下限」与 SKILL.md 红线的分级不一致，改为 P1 并注明「与相邻可点物间距 <8dp 时升为 P0」；recipes 中 scroll-view 示例补充固定输入条的定位说明。
5. 边界划分：skill 只管操作摆放、主次、可点性与焦点顺序，配色字体留给内置 frontend-design，避免触发竞争。

## Related files
- /C:/Users/Administrator/.qoder/skills/frontend-interaction-design/SKILL.md
- /C:/Users/Administrator/.qoder/skills/frontend-interaction-design/platform-conventions.md
- /C:/Users/Administrator/.qoder/skills/frontend-interaction-design/checklist.md
- /C:/Users/Administrator/.qoder/skills/frontend-interaction-design/recipes.md

## Notes
- PowerShell 的 Get-ChildItem/Get-Content 在本轮非交互式 shell 下 ExitCode 1 且行数统计明显失真（实际 128 行显示 38 行），文件内容判断应改用 Read/Grep，不要据此断言写入失败。
- 把定性原则落成「带阈值的可判定检查项」是这个 skill 的核心增益，否则评审输出会退化为「感觉不太对」。
- 明确允许「无问题就直说符合规范」，防止评审时为凑数把 P2 拔高成 P0。

## Task overview
已完成：frontend-interaction-design 用户级 skill 建成（SKILL.md 138 行，远低于 500 行上限；三个 reference 均为 SKILL.md 一层直引；name 小写连字符；description 含中英触发词便于召回），未建项目级副本。用户尚未用真实页面试跑评审，清单判定口径的实际可用性待验证。

## Notes
- 【试跑状态更新（2026-10-26 取证）】"用户尚未用真实页面试跑评审"这一状态至 2026-10-26 仍未改变：本轮全量扫描 C:\Users\Administrator\.qoder\projects\**\transcript\*.jsonl（163 个文件）对该 skill 名零命中，同期有真实调用的 skill 均有数十次命中。清单阈值口径的实际可用性至今未经一轮真实页面评审验证，后续遇到 UI 摆放类需求时应优先用它跑一次评审以闭环验证。
