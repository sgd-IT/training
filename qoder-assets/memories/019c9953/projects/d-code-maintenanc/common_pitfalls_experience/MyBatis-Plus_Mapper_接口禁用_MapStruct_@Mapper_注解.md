---
title: "MyBatis-Plus Mapper 接口禁用 MapStruct @Mapper 注解"
usage_scenario:
    - "MyBatis-Plus Mapper 编译报 Unmapped target properties 或 Ambiguous constructors 错误"
    - "Mapper 接口继承 BaseMapperPlus 却意外引入了 org.mapstruct.Mapper 注解"
    - "项目中多个 Mapper 文件需统一规范注解使用"
keywords:
    - "MyBatis-Plus"
    - "MapStruct"
    - "@Mapper"
    - "编译错误"
---

MyBatis-Plus 的 Mapper 接口（继承 BaseMapperPlus）**不能**使用 MapStruct 的 @Mapper 注解（org.mapstruct.Mapper）。一旦添加，MapStruct 注解处理器会尝试为 MyBatis 方法（如 selectById、selectCount、selectMaps）生成映射实现，导致编译报错：'Unmapped target properties'、'Ambiguous constructors'、'abstract class or interface return type' 等。正确做法是完全删除 org.mapstruct.Mapper import 及 @Mapper 注解（来源：Bash/Grep/Read/SearchReplace）
