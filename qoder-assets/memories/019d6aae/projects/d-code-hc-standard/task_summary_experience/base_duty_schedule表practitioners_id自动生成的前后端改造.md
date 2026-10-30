---
title: "base_duty_schedule表practitioners_id自动生成的前后端改造"
usage_scenario:
    - "修改值班排班人员标识生成逻辑"
    - "排查practitioners_id重复或前端仍可输入问题"
keywords:
    - "practitioners_id"
    - "ThreadLocalRandom六位前缀"
    - "手机号拼接"
    - "base_duty_schedule"
    - "移除前端输入项"
source: "auto"
---

## 任务描述
为 base_duty_schedule 表的 practitioners_id 字段实现"随机6位前缀 + 手机号"组合生成逻辑，确保唯一性与 varchar(64) 类型兼容。

## 执行过程
1. 搜索值班模块相关代码，定位 BaseDutyScheduleServiceImpl.java 与 index.vue
2. 后端 insertBaseDutySchedule：手机号非空时用 ThreadLocalRandom 生成 100000~999999 的 6 位数，拼接手机号形成唯一字符串
3. 前端移除 practitionersId 输入框及对应校验规则
4. 验证生成效果（如 '42315613800138000'）

## 相关文件
- BaseDutyScheduleServiceImpl.java（ruoyi-system 值班排班 service 实现）
- 值班排班前端 index.vue

## 概览
成功实现 practitioners_id 自动化生成：后端随机6位前缀+手机号拼接保证唯一，前端同步移除该字段输入项与校验，提升用户体验与数据一致性。通用决策依据见"唯一ID构造决策-随机前缀+业务字段组合"。
