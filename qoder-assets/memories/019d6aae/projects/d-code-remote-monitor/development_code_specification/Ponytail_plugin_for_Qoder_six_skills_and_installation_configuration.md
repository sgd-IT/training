---
title: "Ponytail plugin for Qoder: six skills and installation configuration"
usage_scenario:
    - "重装或迁移 ponytail 插件到 Qoder 环境时"
    - "排查 ponytail skill 缺失或两份副本版本不一致时"
    - "判断 ponytail 与 code-conciseness-check 等 skill 是否功能重叠时"
keywords:
    - "ponytail"
    - "skills"
    - "Qoder"
    - "/ponytail-review"
    - "/ponytail-audit"
    - "plugin"
    - "installation"
    - "configuration"
    - "YAGNI"
source: "auto"
---

The ponytail plugin provides six core skills: /ponytail-review, /ponytail-audit, /ponytail-debt, /ponytail-gain, /ponytail-help, and /ponytail (the base command). It is installed and configured as a Qoder-native plugin following the naming convention `<plugin-name>@<marketplace-name>` with `local` as the marketplace name for locally developed plugins.

## Notes
- 实测补充（2026-10 skills 目录排查）：ponytail 六件套在本机存在两份副本 —— `C:\Users\Administrator\.agents\skills\ponytail*`（Qoder 实际扫描生效处）与 `C:\Users\Administrator\.qoder\plugins\cache\ponytail\ponytail\skills`（插件缓存）。做环境备份/迁移时两个目录都要带走，只拷其一会出现 skill 缺失或版本不一致；插件注册清单 installed_plugins*.json 并未包含 ponytail 条目，说明其生效依赖 .agents\skills 而非插件注册。
- 职责边界补充（2026-10 skill 合并讨论核实）：ponytail 是 YAGNI 哲学类 skill（能删就删、能不做就不做），任何编码任务自动介入或 /ponytail 显式触发，输出对话式并直接动手改；其中 /ponytail-review 关注过度工程（重造标准库已有轮子、不必要依赖、投机抽象、死灵活性），与只做微观模式冗余检查（重复代码块、过长方法、可简化条件表达式、死代码）的 code-conciseness-check 属不同层次，两者互补不重叠，故不合并。
