---
title: "Qoder skills 本机三处存储位置与跨机迁移要点"
usage_scenario:
    - "换电脑或重装系统前备份 Qoder 环境"
    - "查找某个 skill 的实际存放目录"
    - "迁移后 skill 或插件失效排查"
keywords:
    - ".qoder/skills 目录"
    - ".agents/skills 目录"
    - "plugins cache 插件技能"
    - "skills 迁移备份"
    - "新建skill风格采样"
source: "auto"
---

Qoder 的 skills 在本机分散存放于三个位置（均为用户主目录下，非账号云端同步，换电脑需手动拷贝）：
1. `C:\Users\Administrator\.qoder\skills\` — 用户级自定义 skills：work-log、code-conciseness-check、decision-driven-coding、hand-typing-training、project-map、frontend-interaction-design、code-comment-style、frontend-visual-craft、form-guard、special-work-test、sql-standards、pdf（共 12 项，2026-10-29 实测）；新建自定义 skill 默认落此处且不建项目级副本，命名用 kebab-case，正文中文，SKILL.md 开头注明输出标注【项目名·skills名】。
2. `C:\Users\Administrator\.agents\skills\` — 第三方安装的 **26 个** skills（2026-10-28 逐目录实测，此前记为 25 个属计数错误）：文档四件套 docx/pptx/xlsx/pdf；视觉组 canvas-design/theme-factory/brand-guidelines/frontend-design/algorithmic-art；工程组 skill-creator/skill-lookup/template/mcp-builder/webapp-testing/claude-api/database-design；ponytail 六件套 ponytail/ponytail-review/ponytail-audit/ponytail-debt/ponytail-gain/ponytail-help；流程组 doc-coauthoring/internal-comms/slack-gif-creator/web-artifacts-builder。**注意：canvas、create-skill、create-plugin、vercel-deploy、better-harness 五个名字虽出现在运行时注册清单里，但都不在本目录**——前三者与 vercel-deploy 是 IDE CLI bundle 内置，create-plugin 与 better-harness 在 plugins\cache\qoder-bundler\ 下。`.agents\.skill-lock.json` 只记录了 pdf 一条来源。Qoder 会同时扫描 `.qoder\skills` 与 `.agents\skills` 两个目录。
3. `C:\Users\Administrator\.qoder\plugins\cache\` — 插件携带的 skills：`local\mobile-frontend-dev`（ui-ux-pro-max、uniapp）、`local\refactoring`、`ponytail`、`qoder-bundler\*`（better-harness、qoder-create-plugin、security-scan）；插件注册清单为 `.qoder\plugins\installed_plugins*.json`（内含写死的绝对路径）。其中 security-scan 在清单里 enabled=false，ui-ux-pro-max / uniapp / refactoring 虽在磁盘但未出现在运行时注册清单。
项目级 skills 位于 `<工作区>\.qoder\skills\`，跟随项目仓库走；当前 remote-monitor 工作区的 .qoder 下仅有 plans 与 better-harness-runs，无项目级 skills。

内置 skill（canvas、create-skill、vercel-deploy）打包在 IDE CLI bundle 里，磁盘上找不到独立目录（`.workbuddy` 下的同名目录属于另一工具，不算）。运行时注册去重后总数为 41 个 = 用户级 11 + .agents 26 + 已注册插件 2（better-harness、create-plugin）+ 内置 3 − pdf 重复计 1。

## 新建自定义 skill 的流程要点（沿用既有风格）
Step 1: 同时列出用户级 skills 目录与本处第 2、3 项目录，确认是否存在同名 skill 或项目级副本，否则造成双份生效或位置选错。
Step 2: 读取至少两个同族风格最接近的既有 SKILL.md 头部，采样命名风格、正文语言、属性化 description、固定输出标注约定后再动笔。
Step 3: 一次问清定位模式（设计指导/评审检查/双模式）、适用范围（端/平台）、存放位置三项决策再落盘——这三项决定文件拆分方式，猜错要整篇重写。
Step 4: 按"主文件 + reference"分层落盘：主文件只放工作流、决策步骤与硬红线，详细对照表/清单/代码配方拆到独立文件且须能从主文件一层直引；多处同类阈值只在权威处保留一份数值定义，其余用引用表述，落盘后专门做一轮跨文件一致性核对（分档口径分散写在多个文件时极易互相矛盾）。

迁移注意：备份应用 `robocopy /E`；用户名不同的新机恢复路径要改为对应用户主目录；`installed_plugins*.json` 因含绝对路径建议以"缓存目录备份 + 新机器重装"方式处理；work-log 的历史数据不在 skills 内，而在 `d:\code\remote-monitor\work-log\`。可一并备份还原环境的同级资产：`.qoder\memories`、`.qoder\rules`、`.qoder\agents`、`.qoder\settings.json`。

## Notes
- 【用户级清单已扩充（2026-10-29 实测）】第 1 项由 11 个条目增至 12 个：新增 sql-standards（通用 SQL/DDL 编写与审查，含第 0 步项目现状探测）。上述名称清单为时点快照，盘点一律以实际列目录为准，不得据清单判定某 skill 不存在。
- 【pdf 两侧并非均为实体副本，junction 说法成立】本条此前"两侧各含 13 个实体文件、清理时应删掉一侧保留本体"的表述与「Qoder skill 的三层存放位置与内置 skill 不可编辑」记录的 junction 结论冲突。2026-10-29 复核以该条为准：`.agents\skills\pdf\` 是本体，`.qoder\skills\pdf\` 是指向它的 junction，因此删除任一侧都会伤及本体，迁移与清理前必须先判定链接类型（列目录 + 哈希比对），不要按"删一侧去重"处理。
- 【ponytail 双份且内容已分叉】`.agents\skills\ponytail` 与插件缓存 `plugins\cache\ponytail\ponytail\skills\ponytail` 的 SKILL.md 哈希已不同（4AC8FD32... vs 75F25B84...），说明两份不是同一快照，迁移时都要带走。
- 【命令行读取部分 skill 文件会出乱码】`.agents\skills\ponytail*` 全家、better-harness、security-scan、refactoring 等文件被透明加密，PowerShell `Get-Content` 读回的是乱码，但编辑器打开与修改正常，且 frontmatter 里的 name/description 多行折叠写法也会让正则式批量提取失败。盘点这类文件时应改用 Grep 单行匹配或直接 Read 单个文件，不要据乱码判定文件损坏。
