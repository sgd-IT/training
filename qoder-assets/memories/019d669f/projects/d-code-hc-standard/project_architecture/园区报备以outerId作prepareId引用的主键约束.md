---
title: "园区报备以outerId作prepareId引用的主键约束"
usage_scenario:
    - "设计园区报备数据的本地表主键与引用字段"
    - "排查作业票create因prepareId对不上而失败"
keywords:
    - "outerId主键"
    - "prepareId关联"
    - "园区报备数据"
    - "本地ID替代失效"
    - "建票失败"
source: "auto"
---

园区报备接口把所有报备数据的不可变主键定为 outerId，作业票的 prepareId 必须精确指向该值。使用任何其他字段（本地自增 ID、派生 ID）都会打断 prepareId 关联并导致建票失败。落地要求：本地表（如 base_ticket_bb.outerId）必须原样存储并引用园区侧的 outerId，不得为面向园区的引用另生成或替换 ID。适用范围：设计园区报备数据的本地存储结构、排查票务关联失败；不适用于与园区无票务关联的内部流程。
