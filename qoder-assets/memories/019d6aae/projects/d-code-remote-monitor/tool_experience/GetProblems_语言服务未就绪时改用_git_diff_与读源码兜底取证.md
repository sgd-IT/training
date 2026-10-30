---
title: "GetProblems 语言服务未就绪时改用 git diff 与读源码兜底取证"
usage_scenario:
    - "拉取代码诊断但语言服务未就绪"
    - "区分报错属于本次改动还是仓库既有"
    - "评审手敲代码前核实真实改动范围"
keywords:
    - "语言服务未就绪"
    - "git diff零上下文"
    - "报错归因"
    - "诊断兜底取证"
    - "增量文件口径"
source: "auto"
---

## Usage Scenario
Description: 需要获取代码文件的编译/类型诊断，或判定"哪些改动是本次引入、哪些是仓库既有"时
Examples: 用户贴回一屏 TS 报错要求归因；评审他人手敲代码前需确认实际改动范围；vue-tsc/eslint 全量有存量报错需按增量文件口径判定

## Usage Method
GetProblems: 返回"Language server is not ready or still initializing"时不要重复重试同一调用，直接转兜底取证链——先用 git status/diff --stat 划定被改文件，再用 git diff -U0 逐块取本次真实改动行，最后用读源码定位具体行做结构核对。diff 的零上下文特性正好用来区分"功能行"与编辑器格式化重排行。

## Notes
- GetProblems 语言服务未就绪时会返回假阴性"无问题"，据其判定代码健康会造成漏报，must 改用 git diff + 读源码兜底取证或跑真实编译验证
- 依赖 IDE 语言服务作为唯一诊断来源会在服务未就绪时卡死整个评审流程，必须默认准备 git diff + 读源码这条不依赖语言服务的兜底路径
- 未实际运行得到的诊断结论不能写成已验证，只能表述为逻辑推定并要求用户在 IDE 面板与浏览器运行时确认
- vue 仓库 vue-tsc 全量检查存在存量错误，判定改动是否引入新问题必须按"增量文件口径"与改动前告警清单逐条比对，不能看全量报错总数
