---
title: "PushingGZDataService.java重构：消除企业遍历与仓库编码解析重复"
usage_scenario: []
keywords:
    - "PushingGZDataService"
    - "企业遍历模板"
    - "仓库编码解析"
---

## 任务描述
对PushingGZDataService.java进行大规模重构，核心目标是消除15个saveInfo*方法中重复的企业遍历模板代码和5处仓库编码解析兜底逻辑。

## 任务总结
1. 新增forEachCompany方法，封装企业列表获取、空检查、异常日志等公共逻辑，供15个方法统一调用
2. 新增resolveWarehouseCode方法，统一处理仓库编码的精确匹配、parseInt纯数字、parseWarehouseSeq流水号解析三种兜底策略
3. 逐个重构15个saveInfo*方法，全部替换为forEachCompany调用，并在需要处使用resolveWarehouseCode
4. 额外优化：在saveInfoTargetRealtime中添加反向映射表，将O(n²)查询优化为O(1)
5. 总行数从1677行减少至1368行，减少309行（-18.4%），彻底消除了严重重复代码
