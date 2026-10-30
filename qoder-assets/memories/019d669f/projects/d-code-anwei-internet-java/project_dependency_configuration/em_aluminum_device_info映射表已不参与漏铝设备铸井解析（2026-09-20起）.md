---
title: "em_aluminum_device_info映射表已不参与漏铝设备铸井解析（2026-09-20起）"
usage_scenario:
    - "排查断流时排除旧映射表因素"
    - "确认em_aluminum_device_info现有用途"
    - "复核data_status历史约束是否仍适用"
keywords:
    - "em_aluminum_device_info"
    - "resolveCastingWell"
    - "equip_code"
    - "data_status废弃"
    - "设备映射"
source: "user"
---

自 2026-09-20 起，resolveCastingWell 第二级已改为直接匹配 tb_casting_well_info.equip_code，不再读取 em_aluminum_device_info 的 device_num→casting_well_id 映射。该表本身仍保留（云端→本地下载同步、Web 管理、下载服务等用途），但其 data_status 开关不再影响漏铝设备的铸造井解析；历史排查中“data_status=0 导致断流”的约束不再适用，应改为检查目标铸造井的 equip_code 是否已录入设备编码。
