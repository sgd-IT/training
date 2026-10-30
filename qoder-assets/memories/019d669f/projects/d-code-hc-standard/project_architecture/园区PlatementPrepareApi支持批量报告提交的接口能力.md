---
title: "园区PlatementPrepareApi支持批量报告提交的接口能力"
usage_scenario:
    - "When designing local multi-report submission logic for park integration"
    - "When evaluating whether to implement type multi-value vs batch submission"
keywords:
    - "PlatementPrepareApi"
    - "batch submission"
    - "park API"
    - "array input"
source: "auto"
---

The park's PlatementPrepareApi.save() method accepts an array of reports as input, enabling native support for batch submission of multiple park reports in a single API call. This capability underpins the local split + batchNo implementation strategy and eliminates the need for derived outerId or mapping tables.
