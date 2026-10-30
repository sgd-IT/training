---
title: "拆单循环复用BaseTicketBb实例造成outerId冲突"
usage_scenario:
    - "实现园区报备本地拆单提交逻辑"
    - "排查多条报备outerId重复引发关联失败"
keywords:
    - "循环复用对象实例"
    - "outerId冲突"
    - "拆单本地生成"
    - "insertBaseTicketBb"
    - "票务关联破坏"
source: "auto"
---

Bug class: 园区报备本地拆单时 outerId 冲突。

Root cause: 在循环里复用同一个 BaseTicketBb 对象实例生成多条报备，会让所有报备共用同一个 outerId，违反园区"outerId 即不可变主键"的约束并破坏作业票 prepareId 关联。

Fix pattern: 每条报备在调 insertBaseTicketBb() 之前都必须 new 一个新的 BaseTicketBb 实例，由插入服务内部生成各自的 outerId。

Reusable lesson: Don't 在循环中复用同一 BaseTicketBb 实例批量生成园区报备，因为会造成 outerId 冲突破坏票务关联；instead 每条单独实例化后再插入。适用于园区对接的本地拆单提交逻辑；不适用于单条报备提交或与园区无票务关联的内部流程。
