---
title: "git 提交流程：月报链路重构与 Conventional Commits 实践"
usage_scenario:
    - "执行 git 提交时"
    - "验证中文提交信息编码正确性时"
    - "处理沙箱终端中文乱码问题时"
keywords:
    - "git 提交"
    - "Conventional Commits"
    - "沙箱乱码"
    - "字节校验"
    - "三仓库分批提交"
    - "rebase autostash"
    - "编译依赖收批"
source: "auto"
---

## Task description
- Core requirement: 执行 git 提交，将月报链路重构、告警语义归一、环境配置等多主题变更提交到本地仓库
- Task background: 用户要求按最小单元拆分提交，并统一采用 Conventional Commits 格式；后端存在 5 笔待提交（AI 模块抽取、月报增强、告警字段新增、统计口径调整），前端需提交 .env.local 配置更新

## Execution process
1. 后端月报链路：先确认编译依赖，将 5 笔提交重写为 Conventional Commits 格式（refactor(ai)、feat(ai)、feat(alert)×2、feat(report)）并逐批提交
2. 前端环境配置：修正 .env.local 中的「验证码的开关ssssss」手滑注释，然后以 chore(env) 格式提交
3. 全程执行 git reset 清空暂存区、逐批 add+commit、每批提交前展示清单与信息并获用户确认
4. 所有提交均停在本地，未推送

## Related files
- module-ai/module-ai-biz/src/main/java/com/enjoyiot/module/ai/controller/admin/ChatController.java
- module-ai/module-ai-biz/src/main/java/com/enjoyiot/module/ai/service/LlmClient.java
- module-ai/module-ai-api/src/main/java/com/enjoyiot/module/ai/api/AiChatApi.java
- module-eiot/module-eiot-api/src/main/java/com/enjoyiot/module/eiot/api/alert/dto/AlertRecord.java
- module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/dal/mysql/AlertRecordMapper.java
- module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/firewatch/FireMonthlyReportServiceImpl.java
- module-eiot/module-eiot-biz/pom.xml
- docs/视频拉流对接复用指南.md
- src/views/eiot/firewatch/data-center/index.vue
- .env.local

## Notes
- 沙箱终端内 PowerShell 直接读取 IDE 写入的新文件会返回乱码字节，但 git、node、ripgrep 等原生进程读取正常；提交信息应优先使用 -F 文件方式而非 -m 内联
- 提交信息校验必须用原始字节通道：node execSync 读取 git log --format=%s 并与消息文件字符串比对，或 git show <rev>:<path> 检查关键 ASCII 标记

## Task overview
成功完成 git 提交流程：后端 5 笔月报链路提交全部重写为 Conventional Commits 格式并提交；前端 .env.local 配置更新已修正并提交；所有提交均停在本地，未推送。

## 后续批次：三端 14 笔清单收敛为 7 笔提交
- 用户指令"后端提交2笔，前端和移动端全提交，提交前记得拉取，按最小单元提交"；对"2笔"歧义先经 AskUserQuestion 澄清，确认为清单序号①语义归一+②数据中心两笔
- 三仓库先各自 git pull --rebase --autostash（无新提交无冲突，工作区改动原样保留）
- 后端 9a35e1c feat(alert) 告警四维语义归一（10 文件：清单 9 件 + 编译依赖 AlarmMessage.java——其 buildDetails 新增 tenantName 参数，调用方 AlarmRecordService 在同笔内，不带则该笔编译不过，经 git grep HEAD 核验后额外收录）；28df2a1 feat(data-center) 数据中心统计接口真实化（11 文件）
- 前端 7dc19b2 feat(alarm) 告警双读+AlarmNotice 组件抽离（11 文件，含新 alarm-notice/index.vue）；e3d8e06 feat(data-center) 图表化改造（10 文件）
- 移动端 4ba4543 fix(mobile) card.js 双读归一；b2ddc6e feat(mobile) 告警详情页改版；e92b4a1 chore(mobile) 移除 project.config.json 的 miniprogramRoot
- 大清单批次分两段 add 再单次 commit（同一仓库不同段可并行跨仓库执行）；7 条消息 node 逐字节比对全部 ALL=true；application.yaml 照例排除；后端剩余 ③④⑤⑥⑦ 及 docs 留待后续；临时 m/v/p*.txt 提交后清理
