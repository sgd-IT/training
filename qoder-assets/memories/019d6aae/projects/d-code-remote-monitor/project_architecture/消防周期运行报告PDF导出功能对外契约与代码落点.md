---
title: "消防周期运行报告PDF导出功能对外契约与代码落点"
usage_scenario:
    - "核对周期报表功能实现或盘点代码落点"
    - "修改报表导出接口参数或前端入口"
    - "确认报表章节构成与AI文案生效前提"
keywords:
    - "monthly-report/export-pdf接口"
    - "periodType周期参数"
    - "FireMonthlyReportController"
    - "data-center导出报表按钮"
    - "ReportPeriod周期解析"
    - "ai.llm.url前置条件"
source: "auto"
---

远程值守周期运行报告（月/季/年）功能的对外契约与代码落点：

- 后端控制器：module-eiot-biz 的 controller/admin/firewatch/FireMonthlyReportController.java，提供 GET /eiot/firewatch/monthly-report/export-pdf，参数 periodType(monthly/quarterly/yearly，空默认 monthly) + period(yyyy-MM / yyyy-Qn / yyyy，空默认上一完整期)；旧 month=yyyy-MM 参数保留向后兼容。
- 后端服务：service/firewatch/FireMonthlyReportServiceImpl.java，接口方法为 exportReportPdf，内部按"数据聚合 → AI 分析生成 → PDF 渲染"三段组织，渲染由同文件内的静态类 ReportPdfWriter 承担；周期窗口解析由内部类 ReportPeriod + parsePeriod/monthlyPeriod/quarterlyPeriod/yearlyPeriod 负责（左闭右开，自动算上一周期做环比）。
- 数据结构：controller/admin/firewatch/vo/MonthlyReportDataVO.java。
- 共享渲染工具：service/firewatch/PdfReportUtils.java（与报警记录导出 PDF 共用），drawBarChart 标签数量自适应、每 3 个标一个。
- 前端入口：anwei-fire-remote-monitor-vue 的 src/views/eiot/firewatch/data-center/index.vue，"导出报表"按钮 + 周期类型下拉 + 按类型切换的月份/季度/年度选择器。
- 报告章节共 8 节，文案随周期注入（本月/本季/本年概览、下月/下季/来年工作计划）：概览、告警事件分析（类型占比/趋势/高峰时段/高发单位）、设备运行状态、重点告警明细、隐患与整改、问题与建议（AI 生成）、下期工作计划（AI 生成）、附件签署区；趋势分桶为月按日、季按自然周、年按月。
- 编号规则：XF-YYYYMM / XF-YYYYQn / XF-YYYY。
- 历史基线：基础版随提交 e3297e7 入库；前端入口随 b123a17 入库；AI 增强与作业票式全网格版面属后续改动；月/季/年周期泛化为再后续改动。
- 运行时前提：AI 章节文案生效要求配置项 ai.llm.url 可达，不可用时对应章节自动回退留白而不阻断导出。

## Notes
- 上轮记录的"仅按自然月导出（month=yyyy-MM）""导出月报按钮"表述已过时：2026-10 实测该功能已泛化为月/季/年三周期，接口新增 periodType/period，旧 month 参数仍可用。
