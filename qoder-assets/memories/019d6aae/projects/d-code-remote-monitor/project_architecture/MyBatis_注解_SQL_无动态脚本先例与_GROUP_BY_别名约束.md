---
title: "MyBatis 注解 SQL 无动态脚本先例与 GROUP BY 别名约束"
usage_scenario:
    - "评估合并两条近似统计 SQL 是否可行"
    - "新增 CASE 派生维度做分组统计"
    - "排查分组计数远小于明细条数"
keywords:
    - "注解SQL无script动态写法"
    - "GROUP BY别名撞真实列"
    - "status_group"
    - "eiot_alert_record order_status"
    - "dal/mysql 目录"
source: "auto"
---

anwei-fire-remote-monitor 的 MyBatis 注解 SQL 存在两条执行期核实的结构约束：

1. 全仓 `module-eiot/module-eiot-biz/.../dal/mysql` 目录下 Grep `<script>` 零命中——所有 @Select/@Update 等注解 SQL 一律是单条静态语句，不存在动态 SQL（<script>/<if>/<choose>）先例。因此"用参数开关切换同一方法的两条条件"这类收口在此仓库不可行，两个语义相近的统计方法只能各自独立定义并各带用途注释；评估重复 SQL 是否合并时，必须先算上引入新写法的风格成本，不能只看可减行数。

2. GROUP BY 的 SELECT 别名不得与表里真实列同名：eiot_alert_record 实际存在 order_status 列（varchar，线上仅少量遗留行有值），CASE 派生维度若也叫 order_status，MySQL 的 GROUP BY 会优先按真实列分组，统计结果只覆盖该列非空的行（实测总数 263 而明细 33454）。现统一别名为 status_group，且 Java 侧读取点必须同步 Map.get("status_group")。凡新增 CASE 虚拟维度分组，先确认表内无同名列。
