-- ============================================================
--  SQL 训练台 · 01_seed.sql
--  灌数据（可反复运行，先 TRUNCATE）
--
--  为什么不用 RAND()：造数必须【可复现】。
--  你能预测、能重跑、能对比 —— 随机数据做不到这一点。
-- ============================================================

USE training;

-- MySQL 8 递归 CTE 默认只允许 1000 层，先放开，否则造不满 100 万
SET SESSION cte_max_recursion_depth = 1000000;

SET SESSION unique_checks   = 0;
SET SESSION foreign_key_checks = 0;

-- ------------------------------------------------------------
-- 数字表 0 .. 999999（造数的原材料）
-- ------------------------------------------------------------
TRUNCATE TABLE seq;
INSERT INTO seq (n)
WITH RECURSIVE c(n) AS (
  SELECT 0
  UNION ALL
  SELECT n + 1 FROM c WHERE n < 999999
)
SELECT n FROM c;

-- ------------------------------------------------------------
-- 客户 5 万
-- ------------------------------------------------------------
TRUNCATE TABLE customer;
INSERT INTO customer (name, phone, city, level, created_at)
SELECT
  CONCAT('客户', n),
  CONCAT('138', LPAD(n, 8, '0')),
  ELT(n % 8 + 1, '北京', '上海', '广州', '深圳', '杭州', '成都', '武汉', '西安'),
  n % 5 + 1,
  DATE_ADD('2024-01-01 00:00:00', INTERVAL (n * 37) % 900 DAY)
FROM seq
WHERE n < 50000;

-- ------------------------------------------------------------
-- 商品 2000
-- ------------------------------------------------------------
TRUNCATE TABLE product;
INSERT INTO product (name, category, price, stock)
SELECT
  CONCAT('商品', LPAD(n, 5, '0')),
  ELT(n % 10 + 1, '手机', '电脑', '家电', '服饰', '食品',
                  '图书', '玩具', '美妆', '运动', '汽车'),
  ROUND((n % 4900) / 10 + 9.9, 2),
  n % 500
FROM seq
WHERE n < 2000;

-- ------------------------------------------------------------
-- 订单 20 万（故意做成【倾斜】分布）
--   80% 的订单压在 id 1 .. 10000 这 1 万个客户身上
--   20% 的订单散给 id 10001 .. 50000 这 4 万个客户
--   → 同样一句 GROUP BY customer_id，结果集大小差别巨大，方便做对比实验
-- ------------------------------------------------------------
TRUNCATE TABLE orders;
INSERT INTO orders (order_no, customer_id, status, amount, city, remark, created_at)
SELECT
  CONCAT('NO', LPAD(n, 10, '0')),
  CASE WHEN n % 5 = 0
       THEN (n * 7919) % 40000 + 10001
       ELSE (n * 7919) % 10000 + 1
  END,
  n % 5,
  ROUND((n % 9000) + 0.99, 2),
  ELT((n * 3) % 8 + 1, '北京', '上海', '广州', '深圳', '杭州', '成都', '武汉', '西安'),
  CASE WHEN n % 20 = 0 THEN CONCAT('加急件-', n) ELSE NULL END,
  DATE_ADD('2024-01-01 00:00:00', INTERVAL (n * 13) % 900 DAY)
FROM seq
WHERE n < 200000;

-- ------------------------------------------------------------
-- 订单明细 40 万（每单 2 条）
-- ------------------------------------------------------------
TRUNCATE TABLE order_item;
INSERT INTO order_item (order_id, product_id, qty, price)
SELECT
  n DIV 2 + 1,
  (n * 37) % 2000 + 1,
  n % 3 + 1,
  ROUND((n % 500) + 9.9, 2)
FROM seq
WHERE n < 400000;

SET SESSION unique_checks   = 1;
SET SESSION foreign_key_checks = 1;

-- ★ 关键一步：刷新统计信息
--   InnoDB 的统计是【采样】出来的，不 ANALYZE 的话 EXPLAIN 里的 rows 会离谱，
--   后续所有索引实验都会得出一堆假结论。
ANALYZE TABLE customer;
ANALYZE TABLE product;
ANALYZE TABLE orders;
ANALYZE TABLE order_item;
ANALYZE TABLE seq;

SELECT 'customer'   AS 表名, COUNT(*) AS 行数 FROM customer
UNION ALL SELECT 'product',    COUNT(*) FROM product
UNION ALL SELECT 'orders',     COUNT(*) FROM orders
UNION ALL SELECT 'order_item', COUNT(*) FROM order_item
UNION ALL SELECT 'seq',        COUNT(*) FROM seq;