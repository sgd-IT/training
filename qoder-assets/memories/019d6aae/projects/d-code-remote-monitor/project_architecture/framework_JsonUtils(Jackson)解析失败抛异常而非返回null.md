---
title: "framework JsonUtils(Jackson)解析失败抛异常而非返回null"
usage_scenario:
    - "使用JsonUtils/Jackson做JSON反序列化时"
    - "排查判空守卫不生效的缓存读取代码"
    - "评审手写JSON解析的异常承接方式"
keywords:
    - "JsonUtils.parseObject"
    - "RuntimeException非null"
    - "FAIL_ON_UNKNOWN_PROPERTIES"
    - "同名字段类型冲突"
    - "Jackson解析失败模式"
source: "auto"
---

framework/common 的 JsonUtils.parseObject(String, Class) 在 JSON 解析失败时抛 RuntimeException，不返回 null；objectMapper 只配置了 FAIL_ON_UNKNOWN_PROPERTIES=false（未知字段忽略），但字段类型不匹配仍会抛异常。因此 `if (parsed != null)` 形式的判空守卫永远不会命中，真正的失败路径必须由 try/catch 承接，否则异常被外层 catch 吞掉后表现为"缓存永不命中"且无报错线索。典型触发：把包装对象（顶层放时间戳字符串）与业务 VO 混在同一层再整体反序列化为 VO，VO 内同名字段类型不同（如 LocalDateTime）即抛异常。JsonUtils 已注册 JavaTimeModule，LocalDateTime 本身可正常序列化。
