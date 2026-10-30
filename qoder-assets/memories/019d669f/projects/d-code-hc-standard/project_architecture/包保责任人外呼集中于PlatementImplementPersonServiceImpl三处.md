---
title: "包保责任人外呼集中于PlatementImplementPersonServiceImpl三处"
usage_scenario:
    - "停用或恢复包保责任人的平台推送"
    - "评估包保模块纯本地化的改动范围"
    - "定位包保责任人外呼代码位置"
keywords:
    - "PlatementImplementPersonServiceImpl"
    - "外呼三处"
    - "先推后插落库"
    - "saveOrUpdateBatch"
    - "纯本地化改动范围"
source: "auto"
---

包保责任人（PlatementImplementPerson）与园区平台的对接结构（实测确认）：出站内写操作的外呼只有 3 处，全部集中在 PlatementImplementPersonServiceImpl 内——① insertPlatementImplementPerson 中"先调平台 saveOrUpdateBatch、成功才落本地库"的推送段；② updatePlatementImplementPerson 中对 syncToPlatform(...) 的调用；③ syncToPlatform 私有方法本体（按 outerId 组装批量 upsert）。删除与导出无任何外呼（该实体无删除上报能力），因此"停用推送"只需处理这 3 处即可实现纯本地化，前端与数据库层无需任何配合改动。
