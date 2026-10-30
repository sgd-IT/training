---
title: "WbRepairOrderMapper MapStruct注解误用修复"
usage_scenario:
    - "MyBatis Mapper编译报Unmapped target properties等MapStruct相关错误"
    - "排查Mapper接口意外引入org.mapstruct.Mapper注解导致的构建失败"
    - "统一项目中Mapper注解规范（仅MyBatis @Mapper，禁用MapStruct @Mapper）"
keywords:
    - "MapStruct"
    - "MyBatis"
    - "Mapper注解"
---

## 任务描述
修复WbRepairOrderMapper因误用MapStruct @Mapper注解导致的编译失败问题。

## 执行过程
```mermaid
graph TD
    A[编译错误：Unmapped target properties等] --> B[Glob定位WbRepairOrderMapper.java]
    B --> C[Read查看其import和@Mapper注解]
    C --> D[Grep搜索其他Mapper确认无@Mapper]
    D --> E[Read WbTaskMapper.java验证标准写法]
    E --> F[SearchReplace删除org.mapstruct.Mapper及@Mapper注解]
```

## 任务总结
确认WbRepairOrderMapper错误引入MapStruct的@Mapper注解，与MyBatis-Plus BaseMapperPlus接口冲突；通过比对项目内其他Mapper（如WbTaskMapper）的标准写法，精准删除错误注解及import，消除编译错误，恢复正常构建。
