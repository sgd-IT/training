---
title: "漏铝报警Redis键命名与防抖机制"
usage_scenario:
    - "实现类似防抖报警功能时复用键名规范"
    - "排查Redis缓存未生效或计数异常问题"
    - "新成员理解报警模块状态管理设计"
keywords:
    - "Redis键名"
    - "SEC"
    - "CT"
    - "防抖机制"
    - "漏铝报警"
---

Redis中用于漏铝报警防抖的键名规范：
- `CUSTOMER_CAST_VIDEO_LEAKAGE_SEC`：30秒时间窗口标记键，格式为`customer:cast:video:SEC:{nodeId}`，TTL=30秒；
- `CUSTOMER_CAST_VIDEO_LEAKAGE_CT`：窗口内计数器键，格式为`customer:cast:video:ct:{nodeId}`，值为当前累计检测次数。
两者配合实现'30秒内达到配置次数才触发报警'的防抖逻辑。
