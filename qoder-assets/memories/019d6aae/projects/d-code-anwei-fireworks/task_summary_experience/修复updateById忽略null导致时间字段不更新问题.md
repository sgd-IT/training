---
title: "修复updateById忽略null导致时间字段不更新问题"
usage_scenario:
    - "MyBatis-Plus updateById无法更新空值字段的修复"
    - "统一Controller中save/update数据更新逻辑风格"
    - "排查类似接口字段未生效时检查是否误用updateById"
keywords:
    - "updateById"
    - "UpdateWrapper"
    - "null字段更新"
source: "auto"
---

## 任务描述
修复 `/em/car/update` 接口无法更新 `startTime` 和 `endTime` 字段的问题。

## 执行过程
```mermaid
graph TD
    A[用户反馈字段更新失败] --> B[定位 /em/car/update 对应 Controller]
    B --> C[读取 CarInfoController.java 发现使用 updateById]
    C --> D[对比 save 方法发现已用 UpdateWrapper 显式 set 时间字段]
    D --> E[确认 CarInfo 实体中字段映射正确]
    E --> F[根因：updateById 忽略 null 值导致时间字段不更新]
    F --> G[改用 UpdateWrapper 显式 set 所有需更新字段，保持与 save 一致]
```

## 任务总结
通过将 `updateById` 替换为 `UpdateWrapper.set(...)`，强制更新 `startTime`/`endTime`（即使为 null），解决字段更新失效问题，确保与 `save` 方法行为一致。

## Notes
- 除UpdateWrapper方案外，还可将实体字段的@TableField(updateStrategy)属性设为FieldStrategy.IGNORED，使null值也能被写入数据库，实现真正的清空语义。
