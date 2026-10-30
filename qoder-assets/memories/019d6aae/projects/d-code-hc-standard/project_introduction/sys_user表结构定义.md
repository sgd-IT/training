---
title: "sys_user表结构定义"
usage_scenario:
    - "设计新表与sys_user关联时确认主键和字段类型"
    - "编写SQL JOIN查询前核对关联字段类型和约束"
    - "进行数据迁移或同步时映射源目标字段"
    - "审查数据库设计是否符合业务实体建模规范"
keywords:
    - "sys_user"
    - "复合主键"
    - "user_id"
    - "phone_number"
    - "用户信息表"
source: "auto"
---

sys_user表是用户信息核心表，主键为复合主键(user_id, phone_number)，user_id为bigint auto_increment类型，phone_number为varchar(64)非空字段；表注释为'用户信息表'，字段含dept_id、user_name、nick_name、email、phone_number、sex、avatar、password、status、del_flag等，支持多客户端接入（appId、secretKey、scope）。
