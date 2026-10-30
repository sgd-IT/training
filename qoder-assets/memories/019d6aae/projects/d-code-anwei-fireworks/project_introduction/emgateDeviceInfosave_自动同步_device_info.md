---
title: "/em/gateDeviceInfo/save 自动同步 device_info"
usage_scenario: []
keywords:
    - "闸口设备"
    - "device_info同步"
    - "报警链路"
source: "user"
---

项目中 `/em/gateDeviceInfo/save` 接口需支持在保存闸口设备（em_gate_device_info）的同时，自动向 `em_device_info` 表同步插入一条对应记录（含 device_code、device_name、company_code、store_num、storeroom_num、deleteflag=0），确保报警流程能通过 device_code 正确关联并触发语音播报。
