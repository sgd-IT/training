---
title: "Qoder skill 的三层存放位置与内置 skill 不可编辑"
usage_scenario:
    - "创建或修改 skill 时确认存放层级与本体位置"
    - "判断某技能能否作为融合目标"
    - "排查 skill 重复注册或改动不生效被覆盖"
keywords:
    - "Qoder skill 存放位置"
    - "内置 skill 不可编辑"
    - "用户级 skills 目录"
    - ".agents/skills 目录"
    - "插件 skill 缓存"
    - "pdf junction 重复注册"
    - "CLI bundle"
source: "auto"
---

Qoder 的 skill 分多层存放，改动或融合前先确认归属：用户级在 `~/.qoder/skills/<name>/`，可编辑、跨项目生效；项目级在 `<workspace>/.qoder/skills/`；第三方安装的 skill 在 `~/.agents/skills/<name>/`（由 `.agents/.skill-lock.json` 记录来源，Qoder 会同时扫描 `.qoder\skills` 与 `.agents\skills` 两个根）；插件 skill 位于 `~/.qoder/plugins/cache/<来源>/<插件名>/skills/`，属缓存不宜手改；真正的内置 skill（frontend-design、create-skill 等）打包在 IDE 的 CLI bundle 里，磁盘上找不到独立目录，随版本升级被覆盖，不能作为融合目标。

注意 pdf 不属于"纯内置不可寻址"那一类：它在磁盘上有实体，`~/.agents/skills/pdf/` 是本体，`~/.qoder/skills/pdf/` 是指向它的 junction（两侧 SKILL.md MD5 一致、各 13 个文件）。因此"内置 skill 一律磁盘无目录"的说法对 pdf 不成立，判断某 skill 能否被修改前必须先按上述各根逐一实扫，不能凭名称归类。

要扩展内置 skill 的能力，做法是新建用户级 skill 承接，再在用户级 skill 里写分工说明互相指向。同名 skill 在两个根同时可见会造成重复注册与触发歧义，清理时保留本体一侧。
