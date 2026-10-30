---
title: "修复CarInfo update接口startTime/endTime字段更新失效"
usage_scenario:
    - "MyBatis-Plus updateById 无法更新可能为null的字段时的修复方案"
    - "同类实体更新接口中需强制更新特定字段的实现参考"
    - "排查前端传参为空但数据库未更新的后端逻辑问题"
keywords:
    - "updateById"
    - "UpdateWrapper"
    - "null字段更新"
source: "auto"
---

## 任务描述
修复 /em/car/update 接口中 startTime 和 endTime 字段无法更新的问题。

## 执行过程
```mermaid
graph TD
    A[问题:startTime/endTime修改不生效] --> B[Grep定位CarInfoController]
    B --> C[Read确认updateById直接调用]
    C --> D[Glob+Read CarInfo实体验证字段存在]
    D --> E[对比save方法发现已用UpdateWrapper.set显式赋值]
    E --> F[SearchReplace将updateById改为UpdateWrapper.set]
```

## 任务总结
根因是 MyBatis-Plus updateById 忽略 null 字段；修复方式为改用 UpdateWrapper 显式 .set("start_time", ...) 和 .set("end_time", ...)，确保即使传 null 也强制更新数据库字段，与 save 方法保持一致。

## Notes
- 除UpdateWrapper方案外，还可将实体字段的@TableField(updateStrategy)属性设为FieldStrategy.IGNORED，使null值也能被写入数据库，实现真正的清空语义。
