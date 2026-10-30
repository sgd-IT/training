---
title: "批次全部失败时access_status重置为0而非1"
usage_scenario:
    - "实现或重构checkBatchResult逻辑时确定失败回滚条件"
    - "排查数据未重推问题时验证状态回滚是否生效"
    - "代码审查时检查异常路径下的状态一致性"
keywords:
    - "access_status回滚"
    - "全部失败"
    - "checkBatchResult"
source: "user"
---

SD 推送在 checkBatchResult 中，当 errorCount == totalCount（批次全部校验失败）时，必须把记录的 access_status 重置为 0（待推），不能回滚为 1。原因是各推送方法的查询条件都是 eq(access_status, 0)，置 1 后下轮扫描不到，数据会永久丢失。重置为 0 后 sync_reason 保留平台返回的失败原因，下轮定时任务会重新推送。对应方法为 PushingSDDataService.resetAccessStatus。
