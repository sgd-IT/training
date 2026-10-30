---
title: "前端 Jackson LocalDate 序列化兼容方案"
usage_scenario:
    - "前端开发人员处理 Java LocalDate 后端返回值时复用该格式化函数"
    - "排查日期显示异常问题时参考该兼容方案"
keywords:
    - "LocalDate"
    - "Jackson"
    - "formatPlanDate"
    - "日期格式化"
source: "auto"
---

前端使用 formatPlanDate 函数处理 Jackson 序列化的 LocalDate 对象，支持 [2027,6,24] 数组格式、{year, monthValue, dayOfMonth} 对象格式及标准字符串格式，统一转为 'YYYY-MM-DD' 字符串。
