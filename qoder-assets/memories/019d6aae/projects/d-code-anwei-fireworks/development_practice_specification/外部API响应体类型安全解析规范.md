---
title: "外部API响应体类型安全解析规范"
usage_scenario: []
keywords:
    - "类型检查"
    - "ClassCastException"
    - "API响应解析"
    - "防御性编程"
source: "user"
---

调用外部API并解析响应体时，必须在强转前进行类型检查，禁止直接强制转换（如`(List) res.getBody()`），应先判断`res.getBody() instanceof List`，否则记录错误日志并安全返回，防止ClassCastException。
