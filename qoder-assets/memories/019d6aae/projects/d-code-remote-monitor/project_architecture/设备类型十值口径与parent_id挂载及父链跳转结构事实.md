---
title: "设备类型十值口径与parent_id挂载及父链跳转结构事实"
usage_scenario:
    - "新增或修改设备类型相关前后端字段时"
    - "排查设备告警跳转目标异常或父链断裂"
    - "实现子设备挂载与网关识别逻辑"
keywords:
    - "device_type十值口径"
    - "parentId写入点"
    - "transfer网关父链"
    - "findTransferDeviceId"
    - "node_type拓扑角色"
    - "V20260805_2迁移"
source: "auto"
---

eiot_device_info 的设备类型与父链挂载的结构事实（经读码核实）：

1. device_type 完整取值口径为 10 个（与数据库注释对齐）：transfer(用传设备)/camera(摄像头)/smoke(烟感)/temperature(温感)/temp_humidity(温湿度)/door_magnet(门磁)/gas(燃气)/hydrant(消火栓)/manual(手报)/other(其他)。前端两处录入点均已覆盖全 10 项：新增/编辑弹窗 DeviceInfoForm.vue（可留空 clearable）与详情页 detail.vue 编辑下拉；此前仅 detail.vue 有 6 项、新增表单无该字段导致前端新增设备 device_type 恒为 NULL。

2. parent_id 只有两个写入点，均在后端 DeviceInfoServiceImpl.java：①协议注册链路 deviceInfo.setParentId(registerDevice.getParentId())——UDP/TCP 设备注册报文自带 parentId 时自动挂到网关下；②设备管理批量创建子设备时按传入 parentId 参数设置。因此摄像头 parent_id 指向先注册的 transfer 设备（如展厅远程控制终端），FireWatchAlarmAssistServiceImpl / AlertServiceImpl 的 findTransferDeviceId 沿 parent 链上溯找 transfer 设备决定告警跳转目标；device_type 为 NULL 的设备无法参与该父链定级/跳转逻辑。

3. device_type 是后补的业务列，不在原始建表 DDL 中（_sql/mysql/system.sql 的 CREATE TABLE eiot_device_info 无此列）：原通用 IoT 平台模板（enjoy-iot）的设备分类靠 node_type（int NOT NULL，注释即"设备类型"，语义为拓扑角色 0=网关/1=直连设备/2=子设备）+ product_key + model 表达，不含消防物理品类概念。该列由迁移脚本 _sql/mysql/V20260805_2__camera_alarm_fields.sql（提交 bc54b04，2026-08-11「摄像头报警开放接口与告警配置API增强」）以 ADD COLUMN ... AFTER node_type 新增，同时新增 visible；上线时注释仅列 transfer/camera/smoke/temp_humidity/door_magnet 五个示例值，其余取值属业务扩展未回写注释，故存在注释与代码枚举不一致的历史成因。
