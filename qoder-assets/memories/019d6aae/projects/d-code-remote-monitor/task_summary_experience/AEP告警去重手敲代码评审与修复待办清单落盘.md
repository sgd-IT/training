---
title: "AEP告警去重手敲代码评审与修复待办清单落盘"
usage_scenario:
    - "复检用户手敲的并发去重实现时"
    - "继续推进AEP告警去重修复任务时"
    - "用户要求把待办写进项目留存时"
keywords:
    - "并发占位评审"
    - "putIfAbsent比较替换"
    - "replace空值抛错"
    - "jshell实测取证"
    - "修复待办清单"
source: "auto"
---

## Task description
- Core requirement: 评审用户手敲完成的 AEP 告警去重实现（5 分钟窗口内同键只入库一次），并把修复待办按用户要求依次落到系统任务面板与项目文件
- Task background: AlarmRecordService.saveAlarm 原为 check-then-act 去重（isDuplicate 纯读 + insert 之后才登记），存在并发双入库；用户已按上一轮方向提示改写为"原子占位"路线，需要逐条判定正确性并给出可执行的修复顺序

## Execution process
1. Read 用户手敲后的 AlarmRecordService.java 全文（329 行），锁定 saveAlarm 第 105-148 行的占位实现逐路径推演
2. GetProblems 静态检查：仅报 L2 import Async unused（用户已删 @Async 注解但留下 import 尾巴）
3. Grep 全仓 isDuplicate|cleanExpiredDedupCache：确认 isDuplicate 唯一调用点在 saveAlarm 内；同时证实上一轮验收点提到的"定期清理方法"在当前文件与全仓都不存在，主动做了校对更正
4. Bash + jshell 单行实验取证 ConcurrentHashMap.replace(key, null, value) 的运行时行为：首次因同时传 -q 与 -s 两个 feedback 选项被拒（只能使用一个反馈选项），改为单个 -s 后输出 NPE-CONFIRMED，把 P0-1 从推断升级为实测结论
5. 复盘判定四条缺陷并按严重度排序：P0-1 last==null（全新键）分支仍调 replace 传 null，每类事件首条必抛 NPE 且占位泄露；P0-2 占位后又调 isDuplicate 回读同一缓存，抢到者必被判重复，insert 结构性不可达；P0-3 replace 布尔返回值被丢弃，过期位竞争失败者继续放行；P1 insert 异常无失败释放，整窗口误压制真告警；P2 import、字段注释、日志归位与格式
6. 用户要求建 todo：TodoWrite 建 7 条（P0-1/P0-2/P0-3/P1/P2/验证/复检收口），每条带完成判据，并在对话以表格呈现依赖顺序
7. 用户说"todo写在系统里"：以 merge=true 再刷一次任务列表，确认 7 条全部 PENDING 可见，并说明面板管当下进度、勾除由复检驱动
8. 用户说"写进项目里"：Bash 先确认工作区根目录非 git 仓库（fatal: not a git repository）且无同名待办文件，再 Write 创建《告警去重修复-待办清单.md》，内容为同源 7 条可勾选项加背景与复盘出处
9. 用户追问 isDuplicate 是否已无用：Grep 复核当前文件发现调用已被删除、仅剩方法声明，据此把 p0-2 状态翻为 IN_PROGRESS，并从机械层（零引用死代码）与设计层（旧"先读再判"形状与新"判定是占位副产品"矛盾）两层确认整个方法应连同 javadoc 一起删除
10. 第二轮复检（用户"检查一下，给点思路"）：Read 全文（329→306 行）+ Grep 校验，确认 isDuplicate 调用与方法体已整体清除、import Async 已删；但 else 分支同时兜住 last==null 与"已过期"两种情况，replace 仍无条件执行 → P0-1 未修掉；残留检查删除后 P0-3 由"被遮掩"升级为真洞；全文件 0 处 log.debug → P0-2 后半（跳过日志归位）未完成。据此把 p0-1、p0-2 保持/置为 IN_PROGRESS，并以"三岔口决策表"（①last==null 什么都不做直接放行 ②未过期跳过+日志 ③已过期才比较替换且必须消费返回值）替代用户当前的 if/else 二分支心智模型作为下一步方向；P1 提示为"占位成功到落库结束用 try/catch 收口，catch 里只归还值仍是自己那份的条目再重抛"，自检问法是"从占位成功往下数哪条路径离开方法而没归还"。全程只给方向与判定表，不给实现代码。

## Related files
- /d:/code/remote-monitor/告警去重修复-待办清单.md
- /anwei-fire-remote-monitor/module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/alarm/AlarmRecordService.java

## Notes
- 断言 JDK API 运行时行为时用一行 jshell 实测取代记忆推断，评审结论强度更高；jshell 的 -q/-s/-v 属互斥 feedback 选项，只能给一个
- 本机 Bash 沙箱可能走 Node fallback 执行，PATH 不可靠，工具一律用绝对路径（D:\Tool\jdk\jdk21\bin\jshell.exe）
- "占位动作的当场结果就是最终判定"：任何事后回读同一状态的二次检查都会把赢家判成重复，这是本次 insert 不可达的根因
- 错误之间会互相遮掩：P0-2 的残留检查恰好接住了 P0-3 未接返回值的问题，修 bug 需按严重度逐层剥，否则一处修复会暴露新漏洞
- 训练类任务的待办不落业务代码仓库：写文件前先确认工作区根目录不在 git 仓库内，避免污染业务提交
- 用户逐步追加存放位置要求（对话表格 → 系统面板 → 项目文件），每轮按最新要求补做即可，不必反问
- 用户手敲的收敛方式是把两分支改成三分支：当"占位返回 null"与"值已过期"被塞进同一个 else 时，两类相反的动作（不做 vs 做替换并看结果）互相污染，此时最有效的提示不是逐条纠错，而是给一张"三种结局×各自动作"的决策表让用户自己重排结构
- "改动未落地"的判定要看语义而非只看语法：replace 挪进 else 表面上是在修 P0-1，实际 else 仍覆盖 last==null，所以 P0-1 状态必须留在进行中而不是转 DONE

## Task overview
已完成两轮评审与待办三处落位（对话表格、系统任务面板、项目根 md）：交付四级缺陷判定（含 jshell 实测佐证的 NPE 结论）、7 条修复待办，并在第二轮确认 isDuplicate 已彻底删除、import 已清理的同时，指出 P0-1 仍未修掉、P0-3 已显形、跳过日志缺失。代码修复仍未完成，用户需按 P0-1→P0-2→P0-3→P1→P2 顺序改完贴回复检，通过后再谈部署与 git 提交；当前版本禁止部署。
