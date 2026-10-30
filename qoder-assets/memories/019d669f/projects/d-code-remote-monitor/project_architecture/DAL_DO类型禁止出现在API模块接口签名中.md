---
title: "DAL DO类型禁止出现在API模块接口签名中"
usage_scenario:
    - "When designing or modifying api module interfaces that consume biz/dal data objects"
    - "When debugging unresolved type compilation errors at module boundaries"
keywords:
    - "模块依赖"
    - "API接口"
    - "DAL DO"
    - "编译错误"
    - "AlertRecordDO"
source: "auto"
---

dal DO types (e.g., AlertRecordDO) cannot appear in api module interface signatures because of strict module dependency constraints; attempting to do so causes unresolved type compilation errors. This constraint was verified by tracing the DeviceApi interface signature mismatch and observing the 4 compilation failures.
