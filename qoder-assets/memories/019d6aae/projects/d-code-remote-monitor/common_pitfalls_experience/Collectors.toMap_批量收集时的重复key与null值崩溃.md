---
title: "Collectors.toMap 批量收集时的重复key与null值崩溃"
usage_scenario:
    - "将循环内逐条查询改写为批量查询加Map回填"
    - "用Stream收集器构建ID到名称的映射表"
    - "排查分页接口因toMap抛出的500异常"
keywords:
    - "Collectors.toMap"
    - "Duplicate key异常"
    - "toMap null value"
    - "批量查询回填"
    - "distinct去重"
    - "getTenantName"
source: "auto"
---

Bug class: Java Stream `Collectors.toMap` 在"逐条查询改批量收集"场景下抛 IllegalStateException / NullPointerException

Root cause: toMap 的两条默认约束——(1) 不允许重复 key，遇到第二个相同 key 直接抛 `IllegalStateException: Duplicate key`；(2) value 位置不接受 null（内部走 map.merge），一旦取值函数在异常分支 catch 后 return null（本项目 TenantApiImpl.getTenantName 即如此，其链路为 tenantService.getTenant → tenantMapper.selectById，无缓存、查不到或抖动即返回 null），就抛 NPE。分页列表一页内同租户记录高度重复（单租户占九成数据），触发概率接近必然。

Fix pattern: 放弃 toMap，改为先 `.map(取ID).filter(id -> id != null && id > 0).distinct()` 再 `forEach(id -> map.put(id, api.getName(id)))` 手动写入普通 HashMap（HashMap 允许 null value）；若必须用 toMap，则同时提供 merge function `(a, b) -> a` 并保证 value 非空。按主键建 Map（如 selectByIds 结果转 Map）时 key 天然唯一可直用 toMap；按业务字段（productKey 等）建 Map 仍应加 merge function 兜脏数据。

Reusable lesson: Don't 把可能重复的 ID 流或可能返回 null 的取值函数直接塞进 Collectors.toMap，because 线上第一页数据就会 500；instead 先 distinct + filter 再用 HashMap 手动 put。适用于把循环内逐条远程/查库调用改写为"收集 ID → 批量查 → Map 回填"的所有改造；不适用于 key 已由数据库唯一约束保证且取值必定非空的场景。另注意：建好 Map 后必须替换循环内的原调用点，否则批量化不生效（本次即出现 Map 建了但循环仍逐条调用）。

## Notes
- 【后续轮次验证】本条 Fix pattern 与"必须替换循环内调用点"的提醒均已被证实并被用户采纳：FireAlarmOrderServiceImpl 中租户名最终改为 recordList.stream().map(getTenantId).filter(id -> id != null && id > 0).distinct().forEach(id -> tenantNameMap.put(...)) 的手动 HashMap 写法；分页循环内的逐条 fillDeviceTypeBatch(singletonList) 调用已删除、改为循环外统一调一次；遗留的过期注释（方法参数已无 record 但注释仍写"从 record 中获取设备ID"）属同类改造需一并清理的项。
