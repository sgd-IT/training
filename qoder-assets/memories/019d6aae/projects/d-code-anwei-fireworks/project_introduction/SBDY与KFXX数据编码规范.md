---
title: "SBDY与KFXX数据编码规范"
usage_scenario: []
keywords:
    - "SBDY"
    - "KFXX"
    - "store_num"
    - "storeroom_num"
    - "place_no"
    - "编码规范"
source: "user"
---

项目数据模型规范：
- SBDY（联网设备）实体中，`store_num`字段表示15位仓库编码（如`520526920002101`），必须用于生成JSON中的`place_no`；`storeroom_num`字段表示18位库房编码（如`520526920002101001`），仅标识具体库房，不可用于`place_no`。
- KFXX（库房信息）中，同一仓库下的多个库房必须拥有唯一的`storeroom_num`，禁止重复（如仓库`520526920002101`下库房编码应为`...101001`、`...101002`、`...101003`、`...101004`）。
