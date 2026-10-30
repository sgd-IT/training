# 资产明细清单（MANIFEST）

导出时间 2026-10-30 ｜ 源机器 `C:\Users\Administrator` ｜ 明文 2830 个文件 / 17.3 MB ｜ 可带走容器 `qoder-assets-carry.zip.bin` 8.0 MB

> 体积口径：下表按资源管理器（磁盘）视图统计，被加密的文本文件每个含 4096 字节加密头，因此比明文口径偏大；真正能拷走的是 `.bin` 容器里的明文，体积见容器一栏。

| 分区 | 对应原路径 | 文件数 | 体积 |
|------|-----------|-------|------|
| `rules/` | `~/.qoder/rules/` | 3 | 0.02 MB |
| `user-skills/` | `~/.qoder/skills/`（pdf junction 已排除，本体在第三方目录） | 27 | 0.35 MB |
| `third-party-skills/` | `~/.agents/skills/` | 411 | 9.94 MB |
| `plugins/` | `~/.qoder/plugins/cache/` | 354 | 4.45 MB |
| `memories/` | `~/.qoder/memories/` | 2021 | 11.61 MB |
| `config/` | `~/.qoder/` 根与 `~/.agents/.skill-lock.json` | 8 | 0.01 MB |
| `workspace/` | `d:\code\remote-monitor\.qoder/` | 3 | 0.10 MB |

---

## 一、规则 rules/（3 个）

| 文件 | 大小 | 内容 |
|------|------|------|
| `git-commit.md` | 4.9 KB / 15 行 | 提交与审核前必须先询问用户；禁止一次性大提交，拆成最小逻辑单元；提交信息准确简洁 |
| `code-reuse-check.md` | 5.6 KB / 25 行 | 开发前复用检索：同类业务实现、后端公共能力、前端公共能力、既有接口；决策顺序 直接复用 → 扩展 → 新建 |
| `ponytail.md` | 6.6 KB / 31 行 | Ponytail lazy senior dev 模式，7 级最低成本阶梯（YAGNI 起手） |

## 二、用户级自建 skills（11 个实体 + pdf 链接）

| # | skill | 文件 | 大小 | 用途 |
|---|-------|-----|------|------|
| 1 | `code-comment-style` | 1 | 6 KB | 中文口语风格注释规范，写/改/评审注释时触发 |
| 2 | `code-conciseness-check` | 2 | 7 KB | 重复代码块、过长方法、可简化表达式的简洁度检查 |
| 3 | `decision-driven-coding` | 3 | 14 KB | 决策点驱动的核心逻辑编码方法论 |
| 4 | `form-guard` | 3 | 18 KB | 表单必填校验与错误提示闭环审查 |
| 5 | `frontend-interaction-design` | 4 | 27 KB | 交互摆放、拇指热区与可点性审查（双模式） |
| 6 | `frontend-visual-craft` | 2 | 19 KB | 视觉工艺细节：明暗层次、字重、动效阻尼 |
| 7 | `hand-typing-training` | 1 | 22 KB | 基于真实项目的文字教程与挖空手敲训练 |
| 8 | `project-map` | 3 | 12 KB | 陌生/新接手项目的可审计项目地图 |
| 9 | `special-work-test` | 2 | 42 KB | hc-standard 特殊作业模块测试路径与用例设计 |
| 10 | `sql-standards` | 3 | 33 KB | MySQL 建表/索引/查询规范审查（先探测项目口径） |
| 11 | `work-log` | 3 | 48 KB | 工作记录、周报、简历与面试素材沉淀 |
| — | `pdf` | — | — | 原路径是指向 `~/.agents/skills/pdf` 的 **junction**，备份时已排除，恢复后重建链接 |

## 三、第三方 skills（26 个）

