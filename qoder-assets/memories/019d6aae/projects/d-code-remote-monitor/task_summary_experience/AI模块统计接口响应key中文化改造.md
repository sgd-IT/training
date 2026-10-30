---
title: "AI模块统计接口响应key中文化改造"
usage_scenario:
    - "改造AI模块其他统计接口（如故障统计）的响应key为中文"
    - "复用相同模式将英文key替换为带单位的中文key"
    - "排查前端无法识别英文key导致的数据显示异常"
keywords:
    - "AiDataServiceImpl"
    - "响应key中文化"
    - "统计接口"
---

## 任务描述
将 AI 模块设备与告警统计接口的响应 key 由英文改为中文，适配前端展示需求。

## 执行过程
直接定位并修改 AiDataServiceImpl.java 中两个方法：
- getDeviceStatistics()：将 total/online/offline 等 10 个英文 key 替换为对应中文（如 "total" → "设备总数"）
- getAlarmStatistics()：将 todayTotal/handled/pending 等 8 个英文 key 替换为中文（如 "avgResponseTime" → "平均响应时间(秒)"，明确标注单位）

## 任务总结
在 d:/code/remote-monitor/anwei-fire-remote-monitor/module-ai/module-ai-biz/src/main/java/com/enjoyiot/module/ai/service/AiDataServiceImpl.java 文件中，完成 getDeviceStatistics 和 getAlarmStatistics 方法的响应 key 中文化，所有键名均采用业务可读中文，时间类字段明确标注单位（秒），确保前端无需额外映射即可渲染。
