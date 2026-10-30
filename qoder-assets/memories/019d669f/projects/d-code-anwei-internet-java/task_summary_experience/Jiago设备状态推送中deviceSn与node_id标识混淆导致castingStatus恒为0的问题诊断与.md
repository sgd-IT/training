---
title: "Jiago设备状态推送中deviceSn与node_id标识混淆导致castingStatus恒为0的问题诊断与修复方案"
usage_scenario:
    - "When diagnosing why castingStatus returns 0 despite field confirmation of casting"
    - "When integrating third-party equipment with node-based identifiers"
    - "When debugging discrepancies between large-screen display and API status values"
keywords:
    - "Jiago"
    - "castingStatus"
    - "node_id"
    - "deviceSn"
    - "getByNodeId"
source: "user"
---

## Goal
诊断并修复嘉果漏铝监测 /lyxl/push 接口 castingStatus 恒为 0 的问题（云南创新合金项目），并打通音柱播报配置。

## 重要勘误（2026-09-16 二次核实）
前期曾误连贵州航鹏本地部署库（192.168.250.160/anwei_mon_aluminum_local），"PLC停采一个月"等结论作废。正确生产库为腾讯云CDB：anwei_mon_aluminum（MCP: anwei-mon-aluminum-prod）+ anwei_iot_db（MCP: anwei-iot-db-prod）。

## 正确事实基线
- 云南创新合金（customer_id=380，YNCX），唯一井 id=119「1#铸造单元」，node_id=856d552bfe114a8f9bfef27e17f39e29（UUID）。
- 嘉果终端 0909452694、0909452581 每3秒推 /lyxl/push，两终端同属井119；em_aluminum_device_info 是既有"设备→井"映射机制（device_num→casting_well_id）。
- 信号源健康：ax0083=YNchuangxin_DB250.DBX6.2 与 ax0047=YNchuangxin_DB250.DBX8.1 实时数据0分钟延迟。

## 实施状态（2026-09-16 代码已完成并编译通过，8文件）
1. CastingWellInfoService.resolveCastingWell() 两级解析（先node_id兼容V2，再查映射表），替换 controller 3处+videoEventUp。
2. 抽取 getCastingStatus()（ax0047+冷却水泵+preSignalDelay，与eventUp报警判定同标准），push 与报警共用。
3. Redis报警去重键 nodeId→井id（双终端共用计数器）。
4. status=1心跳短路；SN未映射返回code=500显式暴露。
5. @Valid激活DTO校验（status限0/1、time限14位数字、type限1/2），新建DevicePushExceptionHandler统一转{code:500,msg}文档格式。
6. 上线前置：运维需在云CDB em_aluminum_device_info 插入 0909452694/0909452581→井119 两条映射（MCP只读）。

## dmpk 音柱配置（2026-09-16 核实）
- 正确结构：顶层 dmpk: → 子键 dmpks:（必须复数，对应List<Dmpk>）+ token/apiUrl/deviceCode/isopen；Dmpk元素 castWellId(Long)/repeatCount(Integer)/deviceCode。
- 坑：dev.yml 原把列表key写成 dmpk（单数），SnakeYAML实测 dmpk.dmpks=null → getDmpks()=null，isopen=false短路掩盖NPE；已修复。用户提供的线上服务器配置（castWellId=119, deviceCode=AFE09C0A, repeatCount=1, isopen=true, token=admin:admin, apiUrl=http://111.230.42.212:8088）实测绑定正确。
- isopen=true 是全局开关，同时激活漏铝音柱（sendIpVoice）与人员报警音柱（MonAlarmCameraServiceImpl，其findFirst().get()对未配置井抛异常被catch打error日志）。
- 音柱完整链：设备SN→映射表→井119→dmpks匹配castWellId=119→音柱AFE09C0A播报，三环缺一不可。

## Reusable lesson
第三方设备编号不能直接当平台节点ID用，必须显式映射表转换；映射查不到时宁可显式报错也不静默返回默认值。诊断跨客户SaaS问题先确认数据库连接指向。Spring relaxed binding 下 yml key 拼写错误（如单复数）静默失效不报错，配置列表类属性务必核对 @ConfigurationProperties 字段名。

## 订正与进展（2026-09-20 云库核实）
- 两条映射已存在（id 65/66），但 **data_status=0（禁用），gmt_modified=2026-09-16T08:35:54Z 后一直为0**；且全库无任何铸造井 node_id=0909452694/0909452581（getByNodeId 一级解析必失败），该终端100%依赖映射表。
- 当前代码 resolveCastingWell 映射查询带 `.eq("data_status",1)` 过滤（CastingWellInfoServiceImpl，已提交）；桌面 monkey-aluminium-api.jar（9/18 17:24 构建）含此过滤。**部署该构建后，data_status=0 的映射将解析失败→ /lyxl/push 返回500、声音漏铝报警断流**。部署前须将 65/66 两条记录 data_status 改为 1。
- 9/19 17:30 前仍有 alarm_type=3 记录入库（node_id=0909452694），说明问题会在这次部署后才暴露。
