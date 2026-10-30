---
title: "修复视频上报filePath为空导致批量失败问题"
usage_scenario:
    - "排查视频上报校验失败（filePath为空）类问题的完整诊断路径"
    - "在批量补推类功能中补充缺失字段赋值的标准化修复模式"
    - "维护或扩展作业票/视频同步逻辑时确保filePath一致性"
keywords:
    - "filePath为空"
    - "视频上报校验"
    - "批量补推"
---

## 任务描述
修复视频上报校验失败问题：所有17条记录均因 `filePath` 为空被拒绝，需确保所有调用 `saveOrUpdateVideo` 的路径在推送前正确设置 `filePath = videoPath`。

## 执行过程
```mermaid
graph TD
    A[现象:17条视频上报全失败，报filePath为空] --> B[定位校验入口：PlatementValidator.validateBaseWorkVideoList]
    B --> C[追踪调用链：saveOrUpdateVideo → rePushVideoBatchToPlatform / rePushBatchToPlatform]
    C --> D[查数据库表base_work_video：无filePath列，确认其为纯内存临时字段]
    D --> E[比对正常流程：startWork/endWork/refreshVideo均有video.setFilePath\(h5Url\)]
    E --> F[发现遗漏点1：rePushVideoBatchToPlatform未赋值filePath]
    F --> G[修复1：在循环内添加video.setFilePath\(video.getVideoPath\)\+type兜底]
    E --> H[发现遗漏点2：rePushBatchToPlatform完全未推送视频]
    H --> I[修复2：在操作证推送后新增第6步视频批量推送，统一补全filePath和type]
```

## 任务总结
彻底解决视频上报 `filePath` 为空问题：在 `BaseWorkSpecialServiceImpl` 中，对 `rePushVideoBatchToPlatform`（接口 `/push/workVideo`）和 `rePushBatchToPlatform`（接口 `/push/workSpecial`）两处调用 `saveOrUpdateVideo` 的位置，均补充 `filePath = videoPath` 赋值及 `type = "2"` 兜底逻辑，使所有视频上报路径与开工/完工流程保持一致。
