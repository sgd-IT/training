---
title: "拆单须新建BaseTicketBb实例避免outerId共用"
usage_scenario:
    - "实现园区报备拆单逻辑时避免outerId冲突"
    - "排查多条报备共用同一outerId问题"
keywords:
    - "insertBaseTicketBb"
    - "BaseTicketBb实例复用"
    - "拆单报备"
    - "outerId赋值位置"
source: "auto"
---

拆单场景下每条报备记录必须先 new 一个新的 BaseTicketBb 实例再调 insertBaseTicketBb()，复用同一实例会让所有报备共用一个 outerId（详见「BaseTicketBb对象实例复用导致outerId冲突破坏园区票务关联」）。即 base_ticket_bb 的 outerId 由 BaseTicketBbServiceImpl.insertBaseTicketBb() 内部统一赋值生成，本地存储层不为面向园区的引用另生成或替换 ID。
