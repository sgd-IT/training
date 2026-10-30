---
title: "设备地址口径：space_id 为准、addr 为后端生成的空间快照"
usage_scenario:
    - "改动设备地址或空间关联字段时判断该写哪个字段"
    - "排查设备地址被覆盖成地理编码长地址"
    - "扩展按楼栋楼层统计需关联 eiot_space 时"
keywords:
    - "addr快照"
    - "space_id外键"
    - "initSpaceAddr"
    - "SPACE_NOT_EXISTS"
    - "设备地址来源"
    - "逆地理编码长地址"
    - "FireSpaceRespVO"
    - "tenantName前缀"
source: "auto"
---

eiot_device_info.addr 与 space_id 的职责划分（2026-10-13 落地；同日追加 position 详细位置）：

- `space_id bigint NULL`（关联 eiot_space.id，索引 idx_space_id）是设备地址的**唯一权威来源**；前端设备详情下拉直接绑 spaceId（选项 value=space.id）。
- `eiot_space` 新增 `position varchar(64) NOT NULL DEFAULT ''`（详细位置=区域内具体点位，如：东侧走廊北端/301室），唯一键扩展为 (tenant_id, building, floor, area, position)；position 留空即区域级空间，存量 3 条空间均 ''。用途：辅助判断按"同楼栋+楼层+区域、详细位置不同"展示设备。
- `addr varchar(255)` 为**后端生成的空间快照**：DeviceInfoServiceImpl.initSpaceAddr 按设备租户显式查 FireSpaceDO，以「楼栋+楼层+区域+详细位置」直接拼接（无分隔符、filter isNotBlank）重写入 addr；spaceId 查不到抛 SPACE_NOT_EXISTS（2_006_007_201）。改地址只能改 space_id。
- 告警通知模板（deviceAddr/subDeviceAddr）、巡检任务、大屏与设备列表全部仍读 addr 字符串，故 addr **不可废弃**。
- 存量设备的 addr 是地图逆编码长地址（如「广东省东莞市南城街道碧桂园湾区国际TOWER-1」），编辑时仅作 placeholder，重新选择空间后被替换。
- 前端 detail.vue 的 formatSpaceAddr/formatSpaceLabel 与本地 addr 快照已同步含 position；地图 @address-change 已移除（点选地图只写 lon/lat）。
- 易混淆点：协议解析里的「设备地址」（GBT26875/大华帧头通讯地址）与业务字段 addr 完全无关。

## Notes
- initSpaceAddr 按**设备所属租户（产品租户）**查询校验，跨租户 spaceId 抛 SPACE_NOT_EXISTS。
- /list 对系统租户管理员不传 tenantId 返回全部空间；下拉展示串为「租户名 + 空格 + 地址（含详细位置）」；addr 快照不含租户名。
- 测试数据（2026-10-13）：租户 166 空间 4/5/6 = 1号楼3层东区/东侧走廊北端、东侧走廊南端、301室；设备 8→4、12→5、13→6 已绑定并写入 addr 快照（1号楼3层东区东侧走廊北端 式）。
