---
title: "PushingGZDataService中access_status推送语义标准化"
usage_scenario: []
keywords:
    - "access_status"
    - "增量推送"
    - "PushingGZDataService"
---

## 任务描述
统一规范 PushingGZDataService 中所有 access_status 推送逻辑：主推送方法（人员/车辆/指标报警/视频报警）改为只查询 access_status = 0 并成功后置为 2；其余非主推送路径中的 access_status = 1 过滤条件全部注释掉，避免语义混淆。

## 执行过程
```mermaid
graph TD
    A[需求:统一access_status推送语义] --> B[识别4个主推送方法]
    B --> C[将.eq(..., 1) → .eq(..., 0)并补全.set(..., 2)回写]
    C --> D[全局grep查找所有access_status=1]
    D --> E[对9处非主路径=1过滤全部注释]
    E --> F[二次grep验证无残留]
```

## 任务总结
完成 access_status 推送语义标准化：4个主方法实现「0查→2写」闭环；9处辅助查询的 =1 条件全部注释，添加「// 只上传access_status=0的XXX（增量推送）」说明，消除歧义，保障增量推送一致性。
