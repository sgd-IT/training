---
title: "CastingUnitVoService空指针异常修复"
usage_scenario: []
keywords:
    - "空指针"
    - "NPE"
    - "设备信息"
    - "getCastingUnitVo"
---

## 任务描述
修复 /zzdy/getCastingUnitVoList 接口因 CastingUnitVoServiceImpl 中空指针（NPE）导致的'未知异常'。

## 执行过程
```mermaid
graph TD
    A[定位接口入口] --> B[搜索CastingUnitVoController]
    B --> C[读取CastingUnitVoServiceImpl源码]
    C --> D[分析error.log排除环境问题]
    D --> E[识别两处NPE风险点：deviceInfo==null 和 dValue==null]
    E --> F[首次修复：添加deviceInfo非空校验+ldlist非空判断+字符串字面量前置比较]
    F --> G[二次优化：移除null设备仍add进列表的逻辑，改用continue跳过]
```

## 任务总结
成功修复NPE：在 getCastingUnitVo 方法中，对 selectDeviceInfoByDeviceId 返回值做 null 判断并 continue 跳过；对 List 遍历前加 null 安全检查；将所有 "str".equals(obj) 替换为安全比较，彻底消除该接口的未知异常。
