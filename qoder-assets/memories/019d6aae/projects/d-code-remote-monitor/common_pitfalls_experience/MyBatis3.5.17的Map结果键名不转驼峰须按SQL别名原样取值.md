---
title: "MyBatis3.5.17的Map结果键名不转驼峰须按SQL别名原样取值"
usage_scenario:
    - "编写@Select返回List<Map>的统计查询并取值"
    - "排查row.get某列返回null的问题"
    - "验证框架某行为的底层实现细节"
keywords:
    - "MyBatis Map键名"
    - "MapWrapper"
    - "mapUnderscoreToCamelCase"
    - "裸SQL取值"
    - "javap反汇编"
source: "auto"
---

Bug class：@Select 返回 List<Map<String,Object>> 时，误以为配置 mapUnderscoreToCamelCase=true 会把结果 Map 的键转成驼峰，从而用 row.get("alarmDate") 取值 → 拿到 null。
Root cause：本仓库 MyBatis 版本为 3.5.17（dependencies/pom.xml `<mybatis.version>3.5.17</mybatis.version>`）。经 javap 反汇编 org.apache.ibatis.reflection.wrapper.MapWrapper 证实：其 findProperty(String name, boolean useCamelCaseMapping) 实现就是 `return name;`——**完全忽略 useCamelCaseMapping**，对 Map 结果不做下划线→驼峰转换。（该驼峰转换只对 POJO 字段映射生效。）
Fix pattern：Service 里按 SQL 别名**原样**取值，如 `row.get("alarm_date")`、`row.get("tenant_id")`、`row.get("plan_date")`、`row.get("created_count")`；别名带什么就用什么，别自作主张改驼峰。大屏 FireDashboardServiceImpl 生产代码正是这么写的（可作一致性参照）。
Reusable lesson：怀疑框架行为时不要凭通用知识假设，直接定位本地 .m2 里的 jar 用 `javap -p -c -classpath <jar> <FQCN>` 反汇编目标方法裁决。
适用边界：本项目所有 @Select 返回 Map 的裸 SQL 统计查询取值。不适用：resultType 为 DO/VO 对象的映射（那种走驼峰转换）。
