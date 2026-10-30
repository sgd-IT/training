---
title: "用户昵称前端数据源：后端/system/user/profile/get接口中data.nickname字段"
usage_scenario:
    - "在任意页面中显示动态用户昵称时确定数据来源"
    - "排查用户信息显示异常时定位后端接口位置"
    - "新成员初始化用户模块逻辑时参考数据流"
keywords:
    - "API"
    - "userNickname"
    - "profile endpoint"
    - "动态昵称"
source: "auto"
---

用户昵称需从后端 /system/user/profile/get 接口获取，响应体中 data.nickname 字段即为动态用户昵称；禁止硬编码（如"项目管理员"）或从 Pinia store 获取。
