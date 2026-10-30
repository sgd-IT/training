---
title: "fastjson整体解析缓存信封静默丢字段并误填时间戳"
usage_scenario:
    - "实现或评审Redis缓存JSON读写解析代码"
    - "排查缓存命中但业务字段为空或时间判断恒真"
    - "选择fastjson解析方式时"
keywords:
    - "fastjson静默misinterpret"
    - "缓存信封两步解法"
    - "buildAt毫秒时间戳"
    - "双重序列化"
    - "新鲜度判断恒真"
source: "auto"
---

项目用 fastjson 1.2.83（dependencies/pom.xml 管理版本）。经 JDK javac/java + .m2 内 fastjson jar 实测：JSON.parseObject(json, VO.class) 把缓存信封（顶层 data 对象 + buildAt 毫秒数）整体反序列化为业务 VO 时不抛异常而是静默 misinterpret——VO 中与信封顶层不同名的业务字段全部为 null，而同名字段 buildAt 因类型兼容被当毫秒时间戳自动填成当前时间。后果：基于该 VO 的 getBuildAt() 做新鲜度判断恒为 true，缓存过期后仍返回空数据、重建与旧值兜底逻辑永不执行，且无任何报错线索。正确读法分两步：先 JSON.parseObject(json) 得 JSONObject 信封，再 wrapper.getObject("data", VO.class) 取业务数据、wrapper.getLong("buildAt") 取时间戳；写侧对应 wrapper.put("data", vo) 直接放对象而非 JSON 字符串，避免双重序列化。注意与 framework JsonUtils（Jackson，同名字段类型不匹配即抛 RuntimeException）失败模式相反：fastjson 静默出错更难排查。FireDashboardServiceImpl.loadStatisticsCached 改造中确认 DashboardStatisticsRespVO.buildAt（LocalDateTime）为全项目无赋值的死字段，时间戳元信息只放信封顶层。
