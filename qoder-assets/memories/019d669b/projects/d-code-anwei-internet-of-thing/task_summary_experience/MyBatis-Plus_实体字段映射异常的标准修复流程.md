---
title: "MyBatis-Plus 实体字段映射异常的标准修复流程"
usage_scenario: []
keywords:
    - "MyBatis-Plus"
    - "Unknown column"
    - "@TableField(exist=false)"
---

## 任务描述
解决铸造井信息查询异常：MyBatis-Plus 因实体类 `Customer.resource` 字段未标注 `@TableField(exist = false)`，导致生成含不存在列 `resource` 的 SQL 而报错。

## 执行过程
```mermaid
graph TD
    A[报错: Unknown column 'resource'] --> B[定位SQL来源：CustomerMapper.java]
    B --> C[搜索Customer.java确认resource字段存在]
    C --> D[分析：字段有@ApiModelProperty但无@TableField(exist=false)]
    D --> E[决策：采用方案一（忽略映射）而非建库表列]
    E --> F[在Customer.java第116-117行resource字段添加@TableField(exist=false)]
```

## 任务总结
成功修复字段映射异常：通过添加 @TableField(exist = false) 显式声明 resource 字段不参与数据库映射，避免 MyBatis-Plus 自动拼入 SQL；该方案优先于建表，因字段注释 value 为空且无业务上下文表明其需持久化。
