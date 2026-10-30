---
title: "ExcelBuilder 是零售模块统一的 Excel 门面"
usage_scenario:
    - "在零售模块中实现 Excel 导入/导出功能时"
    - "重构旧有 Excel 工具类时"
keywords:
    - "ExcelBuilder"
    - "ruoyi-common-excel"
    - "Excel 导入"
    - "Excel 导出"
source: "auto"
---

零售模块中，Excel 的读写操作应统一使用 ruoyi-common-excel 模块提供的 ExcelBuilder 工具类（of/writer/read + doReadAllSync/toResponse），禁止自建 ExcelUtils 等重复工具类。AlarmRecordController.java 中已存在标准用法示例。
