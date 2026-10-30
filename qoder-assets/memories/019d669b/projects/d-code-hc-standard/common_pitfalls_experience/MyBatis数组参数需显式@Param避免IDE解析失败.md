---
title: "MyBatis数组参数需显式@Param避免IDE解析失败"
usage_scenario: []
keywords:
    - "MyBatis"
    - "@Param"
    - "array"
    - "collection"
---

MyBatis中，当Mapper接口方法参数为单个数组且未加@Param注解时，框架内部默认使用'array'作为参数名供XML引用；但IDE静态分析无法识别该隐式名称，导致XML中collection="array"报'无法解析符号'错误。正确做法是显式添加@Param("xxx")注解，并将XML中collection值同步改为该名称。（来源：IDE报错现象与MyBatis行为）
