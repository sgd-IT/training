---
title: "摄像头告警wsTenantId回退必要性与推送链路演进考古"
usage_scenario:
    - "判断告警租户相关判空回退是否冗余"
    - "追溯摄像头告警推送方式的历史演变"
    - "PowerShell 下 git 中文日志乱码处置"
keywords:
    - "reportCameraAlert"
    - "wsTenantId回退死代码"
    - "firewatch-alert-new"
    - "全量广播改定向双发"
    - "git log -S 考古"
source: "auto"
---

## Task description
- Core requirement: 判断 DeviceAlertConfigServiceImpl.reportCameraAlert 中 `Long wsTenantId = recordDO.getTenantId(); if (wsTenantId == null) { wsTenantId = reqVO.getTenantId(); }` 这段回退在 TenantUtils.executeIgnore 块内是否有必要；随后追溯热成像摄像头告警推送大屏的方法位置，以及 sgd 写 notifyAlarmSaved 门面之前（yanglu 时代）是怎么推送的。
- Task background: /report、/receiveCameraAlarm 是 @PermitAll 开放回调，无登录租户上下文，故整段落库+推送包在 executeIgnore 里；推送经 AlarmRealtimeNotifyService.notifyAlarmSaved(payload, alarmTenantId) 统一门面，消息类型 firewatch-alert-new，由 duty-screen / monitoring-center 监听弹窗。

## Execution process
1. 读 AlarmRealtimeNotifyService.java（L46-L75）确认 alarmTenantId 的三个作用：决定是否推给告警租户会话（alarmTenantId != null && != 1）、填充 tenantName 供大屏语音播报、系统租户恒双发；结论：wsTenantId 必须传，且它与 executeIgnore 正交——executeIgnore 只管 MyBatis-Plus 租户拦截，不影响显式传参的局部变量取值。
2. 查 _sql/mysql/system.sql 证实 eiot_device_info.tenant_id（L407）与 eiot_alert_record.tenant_id（L187）均为 `bigint NOT NULL DEFAULT 0` → device 路径下 Java 侧最小是 0L 不可能为 null；device 查不到时 recordDO.tenantId 本就等于 reqVO.tenantId，if 属自我赋值。两条路径都无法使 null→有值，故该回退是死代码，可简化为 `Long wsTenantId = recordDO.getTenantId();`。
3. 用 git 考古定位来源：`git log -S "wsTenantId == null" --format="%h %ad %an %s" --date=short` 显示回退由 yanglu 在 2f8baea（2026-08-19「报警类型分类与语音播报支持」）引入；sgd 在 b38012e（2026-09-15「设备告警统一落库 eiot_alert_record 并接入实时推送门面」）沿用并在本地工作区删除（未提交）。
4. `git show 2f8baea -- <DeviceAlertConfigServiceImpl>` 还原 yanglu 版推送：手工拼 HashMap 后调 `webSocketSenderApi.send(UserTypeEnum.ADMIN.getValue(), "firewatch-alert-new", wsPayload)` —— 不带 userId、不做租户过滤，即全量广播给所有 ADMIN 会话；wsTenantId 当时只用于 tenantApi.getTenantName 查名字塞 payload，不参与选目标；且 payload 里 `level` 写死 "2"（落库已改枚举变量，热成像真实值为 "1"），构成落库/推送不一致，该 bug 在 b38012e 才修掉。
5. 梳理三代演进：初版 bc54b04（Map.of 7 字段 + send 广播，无 tenantName/alarmType/deviceName）→ yanglu 2f8baea（补三字段，仍广播，level 写死）→ sgd b38012e（notifyAlarmSaved 门面，sendToTenant 内 TenantUtils.execute(tenantId, sendObject) 实现按租户定向 + 系统租户双发，level 改变量，补 subDeviceName/alertDetails）。
6. 确认用户工作区当前未提交改动共 5 文件 + 1 新文档：AlertRecordPageReq(+tenantId)、AlertRecordMapper(+eqIfPresent(getTenantId))、DeviceAlertConfigServiceImpl(删回退+清理全限定名)、AlertService(+enrichEventTime 事件时间兜底)、application.yaml(active prod→dev)、docs/视频拉流对接复用指南.md；提醒 application.yaml 的 dev 属本地调试不应入库，且删 if 后上方注释"优先设备租户，回退请求租户"已名不副实。

## Related files
- /module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/devicealert/DeviceAlertConfigServiceImpl.java
- /module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/alarm/AlarmRealtimeNotifyService.java
- /_sql/mysql/system.sql
- /module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/controller/app/cameraalert/CameraAlertOpenController.java

## Notes
- PowerShell 下 git 中文输出乱码：`[Console]::OutputEncoding = UTF8` 设置不生效（输出被 Node fallback 捕获），改用 `git -c i18n.logOutputEncoding=GBK log/show` 才得到可读中文提交信息。
- cmd 命令被工具层拒绝（Terminal Shell Type is PowerShell），跨 shell 拼接重定向不可用。
- 业务库无法经现有 MCP 查询（见另一条坑点记忆），本次改用 DDL 文件 system.sql 作为 tenant_id 约束的替代证据源，结论同样确凿。

## Task overview
已完成（纯分析+考古，除用户自己的本地改动外未新增代码变更）：证明 wsTenantId 回退为死代码并给出简化写法；讲清推送必须传 alarmTenantId 的原因及其与 executeIgnore 的正交关系；还原 yanglu 时代的全量广播实现与 level 写死 bug；产出初版→yanglu→sgd 三代推送方式对比。附带结论：若热成像大屏弹窗异常，与删除该 if 无关。
