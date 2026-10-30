---
title: "AI数据接口参数可选性与默认值规范"
usage_scenario:
    - "前端或AI服务调用AI数据接口时确定必需参数"
    - "编写API文档或SDK时标注参数是否必填及默认值"
    - "排查400错误时快速核对参数传递完整性"
keywords:
    - "AI接口"
    - "参数可选性"
    - "默认值"
    - "pageNo"
    - "limit"
source: "auto"
---

AI数据查询接口的参数可选性规则如下：
- `/ai/data/device/statistics` 和 `/ai/data/alarm/statistics`：无参数，直接调用
- `/ai/data/device/detail`：`deviceId` 必传
- `/ai/data/alarm/detail`：`orderId` 必传
- `/ai/data/device/page` 和 `/ai/data/alarm/page`：`pageNo` 与 `pageSize` 均可不传，分别默认为 1 和 20
- `/ai/data/alarm/realtime`：`limit` 可不传，默认为 10
