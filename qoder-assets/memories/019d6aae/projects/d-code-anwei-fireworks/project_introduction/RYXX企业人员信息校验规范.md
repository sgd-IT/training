---
title: "RYXX企业人员信息校验规范"
usage_scenario: []
keywords:
    - "RYXX"
    - "身份证校验"
    - "personName"
    - "强校验"
source: "user"
---

企业人员信息（RYXX）数据接入需实施强校验：`idcard`字段必须为15位或18位有效身份证号码（含X校验码）且非空；`personName`字段必须非空；`person_type`值3对应保管员、4对应守护员；`is_certified`、`deleteFlag`等布尔字段使用整数0/1表示。
