---
title: "DaHuaService出入记录方向判定与时间丢弃约束"
usage_scenario:
    - "排查闸机上报成功但云端无数据"
    - "修改人闸车闸出入方向逻辑前确认现有行为"
    - "核对出入记录person_name字段存储值"
keywords:
    - "DaHuaService出入记录"
    - "eventDate未来时间丢弃"
    - "inOutState方向覆盖"
    - "em_gate_device_info匹配"
    - "personId截断11位"
source: "auto"
---

prod 分支 DaHuaService（monkey-monitor/src/main/java/com/monkey/general/modules/third/service/DaHuaService.java，实现 PersonAndCarStrategy 的具体类）人员/车辆出入记录保存存在两条已实测验证的行为约束：

1. 未来时间静默丢弃：savePersonInoutRecord 对 eventDate.after(new Date()) 的记录直接 return 丢弃，但 DeviceRecordController 仍返回 code 0 / "OK"——采集端看上报成功、云端却无数据。
2. 出入方向来源不一致：人闸 inOutState 取设备管理表 em_gate_device_info 的 device_type（按 companyCode + devSno.contains(device_code) 匹配，1=进入/2=外出）；车闸 saveCarInoutRecord 虽先按设备管理设方向，但随后被 setInOutState(recordVo.getStatus()) 无条件覆盖为采集端上报值。该覆盖行同时存在于 prod / fireworks-local / odlTest-fireworks 三个后端分支。
3. personId 会被截断为前 11 位存入 person_name，按完整值查询会误判未入库。
