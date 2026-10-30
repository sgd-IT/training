---
title: "Jiago设备到铸造井的两级解析机制"
usage_scenario:
    - "排查设备SN未映射铸造井报错"
    - "核对设备到铸造井两级解析实现"
    - "确认equip_code多编码分隔匹配行为"
keywords:
    - "Jiago"
    - "casting well"
    - "resolveCastingWell"
    - "equip_code"
    - "两级解析"
source: "user"
---

Jiago 设备到铸造井的解析采用两级机制（实现在 CastingWellInfoServiceImpl.resolveCastingWell，2026-09-20 起）：第一级匹配 tb_casting_well_info.node_id（兼容 V2 设备）；第二级匹配 tb_casting_well_info.equip_code —— 漏铝设备编码由现场直接手工录入该字段，可录入多个编码（英文/中文逗号分隔、带空格均可），SQL 用 FIND_IN_SET 对 REPLACE 归一化后的列表做元素精确匹配，仅录单个编码同样兼容。第二级已不再查询 em_aluminum_device_info 映射表。两级查询均要求 data_status=1；未匹配返回 null，/lyxl/push 随之返回 500。
