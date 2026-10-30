---
title: "Jiago设备音频报警逻辑改造与防抖集成"
usage_scenario:
    - "改造Jiago设备音频报警逻辑以支持启停信号控制"
    - "为纯音频报警场景剥离摄像头依赖并复用现有防抖机制"
keywords:
    - "Jiago音频报警"
    - "启停信号"
    - "audioEventUp"
    - "Redis防抖"
---

## Task description
- Core requirement: 改造Jiago设备音频报警逻辑，使其基于启停信号判断是否触发报警，并复用Redis防抖机制
- Task background: JiagoController.java原videoFileUpload接口功能与实际不符；eventUp方法直接调用videoEventUp，但后者包含不适用的摄像头关联逻辑；需剥离摄像头依赖并确保只在机器运行时报警

## Execution process
1. JiagoController.java: 修复getSignal接口——修正返回类型为ResponseDataVo、注入DeviceRealtimeService、替换object.isEmpty为ObjectUtils.isEmpty、更新URL和注解、移除无用参数和import
2. JiagoController.java: 抽取私有方法getSignalByNodeId(String nodeId)，返回Integer信号值，并让getSignal和eventUp都复用该方法
3. JiagoController.java: eventUp中增加信号判断，停机状态直接跳过，避免无效报警
4. AluminumLeakageAlarmRecordServiceImpl.java: 新增audioEventUp方法，复用videoEventUp的防抖和推送逻辑，但移除了摄像头依赖
5. AluminumLeakageAlarmRecordServiceImpl.java: 在audioEventUp中，当alarmTimes <= 1或count >= alarmTimes时，在调用pushAlarmRecord后，追加调用alarmVoiceService.broadcastAlarm(castingWellInfo.getCastingWellsName() + "监测到漏铝，请尽快处理！")

## Related files
- /src/main/java/com/monkey/general/utils/loulv/JiagoController.java
- /src/main/java/com/monkey/general/modules/mon/service/impl/AluminumLeakageAlarmRecordServiceImpl.java

## Notes
- videoEventUp方法仍被VideoLeakageAlController调用，因此不能直接修改，必须新建audioEventUp以隔离Jiago音频报警逻辑
- Controller层的信号判断是前置拦截，Service层的signal==1判断是兜底，双重保障

## Task overview
成功完成了Jiago设备音频报警逻辑的改造：Controller层信号接口修复并抽取通用逻辑；eventUp增加启停信号前置判断；Service层新增audioEventUp方法，专用于音频报警，去除了摄像头依赖，复用了原有的Redis防抖和报警推送机制，并在报警确认时统一调用AlarmVoiceService.broadcastAlarm进行音柱播报。
