---
title: "mobileMe长度校验失败的根因排查"
usage_scenario:
    - "排查下游系统报mobileMe长度不符的生产问题"
    - "验证IMEI字段在数据库中是否符合15-17位规范"
    - "区分mobileMe（IMEI）与手机号字段避免误用"
keywords:
    - "mobileMe"
    - "IMEI长度"
    - "参数验证失败"
---

## 任务描述
排查园区推送失败原因：错误提示 'mobileMe长度必须为15-17位'，但用户声称输入15位。

## 执行过程
```mermaid
graph TD
    A[现象：mobileMe参数验证失败] --> B[搜索mobileMe字段定义]
    B --> C[定位多处实体类与Mapper]
    C --> D[分析SQL取值逻辑：COALESCE\(r.mobile_me, u.mobile_me\)]
    D --> E[查询数据库确认r.mobile_me和u.mobile_me实际值]
    E --> F[发现刘林的mobile_me为14位\(86133306029426\)，不满足15-17位要求]
    F --> G[明确区分mobileMe\(IMEI\)与phone_number\(11位手机号\)的语义差异]
```

## 任务总结
根因是部分用户（如刘林）在sys_user表中的mobile_me字段值为14位IMEI码，违反园区平台15–17位校验规则。需补全正确IMEI或重新采集，不可用手机号替代。
