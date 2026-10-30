---
title: "FastExcel导入VO禁用Schema注解的兼容性陷阱"
usage_scenario:
    - "排查FastExcel读取字段全为null"
    - "设计Excel导入VO类的注解方案"
keywords:
    - "FastExcel"
    - "@Schema"
    - "字段映射失败"
    - "注解兼容性"
source: "auto"
---

Bug类：FastExcel 1.1.0 Excel 导入字段映射静默失败。
根因：导入 VO 类或字段上存在 Swagger 的 @Schema 注解（io.swagger.v3.oas.annotations.media.Schema）时，与 FastExcel 1.1.0 字段映射机制冲突，单元格值无法写入对象字段，所有字段为 null（表头与原始数据本身可正常读取）。
修复模式：从 Excel 导入 VO 中移除全部 @Schema 注解（类级与字段级），仅保留 @ExcelProperty；@Schema 只影响文档生成，移除不影响运行时序列化与业务逻辑。
可复用经验：不要在 FastExcel 1.1.0 的导入 VO 上叠加 @Schema 注解，因为会导致字段全 null 且无报错；应在导入 VO 只保留 @ExcelProperty。适用于 FastExcel 1.1.0 + @ExcelProperty 列映射的导入 VO；不适用于其他 FastExcel 版本或 EasyExcel 3.x。完整诊断过程与验证结论以任务总结记忆「移除 Swagger @Schema 注解解决编译错误」为准。
