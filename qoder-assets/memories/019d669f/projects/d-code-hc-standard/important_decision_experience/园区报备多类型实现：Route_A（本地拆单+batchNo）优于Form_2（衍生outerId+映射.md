---
title: "园区报备多类型实现：Route A（本地拆单+batchNo）优于Form 2（衍生outerId+映射表）的工程权衡决策"
usage_scenario:
    - "When choosing between local split and derived outerId strategies for park multi-type reporting"
    - "When assessing coupling and maintainability impact of mapping tables in park integration"
keywords:
    - "园区报备"
    - "multi-type"
    - "Route A"
    - "Form 2"
    - "engineering trade-off"
source: "auto"
---

Conclusion: Route A (local split into N reports + batchNo) is preferred over Form 2 (one call with derived outerId + type→park ID mapping table) for multi-type park report submission.
Rationale (trade-off): Form 2 increases coupling (requires modifying all pre-fill logic to look up mapping by type), introduces error-proneness (mapping maintenance overhead), and complicates outerId derivation; Route A preserves existing pre-fill and ticket linking logic, avoids mapping tables, and only adds batch creation and list merging.
Rejected alternatives: Form 2 — dropped because its complexity and coupling outweigh its theoretical simplicity of single API call.
Applicable / expiry conditions: holds while park's PlatementPrepareApi.save() supports array input and outerId generation is guaranteed unique per object; re-evaluate if park changes API contract or requires strict one-to-one outerId-to-ticket binding.
