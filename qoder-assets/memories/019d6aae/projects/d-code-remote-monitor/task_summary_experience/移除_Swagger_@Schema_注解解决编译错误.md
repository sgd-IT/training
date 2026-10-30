---
title: "移除 Swagger @Schema 注解解决编译错误"
usage_scenario:
    - "Swagger 依赖缺失导致 @Schema 编译失败时的快速修复方案"
    - "AI/内部模块 DTO 不需要文档展示时主动剥离 Swagger 注解"
    - "排查并确认注解类是否真正参与运行时逻辑"
keywords:
    - "@Schema"
    - "编译错误"
    - "Swagger 依赖缺失"
source: "auto"
---

## 问题现象
FastExcel 1.1.0 (cn.idev.excel:fastexcel) 读取 Excel 时，使用 `@ExcelProperty` 注解的 VO 对象所有字段均为 null，但诊断日志显示 FastExcel 能正确读取原始表头和数据（用 `Map<Integer, String>` 类型 listener）。

## 根因
Swagger 的 `@Schema` 注解（`io.swagger.v3.oas.annotations.media.Schema`）与 FastExcel 1.1.0 的字段映射机制存在兼容性问题。当 VO 类或字段上存在 `@Schema` 注解时，FastExcel 无法将读取到的单元格值映射到对象字段，导致所有字段为 null。

## 解决方案
从 Excel 导入 VO 类中移除所有 `@Schema` 注解（包括类级别和字段级别），仅保留 `@ExcelProperty` 注解。

## 适用范围
- FastExcel 1.1.0 版本
- 使用 `@ExcelProperty` 进行列映射的导入 VO
- 此问题不影响其他 FastExcel 版本或其他 Excel 库（如 EasyExcel 3.x）

## 注意事项
- `@Schema` 注解仅用于 Swagger/OpenAPI 文档生成，移除后不影响接口数据、序列化、业务逻辑等运行时行为
- 其他正常工作的导入 VO（如 FirePatrolTaskImportVO、FireHazardImportVO）均未使用 `@Schema` 注解
- 建议在 Excel 导入 VO 中避免使用 `@Schema` 注解

## Notes
- 本条标题中的"编译错误"表述不准确：正文记录的实际问题是 FastExcel 1.1.0 导入时字段全为 null 的运行时映射失败，并非编译期错误；以正文的问题现象/根因/解决方案为准。
- 与常见陷阱记忆「FastExcel导入VO禁用Schema注解的兼容性陷阱」内容同源，后者为结构化四要素口径，检索时可互为参照。
