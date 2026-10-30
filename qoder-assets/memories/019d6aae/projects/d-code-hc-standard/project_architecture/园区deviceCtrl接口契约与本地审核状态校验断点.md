---
title: "园区deviceCtrl接口契约与本地审核状态校验断点"
usage_scenario:
    - "补齐装置开停车本地前置校验"
    - "对接园区deviceCtrl接口字段与路径"
    - "排查开停车记录被平台拒绝原因"
keywords:
    - "deviceCtrl分页查询契约"
    - "approvalStatus未落库"
    - "开车停车交替校验"
    - "时间段重合唯一性"
    - "装置单元编码位数"
source: "auto"
---

园区平台装置开停车（deviceCtrl）契约与本地校验缺口：

平台侧接口为 POST /openapi/basic/deviceCtrl/save（新增）、DELETE 与 GET /openapi/basic/deviceCtrl/{id}、POST /openapi/basic/deviceCtrl/page（分页，body 为 current/size/search[{field,value}]，search 支持 field=approvalStatus，0审批中/1审批通过/2审批不通过）。平台三条校验规则：重大危险源须填 deviceCode（12位纯数字）并给 unitLists（单元 code 为19位纯数字），非重大危险源填 deviceName/unitName；按装置-单元维度不得存在时间段重合的审核通过记录；类型为开车时该装置-单元最近一条审核通过记录必须为停车。

本地已对齐项：PlatementDeviceCtrlApi 走 OpenApiConstant.DeviceCtrl.save/detail/page；BaseDeviceCtrl.isDeviceSelectionValid() 做 [0-9]{12} 编号校验；BaseDeviceCtrlService.normalize() 做重大危险源字段互斥；buildBody() 覆盖 ctrlMode/startTime/endTime/files/unitLists/applicant；AbstractPlatementResourceService 提供 add/sync/delete 状态机。

结构性断点：本地 base_device_ctrl 未落库平台的 approvalStatus，因此"开车须接停车""时间段不重合"这两条依赖审核通过状态的校验在本地无法直接判定；若要本地提前拦截，必须先回写平台 approvalStatus 到本地表，或新增前调 page 接口按装置-单元查历史记录比对，否则只能提交后由平台报错。
