---
title: "work-log skill 规则优化与面试素材文档落地"
usage_scenario:
    - "再次迭代 work-log skill 规则或数据文件格式时"
    - "为新项目初始化工作记录与面试素材体系时"
    - "排查 skill 规范与存量数据格式不一致问题时"
keywords:
    - "work-log skill优化"
    - "面试素材文档"
    - "标题层级规范"
    - "存量格式迁移"
    - "模式E检索"
    - "简历素材池"
source: "auto"
---

## Task description
- Core requirement: 优化用户级 work-log skill 本身，并新增一份面向面试简历的工作记录 md 文档，供后续简历技能经验补充取材
- Task background: skill 位于 C:\Users\Administrator\.qoder\skills\work-log\（SKILL.md/reference.md/examples.md），数据目录 d:\code\remote-monitor\work-log\；2026-10-10 定稿的新版式（记录 H2 + 字段 H3）与存量旧格式（### 日期 · 标题 + 【】平铺）并存，且 SKILL.md 的"不使用 H1"与数据文件用 ## 做文件标题自相矛盾

## Execution process
1. 全量盘点：读三个 skill 文件 + 7 个数据文件 + 探测用户级 skills 目录与工作区含 .git 的子目录，产出 15 项问题清单分 A（规则冲突）/B（功能缺口）/C（数据侧）三组
2. 两项取舍经用户拍板：层级方案选"文件标题 H1 / 记录 H2 / 字段 H3"（H1 只留给文件级标题）；存量旧格式记录一起迁移
3. SKILL.md 改造（219 行）：新增模式判定表（参数归不到 A–D 时先用 AskUserQuestion 一次问清，不猜）、模式 E 按关键词/标签/时间只读检索、写前搜"同日+同模块"合并防重复、今天/这周/最近N天时间口径定义、补 git status --short + git diff --stat 收录未提交改动、自动探测工作区 .git 子目录并排除 ponytail、数据目录改为优先当前工作区/work-log 再回退主目录、补 03 手册分区名与三行格式标准、02 专题触发条件（同主题累计≥3条）、04 每主题 3–5 条上限
4. 响应用户中途追加需求：新增数据文件 06-面试素材.md，每条工作压成 6 行可讲要点（场景与规模/我做了什么/难点与解决/结果/可讲点/技能），模式 A 只对"值得进简历"的工作同步写 06，日常小改动只记 01；模式 C 改为读 06 提炼 04，缺细节才回 01
5. examples.md 全部示例改写为新版式并新增面试素材示例；reference.md 追加第九节初始化骨架（含 06）
6. 部署：先在工作区 _temp/work-log-new/ 写好三份新版文件，node 脚本 _temp/work-log-deploy.cjs 备份原文件到 _temp/work-log-backup-20261019/ 后覆盖到用户级目录，Bash 以 required_permissions='all' 执行成功
7. 存量迁移：00/03/04/05 首行改 H1、03 分区升 H2、04 能力主题升 H2；两个项目文件的 3 条旧格式记录整体重写为 H2 标题 + > 项目 引用行 + H3 字段，内容一字未改
8. 验证：Grep ^# [^#] 确认 7 个文件首行均为 H1；Read 抽查用户级 SKILL.md/examples.md 内容与 06 条目正常

## Related files
- C:\Users\Administrator\.qoder\skills\work-log\SKILL.md
- C:\Users\Administrator\.qoder\skills\work-log\examples.md
- C:\Users\Administrator\.qoder\skills\work-log\reference.md
- d:\code\remote-monitor\work-log\06-面试素材.md
- d:\code\remote-monitor\work-log\01-项目\消防告警分级响应系统.md
- d:\code\remote-monitor\work-log\01-项目\烟花仓库安全监测系统.md
- d:\code\remote-monitor\work-log\00-履历总表.md
- d:\code\remote-monitor\work-log\03-踩坑手册.md
- d:\code\remote-monitor\work-log\04-简历素材池.md
- d:\code\remote-monitor\work-log\05-技能清单.md

## Notes
- 用户初始指令"优化skills意见"意图模糊，先 AskUserQuestion 澄清避免走偏——这正是本次给 skill 补"模式判定"节的现实依据
- PowerShell Get-Content 回显 UTF-8 中文呈 GBK 假乱码，一度疑似写入损坏，改用 Read 工具按 UTF-8 读取才确认文件正常；fs.readFileSync(...).length 是字符数不是字节数，不能据此判断内容缺失
- 原以为 SearchReplace 无法编辑工作区外文件（须 node 脚本兜底），实测本轮可直接编辑用户级 SKILL.md 成功，故部署采用"脚本覆盖 + 后续微调直接 SearchReplace"混合方式；_temp 副本因此与用户级文件不再逐字同步，以用户级为准
- 06-面试素材.md 已有两条真实样例（MQTT 双链路摸底、告警字段语义统一）；10-19 当天的 API 使用统计埋点与权限裁剪两项工作尚无素材细节，未硬编，待用户补充后落盘

## Task overview
已完成：work-log skill 三件套按 8 类问题全部修订并部署到用户级目录（原文有备份可回滚），7 个数据文件层级统一为文件 H1/记录 H2/字段 H3 且 3 条存量记录迁移到新格式，新增 06-面试素材.md 作为面试弹药库并接入模式 A/C 联动。skill 规则内部冲突已消除，检索、防重复、时间口径、未提交改动、路径容错等缺口已补齐。
