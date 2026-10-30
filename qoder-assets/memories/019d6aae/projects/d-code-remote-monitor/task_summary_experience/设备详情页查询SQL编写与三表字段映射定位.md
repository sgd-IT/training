---
title: "设备详情页查询SQL编写与三表字段映射定位"
usage_scenario:
    - "需要按设备id手写设备详情查询SQL时"
    - "排查设备详情页字段来源与关联表时"
    - "为AI数据服务补设备明细取数逻辑时"
keywords:
    - "设备详情查询SQL"
    - "eiot_device_info字段映射"
    - "eiot_product关联"
    - "eiot_space地址拼接"
    - "device/get接口"
source: "auto"
---

## Task description
- Core requirement: 用户就设备详情页截图（设备 id=5117，烟感报警器）索要一条能查出该页面数据的 SQL。
- Task background: 设备详情页由前端 views/eiot/deviceinfo/detail.vue 渲染，后端入口为 GET /eiot/device/get?id=，需要确认页面各字段落在哪张表、哪些列，以及产品名与空间地址是否需要关联查询。

## Execution process
1. 定位数据源：EiotDeviceInfoDO 映射表 eiot_device_info；建表列清单在 _sql/mysql/system.sql 中核对，确认页面字段对应 name/product_key/serial_no/dn/secret/addr/space_id/lon/lat/firm_version/model/brand_code/api_version/device_type/visible/state/create_time/active_time。
2. 定位接口：DeviceInfoController 的 @GetMapping("/get") 走 deviceInfoService.getDevice(id) 后 BeanUtils.toBean(DeviceInfoRespVO)，即单表直映射，无额外聚合。
3. 关联表确认：产品名来自 ProductDO（表 eiot_product，按 product_key 关联）；空间地址来自 FireSpaceDO（表 eiot_space，按 space_id 关联），其唯一键维度为 tenant_id+building+floor+area+position，故地址串按 building/floor/area/position 四段拼出。
4. 产出 SQL：以 eiot_device_info 为主表，LEFT JOIN eiot_product（p.product_key = d.product_key AND p.deleted = 0）与 LEFT JOIN eiot_space（s.id = d.space_id AND s.deleted = 0），WHERE d.id = 5117 AND d.deleted = 0；productKey 列按 CONCAT(p.name, '(', d.product_key, ')') 复刻页面显示格式，设备地址用 CONCAT_WS('/', ...) 拼接。
5. 补充说明：state 取值口径 0=离线/1=在线/2=未激活/3=禁用；页面"创建时间"展示为毫秒时间戳而库列为 datetime；设备标签在独立关联表，本 SQL 未覆盖。

## Related files
- /anwei-fire-remote-monitor/module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/dal/dataobject/deviceinfo/EiotDeviceInfoDO.java
- /anwei-fire-remote-monitor/_sql/mysql/system.sql
- /anwei-fire-remote-monitor/module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/controller/admin/device/DeviceInfoController.java
- /anwei-fire-remote-monitor/module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/dal/dataobject/firewatch/FireSpaceDO.java

## Notes
- 未实际连库执行验证（本轮 MySQL MCP 未启用），字段正确性靠 DO + 建表脚本 + Controller 三方交叉核对保证；这类"只给 SQL"的任务若需运行，应先按记忆约定列 mcps 确认实例再查。
- 页面出现"请选择空间（楼栋/楼层/区域/详细位置）"占位说明该设备 addr 快照与 space_id 为空，LEFT JOIN 结果空间列为 NULL 属正常，不是 SQL 写错。

## Task overview
已完成：交付一条按页面字段中文化命名的设备详情查询 SQL（主表 eiot_device_info，左连 eiot_product 取产品名、左连 eiot_space 取空间四段地址），并给出字段依据文件、state 取值口径与两处限制说明（设备标签另表、时间戳与 datetime 差异）。SQL 未经数据库实跑验证。
