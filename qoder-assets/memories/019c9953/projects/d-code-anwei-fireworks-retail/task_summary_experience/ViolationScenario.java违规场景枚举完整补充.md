---
title: "ViolationScenario.java违规场景枚举完整补充"
usage_scenario:
    - "当需要为ViolationScenario.java枚举类补充新的违规场景时"
    - "当项目引入新的AI识别违规行为并需同步更新核心枚举定义时"
keywords:
    - "ViolationScenario"
    - "枚举"
    - "违规场景"
    - "告警级别"
    - "AlarmLevel"
    - "S01-S11"
    - "AI识别违规行为"
---

## Task description
- Core requirement: 为ViolationScenario.java枚举类补充全部11个违规场景的定义，包括alarmTypeCode、confidence、coolingPeriod、alarmLevel和autoClearTime字段
- Task background: ViolationScenario.java是安威烟花零售项目中用于定义视频AI识别违规行为的核心枚举；AlarmLevel枚举已定义CRITICAL/MAJOR/MINOR/INFO四个级别及其对应中文描述；需根据用户提供的表格数据，将每个场景映射到正确的告警级别，并将时间单位统一转换为秒

## Execution process
1. 读取当前ViolationScenario.java文件内容，确认其结构和已有枚举值
2. 查询AlarmLevel.java枚举，确认各告警级别对应的code值和中文描述
3. 根据用户表格数据，将S01-S11共11个场景逐一映射：
   - S01 吸烟行为 → alarmTypeCode=9, confidence=0.85, coolingPeriod=300s, alarmLevel=MAJOR, autoClearTime=300s
   - S02 烟火监测 → alarmTypeCode=7, confidence=0.90, coolingPeriod=300s, alarmLevel=CRITICAL, autoClearTime=300s
   - S03 超量存储 → alarmTypeCode=3, confidence=0.80, coolingPeriod=1800s, alarmLevel=MAJOR, autoClearTime=300s
   - S04 违规物品存放 → alarmTypeCode=4, confidence=0.85, coolingPeriod=600s, alarmLevel=CRITICAL, autoClearTime=300s
   - S05 阻塞通道堆放 → alarmTypeCode=5, confidence=0.80, coolingPeriod=900s, alarmLevel=MINOR, autoClearTime=300s
   - S06 出入门和逃生门关闭 → alarmTypeCode=6, confidence=0.75, coolingPeriod=1800s, alarmLevel=MAJOR, autoClearTime=300s
   - S07 人员聚集 → alarmTypeCode=1, confidence=0.85, coolingPeriod=600s, alarmLevel=MINOR, autoClearTime=300s
   - S08 超高堆放 → alarmTypeCode=2, confidence=0.80, coolingPeriod=1800s, alarmLevel=MAJOR, autoClearTime=300s
   - S09 店外经营 → alarmTypeCode=8, confidence=0.85, coolingPeriod=600s, alarmLevel=MINOR, autoClearTime=300s
   - S10 店外燃放 → alarmTypeCode=10, confidence=0.90, coolingPeriod=300s, alarmLevel=MINOR, autoClearTime=300s
   - S11 摄像头异常 → alarmTypeCode=11, confidence=0.90, coolingPeriod=300s, alarmLevel=MINOR, autoClearTime=300s
4. 执行SearchReplace操作，将完整的11个枚举值写入ViolationScenario.java文件
5. 移除未使用的lombok.Data导入

## Related files
- d:/code/anwei-fireworks-retail/ruoyi-modules/ruoyi-fireworks/src/main/java/org/dromara/fireworks/domain/enums/ViolationScenario.java
- d:/code/anwei-fireworks-retail/ruoyi-modules/ruoyi-fireworks/src/main/java/org/dromara/fireworks/domain/enums/AlarmLevel.java

## Notes
- None

## Task overview
成功为ViolationScenario.java补充了全部11个违规场景的完整枚举定义：完成了从读取现有文件、查询告警级别定义、映射所有字段（alarmTypeCode/confidence/coolingPeriod/alarmLevel/autoClearTime）、执行代码替换到移除冗余导入的全流程；生成的枚举值完全符合用户提供的表格数据和项目规范。
