---
title: "大华人车数据接入prod链路兼容性诊断（fireworks-dhsdk-local）"
usage_scenario:
    - "排查大华设备人车数据未入生产库"
    - "核对采集分支与prod接收接口契约"
    - "分析进出记录时间或车牌字段异常"
keywords:
    - "大华对接诊断"
    - "fireworks-dhsdk-local"
    - "人员车辆进出记录"
    - "时间偏移丢数"
    - "devSno车牌NUL"
source: "auto"
---

## Task description
- Core requirement: 判断泗水县五彩烟花爆竹销售有限公司（企业编码 3708313491）人员/车辆数据无法进入 prod 系统的原因，核对本仓库 fireworks-dhsdk-local 分支的大华采集程序与 prod 接收端是否兼容。
- Task background: prod 侧接收入口为 monkey-monitor-api 的 DeviceRecordController（/record/upload/dh/personInout、/record/upload/dh/carInout），业务逻辑在 monkey-monitor 的 DaHuaService，DTO 为 PersonInOutRecordDh / CarInOutRecordDh；用户口中的 "dahuasdk-local" 经确认即本仓库分支 fireworks-dhsdk-local（不存在独立采集仓）。任务性质为只读诊断：不切分支、不改代码、不写库。

## Execution process
1. 用 git show fireworks-dhsdk-local:... 只读读取采集端 EventHandler.java 与 HttpUtils.java，避免切换工作区分支。
2. 比对结论：上报路径与 Content-Type 与 prod Controller 匹配，接口层"能对上"。
3. 人员链路缺陷：采集端将事件时间统一加 8 小时；DaHuaService 对晚于当前时间的人员事件直接丢弃，正常通行记录被静默丢弃而异常报警仍可入库。数据库佐证：设备 CB0415CPAJ356DB 的 3 条记录 created_time 比 event_time 早约 8 小时（future_seconds≈28749）。
4. 车辆链路缺陷：采集端未设置 devSno，导致设备匹配失败；上报的 status 语义是"成功/失败"，但 prod 按进出方向解析，正常上报会被全部判为"进入"；车牌字节数组未去除尾部 NUL（库中 4 条历史记录 plate_bytes=33、plate_chars=31、nul_position=8）。
5. 假成功风险：采集端忽略服务端响应，请求未抛异常即返回成功；prod 丢弃人员数据时接口仍返回成功，"无报错"不等于入库成功。
6. 配置待核实项：fireworks-dhsdk-local 默认 application 配置为 companyCode: 8888 且 host 为空，需现场确认是否有外置配置覆盖。
7. 现状数据：该企业 em_person_in_out_info 为 0 条，em_car_in_out_info 仅 2026-05-11 的 4 条；设备表 5 条 valid=1 记录 access_status 均为 0。

## Related files
- monkey-monitor/src/main/java/com/monkey/general/modules/third/service/DaHuaService.java
- monkey-monitor-api/src/main/java/com/monkey/general/modules/third/qt/DeviceRecordController.java
- src/main/java/com/netsdk/module/handler/EventHandler.java（分支 fireworks-dhsdk-local，只读）
- src/main/java/com/netsdk/util/HttpUtils.java（分支 fireworks-dhsdk-local，只读）

## Notes
- 首次派发的诊断子代理因未先与用户确认分支名（去找独立 dahuasdk 仓库）被用户取消；用户纠正后重新派发才有效，跨仓定位类假设应先向用户确认。
- MCP mysql_query 一次 SQL 语法错误（多语句拼接），改为单条查询后正常。
- 静态分析不能当现场复现结论；人员时间偏移的最终定性仍需设备时间与采集日志验证。
- 采集分支 pom.xml 存在多余文本 ad、PtzControl 关闭窗口会 System.exit(0) 终止采集，属次要观察项。

## Task overview
诊断完成（未修复，按用户要求只读）：接口路径与上传格式兼容，但存在人员时间 +8h 被 prod 时间校验丢弃、车辆缺 devSno、status 语义错位、车牌残留 NUL、双向假成功等确定缺陷；同时明确现场需核实 companyCode/host 配置与设备时间。已向用户输出分优先级结论并索取现场配置与一次刷脸/过车日志。
