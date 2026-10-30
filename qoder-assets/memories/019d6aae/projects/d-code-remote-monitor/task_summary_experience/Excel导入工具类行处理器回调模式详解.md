---
title: "Excel导入工具类行处理器回调模式详解"
usage_scenario:
    - "When onboarding new developers to the Excel import utility framework"
    - "When designing new import features requiring custom row validation and business logic"
keywords:
    - "Excel导入"
    - "RowProcessor"
    - "lambda表达式"
    - "模板方法模式"
    - "策略模式"
source: "auto"
---

## Task description
- Core requirement: explain the Java lambda expression used in lines 582–597 of FireDutyScheduleServiceImpl.java for Excel row processing
- Task background: the lambda is passed to ExcelImportUtils.processImport as a RowProcessor<T> callback; understanding its structure, parameter binding, and design rationale enables consistent adoption across modules like patrol tasks and duty schedules

## Execution process
1. Identified the processImport signature: ExcelImportUtils.processImport(file, ImportScheduleDataModel.class, (data, rowNum, result) -> { ... }, "importExcelSchedule")
2. Explained that (data, rowNum, result) corresponds to RowProcessor.process() parameters, injected by the utility's internal for-loop over parsed rows
3. Contrasted lambda syntax with equivalent anonymous inner class implementation to clarify functional interface binding
4. Described full execution flow: Excel file → List<T> parsing → per-row lambda invocation → error/success accumulation in ImportResult
5. Articulated design rationale: separation of fixed infrastructure (file reading, iteration, result aggregation) from variable business logic (row validation, entity creation)

## Related files
- /module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/firewatch/FireDutyScheduleServiceImpl.java

## Notes
- None

## Task overview
Successfully delivered a reusable, self-contained explanation of the ExcelImportUtils RowProcessor callback pattern: clarified parameter binding, execution context, and architectural intent; enables developers to implement consistent import logic across modules without duplicating infrastructure code.
