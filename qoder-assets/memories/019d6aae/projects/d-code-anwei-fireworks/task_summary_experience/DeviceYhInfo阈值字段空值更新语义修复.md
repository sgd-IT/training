---
title: "DeviceYhInfo阈值字段空值更新语义修复"
usage_scenario:
    - "修复MyBatis-Plus updateById无法清空字段的问题"
    - "为支持'清空即置空'语义而调整实体字段更新策略"
keywords:
    - "DeviceYhInfo"
    - "highThreshold"
    - "lowThreshold"
    - "updateStrategy=IGNORED"
    - "空值更新"
source: "auto"
---

## Task description
- Core requirement: 修复DeviceYhInfo实体中highThreshold和lowThreshold字段在前端清空输入后提交仍变回0的问题，实现真正的'清空即置空'语义
- Task background: DeviceYhInfoController.update接口使用updateById全量更新，原实体字段使用FieldStrategy.NOT_NULL策略导致null被跳过，数据库旧值0保留；需改为IGNORED策略使null也能写入

## Execution process
1. DeviceYhInfoController.update: 定位到monkey-monitor-api模块中DeviceYhInfoController的update方法，确认其调用deviceInfoService.updateById(deviceInfo)
2. DeviceYhInfo.java: 确认highThreshold/lowThreshold字段当前为Integer类型并已标注@TableField(updateStrategy = FieldStrategy.NOT_NULL)
3. DeviceYhInfo.java: 将两个字段的updateStrategy从NOT_NULL修改为IGNORED，使null值能显式写入数据库

## Related files
- /monkey-monitor/src/main/java/com/monkey/general/modules/em/entity/DeviceYhInfo.java

## Notes
- None

## Task overview
成功修复DeviceYhInfo阈值字段空值更新问题：将highThreshold/lowThreshold字段的updateStrategy从NOT_NULL改为IGNORED，使前端清空输入后提交的null值能真正写入数据库，实现'清空即置空'语义；该方案安全且不影响其他推送服务（均使用UpdateWrapper显式set）
