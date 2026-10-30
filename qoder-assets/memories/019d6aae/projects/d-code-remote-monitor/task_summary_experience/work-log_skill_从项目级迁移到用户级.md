---
title: "work-log skill 从项目级迁移到用户级"
usage_scenario:
    - "When migrating a project-level skill to user-level for cross-project reuse"
    - "When cleaning up obsolete project-level skill copies after user-level deployment"
keywords:
    - "work-log"
    - "user-level skill"
    - "project-level skill"
    - "skill migration"
    - "Qoder skills"
source: "auto"
---

## Task description
- Core requirement: migrate the work-log skill from project-level to user-level so it is available across all projects
- Task background: the skill was originally located in d:\code\remote-monitor\.qoder\skills\work-log\; user requested moving it to C:\Users\Administrator\.qoder\skills\work-log\ for universal access; data directory remains unchanged at d:\code\remote-monitor\work-log\

## Execution process
1. Verified existing user-level skills directory and confirmed no work-log present; created C:\Users\Administrator\.qoder\skills\work-log\ directory
2. Copied SKILL.md, reference.md, and examples.md from project-level to user-level location
3. Updated user-level SKILL.md: revised data directory description to "unified data directory (shared across projects, resume materials consolidated)" and updated repository path description to "main workspace d:/code/remote-monitor's three repositories, read-only; replaced with current workspace repositories when used in other projects"
4. Deleted the three project-level files (SKILL.md, reference.md, examples.md)
5. Cleaned up empty directories: removed d:\code\remote-monitor\.qoder\skills\work-log\ and d:\code\remote-monitor\.qoder\skills\ if empty
6. Verified final state: project .qoder contains only plans; user-level work-log contains all three files
7. Updated the IMMUTABLE memory (id=3f8c3cc0-298e-4801-8981-bbfd8ffd0466) to reflect the new user-level location

## Related files
- C:\Users\Administrator\.qoder\skills\work-log\SKILL.md
- C:\Users\Administrator\.qoder\skills\work-log\reference.md
- C:\Users\Administrator\.qoder\skills\work-log\examples.md
- d:\code\remote-monitor\.qoder\skills\work-log\SKILL.md
- d:\code\remote-monitor\.qoder\skills\work-log\reference.md
- d:\code\remote-monitor\.qoder\skills\work-log\examples.md

## Notes
- User-level skill and project-level skill must not coexist to avoid duplication; project-level files were fully cleaned up
- The unified data directory d:\code\remote-monitor\work-log\ remains unchanged and continues to store all work-log records
- Migration ensures work-log is available in any project without reinstallation

## 后续更正（2026-10-19）
- 本条记录的三件套位置与"项目级/用户级不可并存"结论仍然有效；但其中隐含的正文版式规范已作废：现行层级为文件级标题 H1、记录 H2、字段 H3（旧规范曾要求"不使用 H1，最高从 H2 开始"，已于 2026-10-19 全量迁移）。
- 数据目录已扩为 7 个文件，新增 work-log\06-面试素材.md（每条工作压成 6 行可讲要点，供简历/面试取材）；skill 同时新增模式 E（按关键词/标签/时间只读检索）与模式判定表。
- 原"仓库路径固定为 d:/code/remote-monitor 三个仓库"的描述已改为：自动探测当前工作区含 .git 的子目录，排除 ponytail 等第三方目录；数据目录优先「当前工作区/work-log/」，回退主目录路径。

## Task overview
Successfully completed full migration of work-log skill to user-level: created user-level directory, copied and adapted all three files, cleaned up project-level files and empty directories, verified final state, and updated the corresponding memory. The skill is now universally available across all projects.
