---
title: "project-map skill 用户级安装与 Git 边界约定"
usage_scenario:
    - "进入新项目需要建立项目地图时"
    - "用户要求更新或刷新项目地图时"
    - "确认 project-map skill 位置与 Git 边界时"
keywords:
    - "project-map"
    - "项目地图"
    - "AGENTS.md"
    - "git 只读"
    - "落盘确认"
source: "user"
---

用户级 skill project-map 位于 C:\Users\Administrator\.qoder\skills\project-map\（SKILL.md、reference.md、examples.md），与 work-log 同级，不建项目级副本。用途：进入/接手任何项目时建立"项目地图"——盘点（只读）→ 对话版地图（6 段）→ 用户确认后落盘（各仓 AGENTS.md、docs/INDEX.md、追溯性 ADR、既有文档索引页）；支持"更新项目地图"增量刷新。硬性约定：① git 全程只读，禁止 add/commit/push 等写命令，产出文件保持未提交状态、提交与否由用户决定；② 落盘前必须列清单经用户确认；③ 可推导信息（调用图/目录树）不落盘，只落约束/决策/契约/索引；④ 输出开头标注【项目名·project-map】；⑤ 禁止抄录配置中的密钥凭据；⑥ 一律不碰 git 提交（2026-10-10 用户明确要求）。触发词："了解这个项目""没有项目地图""梳理架构""onboarding""更新项目地图"。
