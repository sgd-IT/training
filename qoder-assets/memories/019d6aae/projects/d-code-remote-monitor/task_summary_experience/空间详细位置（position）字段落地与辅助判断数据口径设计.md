---
title: "空间详细位置（position）字段落地与辅助判断数据口径设计"
usage_scenario:
    - "续建辅助判断真实数据链路"
    - "改造空间管理与设备地址展示"
    - "查改空间表字段与唯一键"
keywords:
    - "详细位置字段"
    - "eiot_space.position"
    - "辅助判断"
    - "alarm-assist接口"
    - "空间唯一键扩展"
source: "auto"
---

为支撑值守大屏「辅助判断」按真实数据展示（口径：同租户+同 building/floor/area、不同详细位置，行=设备），本轮完成空间模型扩展与全链路改造（改动未提交 Git）。

## 已完成
- DDL（经 MCP 执行成功）：eiot_space 新增 position varchar(64) NOT NULL DEFAULT ''（注释：详细位置（区域内具体点位，如：东侧走廊北端））；唯一键 uk_tenant_space 扩展为 (tenant_id,building,floor,area,position)；存量 3 条空间 position=''。
- 后端 6 文件：FireSpaceDO/FireSpaceDTO/FireSpaceRespVO 加 position；FireSpaceServiceImpl 创建时 trim+唯一校验+落库+日志含 position；DeviceInfoServiceImpl.initSpaceAddr 快照拼接追加 position（式样：1号楼3层东区东侧走廊北端，无分隔符）；Controller/Service 注释同步。mvn clean+compile（JDK21，$env:JAVA_HOME='D:\Tool\jdk\jdk21'）551 文件 BUILD SUCCESS。
- 前端 2 文件：space.api.ts 的 SpaceVO 加 position；deviceinfo/detail.vue 的 formatSpaceAddr 含 position、placeholder 与注释同步。eslint 通过。
- 测试数据（租户 166）：空间 4/5/6 = 1号楼3层东区 的 东侧走廊北端/东侧走廊南端/301室；设备 8（烟火算法摄像头）→4、12（消防通道堵塞摄像头）→5、13（离岗监测摄像头）→6，addr 快照同步写入。

## 待办（下一轮）
- 后端 alarm-assist 接口未建（原型契约 GET /eiot/firewatch/alarm-assist/get?alarmId=）；两屏（duty-screen / monitoring-center）辅助判断仍为演示数据，替换锚点注释「辅助判断：先嵌演示数据，后端 alarm-assist 接口就绪后替换为按 alarmId 查询」。
- 查询口径设计：告警→设备→space→同租户+building+floor+area 空间下设备（含自身，自身行呈"报警中"）；设备状态映射（报警中/未报警/可查看/未接入）待与用户确认。
- IDEA 运行的后端服务需用户重启后新链路才生效。
