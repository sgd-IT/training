---
title: "批量补推视频需手动设置filePath字段"
usage_scenario:
    - "批量补推视频全部失败且报错filePath不能为空"
    - "调用saveOrUpdateVideo接口前BaseWorkVideo对象filePath未初始化"
    - "数据库无filePath字段但API校验强制要求该字段"
keywords:
    - "filePath"
    - "videoPath"
    - "批量补推"
    - "PlatementValidator"
---

批量补推视频接口 `rePushVideoBatchToPlatform` 调用 `saveOrUpdateVideo` 时，`BaseWorkVideo` 对象的 `filePath` 字段为空，导致校验失败。根本原因是数据库表 `base_work_video` 不含 `filePath` 字段，该字段为 Java 对象中用于 API 上报的临时字段，需在推送前显式赋值。修复方式：在 `ruoyi-system/src/main/java/com/ruoyi/base/service/impl/BaseWorkSpecialServiceImpl.java` 的 `rePushVideoBatchToPlatform` 方法中，对每个查出的 `video` 对象执行 `video.setFilePath(video.getVideoPath())`（同时建议补充 `video.setType("2")` 防止 type 为空）。此赋值是调用 `PlatementTicketNewApi.saveOrUpdateVideo` 前的必要前置动作。（来源：代码阅读与校验逻辑分析）
