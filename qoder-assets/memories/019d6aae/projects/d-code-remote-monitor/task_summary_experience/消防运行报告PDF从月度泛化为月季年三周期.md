---
title: "消防运行报告PDF从月度泛化为月季年三周期"
usage_scenario:
    - "扩展或排查月报季报年报周期报表功能"
    - "修改报表PDF渲染或AI分析章节口径"
    - "核查统计报表需求完成度与数据源"
keywords:
    - "周期报表泛化"
    - "ReportPeriod"
    - "季报年报"
    - "XF编号"
    - "趋势分桶"
source: "auto"
---

## Task description
- 核心需求：核对"值守中心信息统计报表（查询历史火警/故障/设施异常 + 月度、季度、年度统计报表）"需求的完成度，并在用户确认"可行"后把现有月度报告 PDF 泛化为月/季/年三种周期。
- 任务背景：历史查询由报警记录页/隐患管理页/数据中心覆盖；月报已有 FireMonthlyReportServiceImpl 的"聚合→AI→PDF渲染"三段式实现；季报年报此前零代码（PRD-05 §3.2.4/3.2.5 仅定义）。用户两项决策：月/季/年一次泛化 + 复用现有 8 节换周期（不补 PRD 独有章节）。

## Execution process
1. 需求盘点：Grep 全仓库确认无季报/年报实现（维保计划里的 quarterly 是排期非统计报表）；将 PRD 季报/年报章节逐项映射数据源，结论为大部分现成可算，仅"合规性检查""资源需求"无数据源，"维保年度统计"需补窗口化 SQL。
2. 可行性论证：ReportPdfWriter/PdfReportUtils 为通用绘制原语与"月"零耦合可原样复用；识别出 6 处文案级耦合点（标题、编号 XF-YYYYMM、文件名、章节名"本月概览"、AI 提示词、环比窗口）。
3. ServiceImpl 改造：新增 ReportPeriod 内部类 + parsePeriod/monthlyPeriod/quarterlyPeriod/yearlyPeriod，统一左闭右开窗口并自动算上一周期做环比；buildReportData 签名泛化为接收 ReportPeriod；新增 buildTrend 实现趋势分桶（月按日/季按自然周/年按月，drawBarChart 标签每 3 个标一个天然兼容）；AI 提示词模板化注入周期词（本月/本季/本年度结论），stripSectionLabel 标记跟随切换；renderPdf/renderOverview/renderAlarmAnalysis/renderHazards/renderAnalysisSection 全部参数化，标题编号文件名随周期生成。
4. Controller 新增 periodType(monthly/quarterly/yearly)+period(yyyy-MM / yyyy-Qn / yyyy) 参数，保留旧 month 参数向后兼容；Service 接口方法更名为 exportReportPdf。
5. 前端 data-center/index.vue：header-right 新增周期类型 el-select，按类型切换月份/季度/年度选择器（季度用 value-format yyyy-Qn），默认上一完整期，按钮改"导出报表"，下载文件名带周期标识；Element Plus 版本 2.9.1，el-select 深色样式需 :deep(.el-select__wrapper)。
6. 注释精简（用户中途反馈）：类头大段 Javadoc 压缩至 2~4 行；扫描全文 15 处多行注释，再精简 ReportPeriod 字段注释块（16 行→6 行）、fillAiAnalysis、主方法冗余引用。
7. 验证：JAVA_HOME=D:\Tool\jdk\jdk21 + clean + compile 两轮均 BUILD SUCCESS，日志出现 Recompiling/Compiling 544 source files 确认为真实重编译。

## Related files
- anwei-fire-remote-monitor/module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/firewatch/FireMonthlyReportServiceImpl.java
- anwei-fire-remote-monitor/module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/firewatch/FireMonthlyReportService.java
- anwei-fire-remote-monitor/module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/controller/admin/firewatch/FireMonthlyReportController.java
- anwei-fire-remote-monitor-vue/src/views/eiot/firewatch/data-center/index.vue

## Notes
- GetProblems 返回语言服务未就绪且给出陈旧诊断（调用已改名仍报旧方法 undefined），结论必须以 Maven 编译为准。
- AlertRecordMapper 实际路径在 dal/mysql/ 下而非 dal/mysql/firewatch/ 子目录，按子目录猜测路径读取会 404。
- ripgrep 封装不支持 (?:...) 非捕获组，多行 Javadoc 定位改用单行列出 javadoc 正文行按行号连续性判断。
- Grep 输出显示的前导空格丢失是展示差异，并非文件真实缩进，SearchReplace 前须 Read 取回精确原文。
- 遗留差异：PRD 月报内容表要求的"维保执行情况/巡检完成率/值班合规性"三项当前 8 节本来就没有；getRepairStats 是当前状态快照无时间窗口。

## Task overview
已完成：后端三文件 + 前端一文件的月/季/年周期泛化全部落码，两轮 clean+compile BUILD SUCCESS，旧 month 参数保留兼容；端到端 PDF 实导（选季度/年度核对版面）尚未真机联调，代码未提交 Git（待用户确认）。
