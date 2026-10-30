---
title: "工单新增设备ID必填校验规范"
usage_scenario:
    - "为工单新增BO类添加校验注解时确定必填字段"
    - "修复因前后端校验不一致导致的数据质量问题"
    - "制定新模块表单字段校验策略时参考"
keywords:
    - "@NotNull校验"
    - "前后端校验对齐"
    - "WbRepairOrderBo"
    - "设备台账就绪"
---

新增维保工单时，deviceId字段后端必须添加@NotNull校验，与前端必填保持一致，依据是WbRepairOrderBo.java第39行注释'设备台账就绪后启用必填校验'且wb_device表已就绪，防止校验被绕过导致无设备工单入库引发派工失败与统计失真。
