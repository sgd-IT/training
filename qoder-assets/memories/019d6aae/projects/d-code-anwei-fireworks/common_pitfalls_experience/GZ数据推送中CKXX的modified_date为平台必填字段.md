---
title: "GZ数据推送中CKXX的modified_date为平台必填字段"
usage_scenario: []
keywords:
    - "GZ平台"
    - "CKXX"
    - "modified_date"
    - "级联失败"
source: "user"
---

GZ省平台要求所有上报数据中CKXX（仓库基础数据）必须包含非空的`modified_date`字段，否则CKXX被拒，进而导致KFXX、SBDY、ZBDY等所有下游数据因关联编码不存在而全部失败。（来源：read_file读取JSON对比 + grep_code分析SetGZUploadInfo.java中getWarehouseMap逻辑）
