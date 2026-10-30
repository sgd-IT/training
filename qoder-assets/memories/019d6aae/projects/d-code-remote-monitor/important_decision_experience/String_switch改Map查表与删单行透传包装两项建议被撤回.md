---
title: "String switch改Map查表与删单行透传包装两项建议被撤回"
usage_scenario:
    - "代码评审中判断String switch是否应改为Map查表"
    - "决定是否删除仅透传的单行包装函数"
keywords:
    - "String switch"
    - "哈希跳转表"
    - "薄包装函数"
    - "净减行数"
    - "过度简化反向优化"
source: "auto"
---

Conclusion: 在消防远程值守项目的 ponytail-review 中，两条"简化"建议经复核后主动撤回——AlarmSemantics.java 的四个 String/int switch 方法不改为 static Map 查表；monitoring-center/index.vue 中仅透传 popupRef.value?.close() 的 closePopup() 薄包装不删除。
Rationale (trade-off): Java 的 String switch 编译为哈希跳转表，无装箱与哈希计算开销，改 Map.of() 反而引入初始化成本；单行薄包装在 6 个调用点提供语义命名与单一改动收口点，删除后调用点变长且收益为零。
Rejected alternatives: switch→Map 可减约 40 行、删 wrapper 可减 1 行，两者在纯行数口径下都算"改进"，但可读性与性能维度上是负优化。
Applicable / expiry conditions: 适用于以"净减行数"为目标的极简主义评审中对单行封装与 switch 语句的取舍判断；当 switch 分支需运行时动态增删、或包装函数开始承载额外逻辑时须重新评估。
