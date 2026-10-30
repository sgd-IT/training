---
title: "实时数据表device_code格式不一致问题"
usage_scenario: []
keywords:
    - "device_code"
    - "格式不一致"
    - "em_temperature"
    - "em_humidity"
    - "GZ规范码"
source: "user"
---

项目实时监测功能存在device_code格式不一致问题：em_device_info表中device_code为短码（如'PZRZ-CK-104'），而em_temperature/em_humidity表中device_code实际存储为18位GZ规范码（如'520123920003060001'），导致按短码查询实时数据时全部返回空，触发'无实时数据'警告。
