---
title: "重复统计SQL不合并与判定逻辑归一落点的取舍"
usage_scenario:
    - "评估是否合并两条近似注解SQL"
    - "选择重复判定逻辑的归一落点"
    - "权衡重构收益与项目风格一致性"
keywords:
    - "动态SQL引入成本"
    - "重复SQL不合并"
    - "接口静态方法归一判定"
    - "薄委托禁用"
    - "收口类暴露判据"
source: "auto"
---

Conclusion: 两处收口决策——① 语义只差一个时间条件的两条统计 SQL（countTodayByOrderStatus / countByOrderStatus）保持两份独立方法，不做参数化合并；② 跨类共用的判定逻辑优先"提升为服务接口静态方法"或"由既有收口类暴露实例方法"，而不是新增一层薄委托方法。

Rationale (trade-off): ①合并的唯一干净解法是 @Select 里引入 <script> 动态 SQL，而该仓库 dal/mysql 全目录零动态 SQL 先例，为省约 6 行引入新写法会破坏一致性、增加后续维护者的认知成本，收益抵不上风格风险。②接口静态方法可直接被 Controller 与 Impl 同时限定名调用，不产生"只转发一行"的包装；系统租户判定本已收口在 FirePropertyTenantSupport，再在别处写 == 1L 字面量等于多开一个真值源。

Rejected alternatives: ①用 todayOnly 布尔参数 + <if> 合并两方法——被否，因引入项目未有的动态 SQL；把"今日"改为传日期区间字符串——被否，因时区算法会与后端 CURDATE() 分叉。②在 Impl 保留私有方法 + Controller 各自内联——被否，散落两份字面量；新增 public 委托方法——被否，属无信息量的薄包装。

Applicable / expiry conditions: ①在该仓库注解 SQL 仍无 <script> 先例、且两方法差异仅一处条件时有效；一旦项目引入 MyBatis 动态 SQL 惯例或差异扩大到两处以上，应重新评估合并。②适用于纯函数式判定（不依赖注入 bean、无副作用）；若判定需要读数据库或上下文状态，则应做成 service 实例方法而非接口 static。
