---
title: "多仓库工作区根目录布局与 docs、_temp、work-log 收纳分工"
usage_scenario:
    - "汇总或提交跨前后端多仓库改动时定位仓库"
    - "逐仓库执行fetch/rebase等同步操作时确定路径与远端"
    - "在根目录新建文件时选择存放目录"
keywords:
    - "多仓库工作区布局"
    - "git -C 跨仓库操作"
    - "work-log 目录归属"
    - "根目录收纳分工"
    - "docs 与 _temp 用途"
source: "auto"
---

d:\code\remote-monitor 是多仓库工作区根目录，本身不是 git 仓库；其下三个独立 git 仓库为 anwei-fire-remote-monitor（后端 Java）、anwei-fire-remote-monitor-vue（PC 前端）、anwei-fire-remote-monitor-mobile（移动端）。跨仓库改动需逐仓分别执行 git 命令（git -C <repo路径> log/status）。

work-log 数据目录 d:\code\remote-monitor\work-log\ 位于三个仓库之外，不属于任何 git 仓库，写入内容不会被误提交；skill 本体在用户级目录 C:\Users\Administrator\.qoder\skills\work-log\（含 SKILL.md、reference.md、examples.md），项目级副本已在迁移到用户级时删除。会话导出文件 _sess_*.txt / _chat_records.txt 现位于 training\ 下（与手敲训练文件混放），内容多为 base64 加密，读取无信息增益。

根目录必须保持"仅目录、无散落文件"结构，各目录分工：docs\ 存放工作区级方案文档（消防告警字段语义定稿、告警去重待办、UDP 排障计划等）；_temp\ 存放调试产物与过程截图（perm_check*、redis_keys.txt、duty-screen 截图、移动端仓库快照 zip），可随时整体清理；training\ 存放手敲训练文件，新增训练的 HINTS.md 与 level*.js 成对存入；ponytail\、algorithmic-art\、learning-socket\、.qoder\、.vscode\ 保持原样。原空壳目录 anwei-fire-monitor\、孤儿根级 node_modules\ 以及 logs\ 目录均已删除。后续新增方案文档一律放 docs\、临时产物一律放 _temp\，不得散落在根目录。

## Notes
- 【会话归档结论修正（2026-10-26 实测）】上文"会话记录只存服务端、本地无 transcript"的推断不成立：本机确有本地会话转录 C:\Users\Administrator\.qoder\projects\<项目>\transcript\*.jsonl（163 个文件、61.3MB），只是最新一条止于 2026-09-11 16:36，此后不再增长。做历史/用量统计时应把它当作有断层的数据源使用，不能因近期零命中就判定资产未被使用。
