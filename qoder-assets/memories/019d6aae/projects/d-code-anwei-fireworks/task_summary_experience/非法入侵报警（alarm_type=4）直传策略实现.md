---
title: "非法入侵报警（alarm_type=4）直传策略实现"
usage_scenario:
    - "处理无视频设备关联的报警类型（如非法入侵、火情等）上传逻辑"
    - "排查报警数据漏传问题时定位‘跳过’或‘降级’类过滤逻辑"
    - "新增报警类型需绕过设备关联校验时的代码改造参考"
keywords:
    - "非法入侵"
    - "alarm_type=4"
    - "视频报警上传"
    - "设备关联"
source: "auto"
---

## 任务描述
修复视频报警数据上传逻辑：对 alarm_type=4（非法入侵）等无法关联视频设备的报警，禁止跳过或降级，改为设备字段置空后正常上传。

## 执行过程
```mermaid
graph TD
    A[需求:alarm_type=4报警必须上传] --> B[定位getVideoAlarmMap中的continue跳过逻辑]
    B --> C[定位PushingGZDataService中的降级映射代码]
    C --> D[SearchReplace删除SetGZUploadInfo.java中device==null→continue]
    D --> E[SearchReplace删除PushingGZDataService.java中fallbackDevice降级块]
    E --> F[验证编译通过，无语法错误]
```

## 任务总结
成功实现非设备关联型报警的直传策略：在 getVideoAlarmMap 中移除 device == null 时的 continue，改为空字符串填充 place_no/video_code/camera_name；同步删除 PushingGZDataService 中的降级映射逻辑，确保 alarm_type=4 报警完整上传且设备字段为空。
