---
title: "小表关联列缺索引经BNL废掉驱动表分页索引顺序"
usage_scenario:
    - "分页列表SQL出现全表扫加临时表filesort"
    - "为关联列评估是否补建二级索引"
    - "按扫描行数估算索引收益严重低估时"
keywords:
    - "Block Nested Loop"
    - "驱动表ORDER BY失效"
    - "eiot_product.product_key"
    - "开关型索引收益"
    - "AlertRecordMapper分页"
    - "idx_alert_cover列序"
    - "handle_status不回表缺口"
source: "auto"
---

反直觉结构事实（本会话实测核实）：JOIN 关联列缺索引时，慢的不是那张小表本身，而是被连带废掉的驱动表分页计划。

机制链：eiot_product.product_key 无索引 → LEFT JOIN eiot_product 退化为 Block Nested Loop → 优化器无法沿用 eiot_alert_record 上 ORDER BY id DESC LIMIT 的 PRIMARY 倒序 early-exit → 变成全表扫 13,705 行 + Using temporary + filesort（含 details TEXT 时还可能排序落盘）。生产实测 2517ms；测试库补 idx_product_key(product_key) 后 EXPLAIN 变为 t:type=index/key=PRIMARY/rows=20、t2:ref/idx_product_key/rows=1，temporary 与 filesort 消失，18.85ms。

两个判据要点：
1. 收益是"开关型"而非"斜坡型"——用"小表只有 25 行 × 每页比较次数"估时间收益会得出"不值得加"的错误结论，实际改变的是这条 SQL 是 O(页) 还是 O(全表)。
2. 验收只看执行计划（type/key/rows/Extra），不看毫秒；同一句 SQL 冷热态可差数十倍。

补充（2026-10-29 测试库实测）：eiot_alert_record 上 idx_alert_cover 的列序为 (alert_time, deleted, alarm_kind, level, read_flg, tier, tenant_id)，首列是 alert_time 而非 tenant_id，因此任何写成 alert_time >= X AND alert_time < Y 的毫秒区间条件都能命中它（type=range、key_len=9）；但该索引不含 handle_status，凡在 WHERE 里按 handle_status 过滤就必须逐行回表。同表的 idx_tenant_alert_time 则以 tenant_id 打头，两者用途不同不可混为一谈。

代码落点：告警分页 AlertRecordMapper.selectPage（LEFT JOIN eiot_product on product_key）、设备分页 EiotDeviceInfoMapper.selectPage（同病，但含 groupId inner join 与 nodeType 副表条件，改造面更大）。修复优先级：先给关联列补索引，仅当补索引后计划仍退化才做两段式分页（先 .select(id) 取 id 页再按 id 关联）。
