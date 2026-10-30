---
title: "贵州平台place_no字段优先库房编码修复"
usage_scenario: []
keywords:
    - "place_no"
    - "库房编码"
    - "贵州平台"
    - "SetGZUploadInfo"
---

## 任务描述
修复贵州平台数据推送中 `place_no` 字段赋值逻辑错误，确保 SBDY（设备）和 ZBBJ（报警）数据严格遵循规范：优先使用库房编码（18位），无库房时降级使用仓库编码（15位）。

## 执行过程
```mermaid
graph TD
    A[需求:修复place_no推送错误] --> B[定位SetGZUploadInfo.java中getDeviceInfoMap方法]
    B --> C[发现203行硬编码d.getStoreNum()]
    C --> D[查阅规范V1.3确认11.1.1/11.4.1等章节统一要求]
    D --> E[确认DeviceYhInfo/AlramInfo实体含getStoreroomNum方法]
    E --> F[检查StringUtils已导入]
    F --> G[修复getDeviceInfoMap：三元判断优先库房编码]
    G --> H[定位getTargetAlarmMap方法]
    H --> I[发现440行同样硬编码a.getStoreNum()]
    I --> J[确认device关联对象已预置正确storeroomNum]
    J --> K[修复getTargetAlarmMap：改用device.getStoreroomNum()]
    K --> L[编译验证通过]
```

## 任务总结
成功修复两处 `place_no` 赋值缺陷：SBDY 使用 `d.getStoreroomNum()` 优先取库房编码；ZBBJ 改用关联的 `device.getStoreroomNum()`（而非 `a.getStoreroomNum()`，因 AlramInfo 本身无该字段）；彻底解决因场所编码不匹配导致的 CKXX/SBDY/ZBDY/ZBSS/ZBBJ 全链路校验失败问题。
