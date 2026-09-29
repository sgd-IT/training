# SQL 训练台（Phase 2 起，每训练日 30 min）

跑在**你本机已有的 MySQL 8.0.46**上（服务 `mysql`，`127.0.0.1:3306`），
库名 `training`。不碰 `gd_picture` / `xxl_job`。

## 怎么跑

```cmd
cd D:\code\training\sql

run.cmd -Setup               :: 建库建表 + 灌数据（约 20 秒，可反复跑）
run.cmd -i                   :: 交互窗口 —— DDL 在这里手敲
run.cmd s1_bplus_tree.sql    :: 跑某一份练习
```

`-Setup` 就是**重置**：删表重建，回到"只有主键、没有任何二级索引"的干净起点。
每次做完一轮索引实验，想从零再来一遍，就 `-Setup`。

## 为什么是 MySQL 而不是 H2

面试要答的 `EXPLAIN`（`type` / `key` / `rows` / `Extra`）、索引失效、慢查询，
全是 MySQL 方言。H2 的执行计划长得完全不一样，练了迁移不过去。

## 表结构（4 张业务表 + 1 张造数表）

| 表 | 行数 | 说明 |
|---|---|---|
| `customer` | 50,000 | `phone` 高选择性；`city`(8 值) / `level`(5 值) 低选择性 |
| `product` | 2,000 | `category` 10 个品类 |
| `orders` | 200,000 | 主战场。`order_no` 业务唯一但**故意不给索引**；`remark` 80% 为 NULL |
| `order_item` | 400,000 | 每单 2 条明细 |
| `seq` | 1,000,000 | 造数用的数字表 |

**初始状态：所有表只有主键，一个二级索引都没有。**
索引全部由你自己在实验里加 —— 加了什么、`EXPLAIN` 变没变，才是这课的重点。

## 两个刻意的设计

**1. 数据可复现，不用 `RAND()`。**
造数全用 `(n * 质数) % 范围` 这类确定性公式。
你能预测、能重跑、能逐次对比 —— 随机数据做不到。
这和 Java 那边的第 5 条硬规矩（**先预测，再跑**）是同一件事。

**2. `orders` 的客户分布是倾斜的。**

```
80% 的订单  →  压在 id 1 .. 10000 这 1 万个客户身上（人均约 16 单）
20% 的订单  →  散给 id 10001 .. 50000 这 4 万个客户（人均约 1 单）
```

同样一句 `GROUP BY customer_id`，结果集大小差几十倍 —— 方便做对比实验。

## 三个必须记住的操作

```sql
-- 刷新统计信息。InnoDB 的统计是【采样】的，不 ANALYZE 的话
-- EXPLAIN 里的 rows 会离谱，后面所有索引实验都会得出一堆假结论。
ANALYZE TABLE orders;

-- 看表真实物理大小（Part 4 要用）
SELECT TABLE_NAME, TABLE_ROWS, AVG_ROW_LENGTH, DATA_LENGTH, INDEX_LENGTH
FROM information_schema.TABLES WHERE TABLE_SCHEMA = 'training';

-- 看索引（Cardinality = B+Tree 里不同值的估计个数）
SHOW INDEX FROM orders;
```

## 大纲（S1 → S7）

| 编号 | 专题 |
|---|---|
| **S1** | B+Tree 结构与插入 ★ |
| S2 | 索引类型（聚簇 / 二级 / 回表 / 覆盖 / 下推） |
| S3 | 索引失效（最左前缀、函数、隐式转换、前导 `%`、`OR`） |
| S4 | 事务（ACID、隔离级别、MVCC） |
| S5 | 锁（行锁 / 间隙锁 / 死锁） |
| S6 | 执行计划（`EXPLAIN` 各列、`type` 等级、`Extra`） |
| S7 | 优化实战（深分页、慢查询定位、索引设计原则） |

## 备注

`run.cmd` 里写着 `root` / `123456`。本地训练库，图省事；
**真实项目里凭据绝不能进仓库** —— 走配置中心 / 环境变量 / 密钥管理。