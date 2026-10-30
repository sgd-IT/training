---
title: "Qoder 本地资产目录分布与内置 skills 不落盘"
usage_scenario:
    - "盘点或备份Qoder配置资产时"
    - "查找skill或规则文件位置时"
    - "核对插件启用状态时"
keywords:
    - ".qoder目录结构"
    - "plugins cache注册表"
    - "bundled-resources为空"
    - "settings.json启用开关"
    - "AGENTS.md"
source: "auto"
---

Qoder 的用户级资产分散存放在 C:\Users\Administrator\.qoder\ 下，且各类资产来源不同：
- 用户自定义 skills：~\.qoder\skills\<skill名>\SKILL.md（每个 skill 一个目录）
- 用户级规则：~\.qoder\rules\*.md（如 code-reuse-check.md、git-commit.md、ponytail.md）；项目级规则为仓库根的 AGENTS.md，工作区根目录的 .qoder 可能只有 plans 而无 rules
- 插件本体：~\.qoder\plugins\cache\<来源>\<插件名>\，来源分 qoder-bundler（市场）与 local；启用开关在 ~\.qoder\settings.json 的 enabledPlugins；注册表存在新旧两套文件 installed_plugins_v2.json 与 installed_plugins-v2.json，两者内容可能不一致
- 外部命令：~\.qoder\external-commands\registry.json
- 反直觉事实：客户端内置 skills（docx、pptx、xlsx、canvas、algorithmic-art 等约 22 个）不在本地磁盘上，~\.qoder\app\bundled-resources\v1 实测为空目录，它们由会话运行时提供，因此无法被打包备份，登录 Qoder 后自动可用
- 插件激活标记：~\.qoder\.ponytail-active 表示该插件当前激活，但插件也可能只存在于 cache 而未注册（缓存残留），需交叉核对注册表与 settings.json 才能判断是否生效
