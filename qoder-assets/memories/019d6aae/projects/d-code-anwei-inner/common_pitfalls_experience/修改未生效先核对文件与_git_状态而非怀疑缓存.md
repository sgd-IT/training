---
title: "修改未生效先核对文件与 git 状态而非怀疑缓存"
usage_scenario:
    - "用户反馈改动未生效反复追问"
    - "排查 AI 修改丢失或回退"
    - "本机 IDE 与工具协作编辑冲突"
keywords:
    - "未提交修改被覆盖"
    - "IDE 缓冲区覆盖"
    - "git status 核对"
    - "功能未生效排查"
    - "Reload from Disk"
source: "auto"
---

Bug 类：用户反复反馈"改动没生效/找不到功能入口"，实际是磁盘上未提交的修改被覆盖回退。根因：用户在 IDE（IDEA）中打开着目标文件的旧缓冲区，保存或 git rollback 把磁盘内容覆盖回仓库版本；该操作不写 reflog，表现为 git status 不再列出该文件、文件内容回到 HEAD 版本。排查方法：收到"功能没生效"类反馈时，第一步先核对 git status 与文件实际内容（Read 目标行）确认修改是否还在，再怀疑浏览器缓存或 dev server；确认后用 SearchReplace 重写修改，并让用户在 IDE 中 Reload from Disk 避免二次覆盖，最后建议尽快提交固化。适用边界：本机多工具协作编辑同一文件的场景；配合 dev server 产物验证（请求 /src/... 路径检查编译产物是否含新符号）可快速区分服务端与浏览器侧问题。
