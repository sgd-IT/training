---
title: "FastExcel 1.1.0 与 Swagger @Schema 注解冲突诊断与修复"
usage_scenario:
    - "Diagnosing Excel import failures with FastExcel 1.1.0"
    - "Resolving annotation conflicts in Java import libraries"
keywords:
    - "FastExcel"
    - "@Schema"
    - "Excel import"
    - "field mapping failure"
    - "annotation conflict"
source: "auto"
---

## Task description
- Core requirement: diagnose and fix the issue where FastExcel 1.1.0 fails to map Excel data to VO fields when Swagger @Schema annotations are present.
- Task background: The project uses FastExcel for Excel import; the ImportScheduleDataModel VO was found to have all fields null during import, despite correct header/data reading. The root cause was traced to a compatibility issue with @Schema annotations.

## Execution process
1. Confirmed the problem: FastExcel 1.1.0 reads headers and data correctly (using Map<Integer, String> listener) but fails to populate VO fields.
2. Identified the root cause: @Schema annotations from Swagger interfere with FastExcel's field mapping mechanism.
3. Applied the solution: removed all @Schema annotations (class and field level) from the Excel import VO, retaining only @ExcelProperty.
4. Verified the fix: After removal, the Excel import functioned correctly, populating all VO fields as expected.
5. Cleaned up diagnostic code: Removed the temporary ReadListener logging added to ExcelUtils.java for diagnosis.

## Related files
- /framework/spring-boot-starter-excel/src/main/java/com/enjoyiot/framework/excel/core/util/ExcelUtils.java

## Notes
- None

## Task overview
Successfully diagnosed and resolved the FastExcel 1.1.0 @Schema annotation conflict: identified the incompatibility as the root cause of null VO fields, applied the fix by removing @Schema annotations, and verified full functionality restoration.
