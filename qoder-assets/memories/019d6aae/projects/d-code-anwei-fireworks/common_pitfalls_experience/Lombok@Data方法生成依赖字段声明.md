---
title: "Lombok@Data方法生成依赖字段声明"
usage_scenario: []
keywords:
    - "Lombok"
    - "@Data"
    - "setter生成"
    - "字段声明"
source: "user"
---

Lombok @Data 注解仅对已声明的字段生成 getter/setter 方法。若调用 setXxx() 但实体类中未定义 xxx 字段，则编译报错'无法解析方法'。需先在实体类中添加对应字段，再由 Lombok 自动生成方法。（来源：Java代码分析）
