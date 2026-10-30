---
title: "work-log数据目录、用户级skills目录与本地产物查看路径"
usage_scenario:
    - "定位 work-log 记录文件"
    - "新增项目工作记录"
    - "查找工具在工作区生成的文件位置时"
keywords:
    - "work-log"
    - "数据目录"
    - "用户级skills"
    - "透明加密"
    - "本地产物查看路径"
source: "auto"
---

work-log 数据目录为跨项目共享的 d:\code\remote-monitor\work-log\，包含 00-履历总表.md、01-项目\（已有 hc-standard、消防远程值守系统、消防告警分级响应系统、烟花仓库安全监测系统、Spring AI RAG 知识库助手）、02-专题\（含 AI项目评测与bad case口径.md、AI评测黄金集-50条模板.csv）、03-踩坑手册.md、04-简历素材池.md、05-技能清单.md、06-面试素材.md。用户自定义 skills 位于 C:\Users\Administrator\.qoder\skills\，包含 work-log、code-comment-style、code-conciseness-check、decision-driven-coding、hand-typing-training、project-map、pdf 等，改动约定类内容只落这些自定义 skill。注意：整个 d:\code 目录树受企业透明加密保护，PowerShell/Get-Content 读到的都是密文，任何对该目录下文件的"读取→修改→写回"或追加写入都会损坏文件；写入只能走 Write 工作区临时文件 + Copy-Item 整文件覆盖 + Read 回读校验。

本地生成/导出产物的查看位置：由 pdf 等 skill 在工作区内生成的文件默认落在当前工作区目录（不会自动进系统"下载"文件夹），需按文件名在工作区根目录或其子目录查找；若用户问"下载在哪、我要看看"，先区分三类落点——a) 工作区内由工具直接 Write 出的产物（可在 IDE 文件树看到）；b) 浏览器下载目录 %USERPROFILE%\Downloads（只有走浏览器链路才有）；c) 服务端 {profile}/print 等业务输出目录。回答时给出具体绝对路径并说明该目录是否受透明加密影响（d:\code 下的文件用命令行读取会得到密文，须用 IDE/Read 通道查看）。
