---
title: "MyBatis-Plus selectBatchIds 按主键批量，传业务键静默查空"
usage_scenario:
    - "将循环内逐条查询改写为批量查询回填"
    - "排查批量查询后某列数据全部空白"
    - "选择按主键还是按业务字段批量查询"
keywords:
    - "selectBatchIds 主键语义"
    - "BaseMapperX selectList"
    - "业务键批量查询"
    - "静默查空"
    - "productKey 隐式转换"
source: "auto"
---

Bug class: MyBatis-Plus / BaseMapperX 批量查询方法选错，编译与运行都不报错，数据静默丢失。

Root cause: selectBatchIds(Collection) 语义是「按主键 id 批量查询」，生成 SQL 为 `WHERE id IN (...)`。当传入业务键（如 eiot_product.product_key 这类字符串）时，id 列为 bigint，MySQL 把字符串隐式转换成 0，结果集恒为空——不抛异常、不打日志。在告警分页 fillDeviceTypeBatch 改造中即出现此写法，连锁导致 productMap 恒空 → categoryIds 恒空 → 页面「设备类型」列全部空白，而设备名列正常显示，肉眼极易漏过。

Fix pattern: 按业务字段批量查询改用项目 BaseMapperX 提供的 `selectList(SFunction, Collection)`（等价于 `WHERE 字段 IN (...)`），例如 `productMapper.selectList(ProductDO::getProductKey, productKeys)`；建 Map 时补 merge function `(a, b) -> a` 兜重复脏数据。只有按主键批量才可用 selectBatchIds / selectByIds（项目内新写法统一用 selectByIds，selectBatchIds 已 deprecated）。

Reusable lesson: Don't 把业务键集合传给 selectBatchIds because 主键列的整型隐式转换会静默返回空集，下游 Map 恒空造成字段空白却无任何报错；instead 先确认目标列是否为主键，非主键一律用 selectList(字段, 集合)。适用于本项目所有继承 BaseMapperX 的 Mapper 的批量取值改写；不适用于按主键 ID 批量的场景。验收要点：改完必须检查受影响的那一列（而非整个页面）是否有值，只看接口耗时或设备名正常不足以发现该 bug。
