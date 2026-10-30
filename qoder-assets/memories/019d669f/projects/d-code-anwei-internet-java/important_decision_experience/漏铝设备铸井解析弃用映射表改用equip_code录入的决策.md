---
title: "漏铝设备铸井解析弃用映射表改用equip_code录入的决策"
usage_scenario:
    - "排查设备SN未映射铸造井断流"
    - "新现场漏铝设备上线配置"
    - "复核铸井解析机制变更原因"
keywords:
    - "equip_code"
    - "resolveCastingWell"
    - "em_aluminum_device_info"
    - "设备映射"
    - "解析机制变更"
source: "user"
---

## 结论
resolveCastingWell 第二级弃用 em_aluminum_device_info 的 device_num→casting_well_id 映射，改为直接匹配 tb_casting_well_info.equip_code：漏铝设备编码由现场手工录入该字段，支持多个编码用英文/中文逗号分隔（FIND_IN_SET 对归一化后的列表做元素精确匹配）。
## 理由与权衡
映射表受云端→本地 DownloadJob 同步覆盖，data_status 手工修改易被回滚导致断流；equip_code 由现场直接维护、不受同步覆盖。代价：各现场须在目标铸造井录入 equip_code，未录入即 /lyxl/push 返回 500 断流。
## 被否决方案
保留映射表作为兜底（equip_code 查不到再查旧映射）——用户明确选择直接替换删除，以彻底摆脱对该表的依赖。
## 适用条件
2026-09-20 起解析逻辑以此为准；新现场上线或迁移前须确认 equip_code 已录入；如需回退，恢复旧第二级映射查询即可。
