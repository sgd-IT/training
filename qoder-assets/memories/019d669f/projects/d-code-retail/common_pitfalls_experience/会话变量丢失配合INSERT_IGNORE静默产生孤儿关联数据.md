---
title: "会话变量丢失配合INSERT IGNORE静默产生孤儿关联数据"
usage_scenario:
    - "执行含会话变量的SQL脚本不报错却未生效"
    - "授权或关联表插入成功但查询无数据"
    - "跨窗口分语句执行SQL脚本排错"
keywords:
    - "session variable lost"
    - "INSERT IGNORE NULL to zero"
    - "self-contained INSERT SELECT"
    - "orphan row validation"
    - "affected rows misleading"
source: "auto"
---

## Bug 类型
SQL 脚本依赖会话变量（SET @var 取号/关联）时，脚本"执行成功"但数据实际挂到了错误关联对象上（如 role_id=0），目标记录查询为空，全程无报错。

## 根因
会话变量作用域仅限当前数据库连接：分窗口、逐条执行或客户端单独执行 SET 时变量丢失为 NULL；INSERT IGNORE 会把 NULL 写入 NOT NULL 数值列的报错静默降级为警告（转为 0），产生孤儿数据。

## 修复模式
取号/关联类 INSERT 改写为自包含写法 INSERT ... SELECT 子查询（JOIN 目标表按业务键如 role_key 匹配），不依赖任何会话变量；已产生的孤儿数据用精确条件（如 WHERE role_id = 0）清理后重放。

## 可复用教训与边界
"affected rows = N"只证明插入了 N 行，不证明挂对了对象——授权/关联数据验证必须 JOIN 校验关联主体存在。适用于任何多行授权、分配、关联表的初始化脚本。
