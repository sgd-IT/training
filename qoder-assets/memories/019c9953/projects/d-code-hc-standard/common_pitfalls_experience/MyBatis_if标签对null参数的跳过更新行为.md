---
title: "MyBatis <if>标签对null参数的跳过更新行为"
usage_scenario:
    - "MyBatis更新操作中字段值为null但数据库未更新"
    - "使用<if>动态SQL时setter设了null值却未生效"
    - "排查sync_status已更新但sync_result仍为NULL的异常状态"
keywords:
    - "MyBatis"
    - "if标签"
    - "null值更新"
    - "动态SQL"
---

MyBatis动态SQL中`<if test="syncResult != null">`在参数为null时会跳过该字段更新，即使已调用setter方法。因此当`json.getStr("data")`返回null时，`updateEntity.setSyncResult(null)`无效，sync_result字段保持原值（NULL）。正确做法是显式判断并赋予默认值，例如：`updateEntity.setSyncResult(data != null ? data : "true")`（来源：Java代码逻辑与MyBatis行为）
