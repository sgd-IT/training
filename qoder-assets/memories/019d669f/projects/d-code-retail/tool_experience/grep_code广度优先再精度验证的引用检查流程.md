---
title: "grep_code广度优先再精度验证的引用检查流程"
usage_scenario:
    - "当验证一个文件是否安全删除前"
    - "当确认一个配置或工具模块未被使用再进行重构时"
keywords:
    - "grep_code"
    - "reference verification"
    - "safe refactoring"
    - "broad-first-then-narrow"
source: "auto"
---

## Usage Scenario
Description: When planning to delete or modify a file that may be imported or referenced elsewhere in the project, to avoid breaking unknown dependencies
Examples: Before deleting a legacy utils file; before refactoring a shared configuration object

## Usage Method
grep_code: First use grep to search for imports or usages of the target symbol/file across the entire project; only proceed with deletion/modification if zero matches are found

## Notes
- Deleting or modifying a file without first confirming zero references causes silent runtime failures or build breaks; must always grep first to verify no usage
