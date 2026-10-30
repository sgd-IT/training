---
title: "海康视频平台URL配置要求"
usage_scenario:
    - "配置application-dev.yml等profile文件时补全hikang.url项"
    - "排查HikVisionUtil初始化失败问题时检查URL完整性"
    - "新成员接入海康API时确认必需配置项"
keywords:
    - "hikang.url"
    - "海康视频"
    - "URL配置"
    - "初始化失败"
---

项目海康视频平台集成需配置`hikang.url`前缀下的完整URL集合，包括：
- `token`: OAuth token获取地址
- `group`: 消费者组ID获取地址
- `peopleUrl`: 人员消息获取地址
- `closeUrl`: 直播流关闭地址
缺失任一URL将导致`HikVisionUtil`初始化失败（`URI is not absolute`异常）。
