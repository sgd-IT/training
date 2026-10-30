---
title: "设备推送place_no误用storeroom_num而非store_num"
usage_scenario: []
keywords:
    - "place_no"
    - "store_num"
    - "storeroom_num"
    - "resolveWarehouseCode"
---

设备推送中 `place_no` 字段应取 `store_num`（15位仓库编码），但实际取了 `storeroom_num`（18位库房编码），根本原因是 `resolveWarehouseCode` 方法未能匹配到 `store_num` 值，导致 `d.setStoreNum()` 未执行，最终回退使用原始 `storeroom_num`。（来源：代码逻辑分析）