| # | skill | 文件 | 大小 | 用途 |
|---|-------|-----|------|------|
| 1 | `algorithmic-art` | 5 | 60 KB | p5.js 生成式艺术 |
| 2 | `brand-guidelines` | 3 | 14 KB | Anthropic 品牌配色与字体 |
| 3 | `canvas-design` | 84 | 5.4 MB | 海报/静态视觉设计（png、pdf 输出，体积最大） |
| 4 | `claude-api` | 42 | 507 KB | Claude API / Anthropic SDK 开发与迁移 |
| 5 | `database-design` | 1 | 5 KB | 数据库 schema 设计、迁移与优化 |
| 6 | `doc-coauthoring` | 2 | 16 KB | 文档共创工作流 |
| 7 | `docx` | 62 | 1.1 MB | Word 文档读写与编辑 |
| 8 | `frontend-design` | 3 | 15 KB | 前端界面设计与实现 |
| 9 | `internal-comms` | 7 | 22 KB | 内部沟通材料（周报、简报） |
| 10 | `mcp-builder` | 11 | 123 KB | MCP server 开发指南 |
| 11 | `pdf` | 13 | 59 KB | PDF 全流程处理（**本体所在**） |
| 12 | `ponytail` | 1 | 6 KB | 最省实现模式（与插件缓存版内容分叉） |
| 13 | `ponytail-audit` | 1 | 2 KB | 全仓过度工程审计 |
| 14 | `ponytail-debt` | 1 | 2 KB | ponytail 标注债务台账 |
| 15 | `ponytail-gain` | 1 | 2 KB | ponytail 收益记分板 |
| 16 | `ponytail-help` | 1 | 3 KB | ponytail 速查卡 |
| 17 | `ponytail-review` | 1 | 2 KB | 过度工程代码评审 |
| 18 | `pptx` | 60 | 1.1 MB | PPT 读写与编辑 |
| 19 | `skill-creator` | 19 | 225 KB | 创建/改进 skill 与评测 |
| 20 | `skill-lookup` | 1 | 2 KB | skill 检索与安装 |
| 21 | `slack-gif-creator` | 8 | 44 KB | Slack 动图制作 |
| 22 | `template` | 2 | <1 KB | 空模板（未被使用） |
| 23 | `theme-factory` | 14 | 141 KB | 制品主题套用（10 套预设） |
| 24 | `web-artifacts-builder` | 6 | 46 KB | 多组件 HTML 制品构建 |
| 25 | `webapp-testing` | 7 | 23 KB | Playwright 本地页面测试 |
| 26 | `xlsx` | 55 | 1.1 MB | 表格读写、公式与格式 |

## 四、插件 plugins/

| 路径 | 携带 skill | 文件 | 大小 | 来源 |
|------|-----------|------|------|------|
| `local/mobile-frontend-dev` | `ui-ux-pro-max`、`uniapp` | 61 | 119 KB | **自建**，市场装不到 |
| `local/refactoring` | `refactoring` | 3 | 20 KB | **自建**，磁盘在、未注册到运行时 |
| `ponytail/ponytail` | `ponytail` | 3 | 8 KB | 本地插件，与 `.agents` 版内容分叉 |
| `qoder-bundler/better-harness` | `better-harness` | 265 | 4.1 MB | 市场 v0.2.9，可重装 |
| `qoder-bundler/qoder-create-plugin` | `create-plugin` | 7 | 40 KB | 市场 v0.1.0，可重装 |
| `qoder-bundler/security-scan` | `security-scan` | 18 | 170 KB | 市场 v0.9.4，当前 enabled=false |
| `qoder-enterprise-019ddc40-…` | — | 0 | 0 | 企业壳空目录 |

## 五、记忆 memories/

5 个账号/版本目录，各自结构和内容独立（不是互为备份，都留着）：

| 目录 | 文件 | 大小 | 说明 |
|------|------|------|------|
| `019c9953` | 263 | 0.23 MB | 含 global + 6 个项目；有 `Git提交前需用户确认`、训练时段等早期行为记忆 |
| `019d1dad` | 15 | 0.01 MB | 项目级为主，remote-monitor 等项目的实践规范 |
| `019d669b` | 45 | 0.03 MB | 烟花仓库增量上传等规范 |
| `019d669f` | 303 | 0.65 MB | global + 项目；有 `Git提交与审核规则`、`SQL变更口径`、`开发前复用检索规则`、`用户名叫 sgd` |
| `019d6aae` | 1395 | 2.83 MB | 体量最大，remote-monitor 全部代码/注释/实践规范 + 12 个项目 |

分类最高频的几类（也是"隐形规则"密集区）：`development_code_specification`、`development_practice_specification`、`development_comment_specification`、`common_pitfalls_experience`、`important_decision_experience`、`user_behavior`。

## 六、config/

| 文件 | 说明 |
|------|------|
| `settings.json` | 插件启用开关（better-harness、mobile-frontend-dev、qoder-create-plugin 开，security-scan 关）。**无 hooks 配置** |
| `mcp.json` | MCP 服务配置**脱敏版**，13 个 server，口令与 Figma Key 已替换为 `***REDACTED***` |
| `mcp.json.secret` | MCP 配置**原文**，含真实口令，⚠ 不可外传 |
| `installed_plugins.json` | 市场插件注册（含 `c:\Users\Administrator\...` 绝对路径，仅作参考） |
| `installed_plugins_v2.json` / `installed_plugins-v2.json` | 新版插件注册清单 |
| `.skill-lock.json` | `.agents` 下 skill 的来源记录（当前只记了 pdf 一条，来源 anthropics/skills） |
| `.ponytail-active` | ponytail 激活标记文件 |

## 七、workspace/.qoder/

| 路径 | 说明 |
|------|------|
| `plans/` | 空目录（历史方案已清理） |
| `better-harness-runs/20261026-skills-audit/` | 一次 skills 审计的运行产物（bundle.json、checkup.json） |
