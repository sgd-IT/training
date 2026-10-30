---
title: "多工具协同诊断：用代码、DB、记忆三重证据链锁定隐患归属根因"
usage_scenario:
    - "诊断任何涉及跨层（前端/App/后端/平台/DB）的归属类问题 | 当单一工具返回结果模糊时，启动交叉验证 | 为复杂问题编写复盘文档时，必须列出三重证据来源"
keywords:
    - "跨层归属诊断"
    - "代码DB双取证"
    - "buildUploadEntity"
    - "information_schema核验"
    - "现状与推断区分"
source: "auto"
---

面对"隐患被挂到错误装置"这类跨层归属问题，单一工具无法给出完整答案，必须构建三层证据链并相互印证：

1. **代码层**：用 Grep / Read 定位推送组装方法（如 BaseHiddenTroubleServiceImpl.buildUploadEntity），确认 riskUnitId 与 hazardCode 确实由 measureId 反查（measure → event → unit → obj）得出，而非 area_id；同时检查同类字段拼装表达式（如 CONCAT('GK', risk_measure_code)）是否存在空值风险。
2. **数据库层**：用 MySQL MCP（只读 mysql_query）直接查业务表坐实数据根源——例如确认某条通用措施在全库仅存在 1 条且唯一归属于错误单元；再查 information_schema 验证"区域→装置单元"映射表根本不存在、base_area 仅有 equipment_id 且为空，从而区分"现状事实"与"推断口径"。
3. **既有结论层**：复用已沉淀的项目架构/踩坑类记忆锚定关键链路（如移动端从排查任务点异常进入的路径），避免重复探索。

配套纪律：任何对外交付的映射表必须逐列标注"是现状还是推断"，未被库表或代码证实的一律标为待企业确认；写库/调接口前先确认执行通道权限（MCP 只读则明确说明受限原因并请用户提供可写通道或 token），不得假定可执行。若任一环节证据缺失或矛盾，结论即不可靠。
