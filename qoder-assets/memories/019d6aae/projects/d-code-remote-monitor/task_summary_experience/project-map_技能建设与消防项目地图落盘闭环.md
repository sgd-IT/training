---
title: "project-map 技能建设与消防项目地图落盘闭环"
usage_scenario:
    - "再次维护或扩展 project-map 技能时"
    - "在新项目复用建图四步流程前查证既有约定"
    - "回顾本项目地图产物的位置与形成依据"
keywords:
    - "project-map技能建设"
    - "项目地图落盘"
    - "AGENTS.md指针缺口"
    - "docs目录未跟踪文件"
    - "增量盘点"
source: "auto"
---

## Task description
- Core requirement: 把"给 AI 建项目地图"的方法论固化成可跨项目复用的用户级 skill，并用它在本工作区完成一次真实建图与落盘。
- Task background: 用户自述缺乏项目全局视野；前序会话已手工梳理过三仓结构并核实技术栈。讨论确定核心公式"地图 = 最小约束集 + 入口索引 + 决策日志"，只持久化约束/决策/契约/索引四类不可推导信息，可推导信息（调用图、目录树）不落盘。

## Execution process
1. 需求澄清：AskUserQuestion 确认安装位置选个人级 C:\Users\Administrator\.qoder\skills\project-map\（不建项目级副本）、覆盖范围选"完整闭环+更新模式"。
2. 惯例对齐：读取同级 work-log 的 SKILL.md，沿用【项目名·skill名】输出标注与 git 只读边界。
3. 用户追加硬约束"然后是不提交git的"，固化为三层规则：skill 本体不在任何 git 仓库内；运行期 git 全程只读（log/diff/status/show，禁 add/commit/push/stash/reset/checkout）；产出文件留在未提交状态由用户决定提交。
4. 写入 SKILL.md(107行)/reference.md/examples.md(48行) 三件套；Node fallback 下 `(Get-Content x).Count` 报 43/18/7 与字节数矛盾，判定为编码干扰，改用 Read 工具复核为权威结论。
5. 首跑建图模式：复用本会话已核实事实做增量盘点，补齐 server.port=48080（dev/prod 同端口）、module-eiot 六子模块 Java 文件数（biz 569 / iot-components 229 / core 123 / temporal 121 / api 55 / task 9）及包根差异（core 用 com.enjoyiot.eiot.common.*，非 module.eiot.*）。
6. 输出 6 段对话版地图 + 6 项落盘清单；用户裁决只做两项，其余四项（AGENTS.md×3、superpowers 索引页、追溯性 ADR×3、Mermaid 时序图与漂移修复）明确跳过。
7. 落盘 anwei-fire-remote-monitor/docs/项目地图.md（81行：仓库边界、系统全景、三端结构、文档索引）与 docs/速查索引.md（51行：业务域×端表15行、改哪里表8行、已知坑7条），两文互链并带"最后核实"标注与更新触发器小节。
8. 验证：Test-Path 抽查 9 个跨仓引用路径全部存在；grep 确认无凭据抄录；git -C anwei-fire-remote-monitor status --short -- docs 确认新文件为未跟踪。

## Related files
- /C:/Users/Administrator/.qoder/skills/project-map/SKILL.md
- /C:/Users/Administrator/.qoder/skills/project-map/reference.md
- /C:/Users/Administrator/.qoder/skills/project-map/examples.md
- /anwei-fire-remote-monitor/docs/项目地图.md
- /anwei-fire-remote-monitor/docs/速查索引.md

## Notes
- Select-String 结果用 `$_.Path.Split('\')[2]` 取模块名会拿到工作区目录名而非模块名，且匹配行数异常，应改为直接读取各 pom.xml 或列包结构。
- Get-ChildItem -Directory -Recurse -Name 对个别子模块返回空，易误判为空壳；可靠做法是分别统计 *.java 文件数再下结论。
- docs/ 下第三个未跟踪文件 视频拉流对接复用指南.md 是历史遗留，非本次产物，说明该仓 docs 目录整体处于未入库状态。
- 遗留缺口：无 AGENTS.md 指针时 AI 不会自动读取这两份地图文件，需用户另行发话补指针。

## Task overview
已完成：project-map 用户级技能建成（三件套齐备、Git 边界三条硬规则固化）；消防项目首次建图跑通全流程并按用户选择落盘两份文档，路径复核/密钥扫描/git 边界三项验证通过，全程未执行任何 git 写命令。待办：AGENTS.md 指针缺失导致地图无自动读取入口；其余四项落盘产物按用户指示未做。
