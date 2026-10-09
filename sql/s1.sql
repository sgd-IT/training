-- ============================================================
--  SQL-S1 · B+Tree 结构与插入
--  Day 9 (2026-09-28) · 30 min
--
--  跑法 :  run.cmd s1.sql
--  交互 :  run.cmd -i        （DDL 手敲，别复制）
--  重置 :  run.cmd -Setup    （回到"零二级索引"的干净起点）
--
--  ★ 先预测，再跑 —— 把【具体的值】填上再运行
--  ★ Part A 手写 → Part B 拿你写的 SQL 做实验 → Part C 闭卷
-- ============================================================

USE training;


-- ############################################################
-- Part A · 手写（约 12 min）
--   下面 4 题【全是空行】—— SQL 你自己敲
--   卡住就空着，继续下一题，最后一起问
-- ############################################################

-- A-1  orders 一共多少行？
-- 你的 SQL:


-- A-2  按订单号查一条：order_no = 'NO0000123456'
--      只要 order_no / amount / status 三列
-- 你的 SQL:


-- A-3  status = 3 的订单有多少条？
-- 你的 SQL:


-- A-4  订单号 NO0000000123 的客户姓名和手机号
--      ⚠️ 要用两张表：orders + customer
-- 你的 SQL:



-- ############################################################
-- Part B · B+Tree 实验（约 18 min）
--   直接用你 A-2 / A-3 写的那两句
-- ############################################################

-- B-0  一个 B+Tree 节点 = 一页。页多大？
SELECT @@innodb_page_size AS 页大小_字节;


-- B-1  把 A-2 变成执行计划（order_no 上【还没有】索引）
--      预测  type = ______   key = ______   rows = ______
EXPLAIN SELECT order_no, amount, status FROM orders WHERE order_no = 'NO0000123456';


-- B-2  ★ 手敲（在 run.cmd -i 里，别写进本文件）：
--          CREATE INDEX ...
--      给 order_no 建好索引后，回来再跑一遍下面这句
--      预测  type = ______   key = ______   rows = ______
EXPLAIN SELECT order_no, amount, status FROM orders WHERE order_no = 'NO0000123456';


-- B-3  B-2 走了索引，但 MySQL 还得回去拿 amount / status —— 这叫"回表"。
--      为什么这条要回表，而"按主键查一条"就不用？
--      （二级索引的叶子上挂的是什么？聚簇索引的叶子上挂的又是什么？）
-- 答:


-- B-4  ★ 手敲：给 status 建索引，然后跑下面这句
--      预测  type = ______   key = ______   rows = ______
EXPLAIN SELECT * FROM orders WHERE status = 3;


-- B-5  优化器走你刚建的 status 索引了吗？为什么？
--      （status 一共几个不同值？status = 3 要捞多少行？占总行数百分之几？）
-- 答:


-- B-6  索引元数据：Cardinality = B+Tree 里【不同值】的估计个数
SHOW INDEX FROM orders;


-- B-7  三层 B+Tree 能存多少行？用你自己这张表的真实数字算
SELECT TABLE_NAME, TABLE_ROWS, AVG_ROW_LENGTH, DATA_LENGTH, INDEX_LENGTH
FROM information_schema.TABLES
WHERE TABLE_SCHEMA = 'training' AND TABLE_NAME = 'orders';

--      ① 一页放几条 orders 记录     = 16384 / AVG_ROW_LENGTH = ______
--      ② 一个目录项约 22 字节（主键 BIGINT 8 + 页号 4 + 页头开销 ~10）
--         一页放多少个目录项         = 16384 / ______ = ______
--      ③ 三层能存多少行             = ______ × ______ × ① = ______
--      这个数和 TABLE_ROWS（20 万）比，说明什么？
-- 答:



-- ############################################################
-- Part C · 闭卷（不许查，先写在"答:"后面）
-- ############################################################

-- C-1  叶子节点分裂 和 非叶子节点分裂，中间那个 key 分别怎么处理？
-- 答:

-- C-2  为什么 MySQL 用 B+Tree 而不是 B-Tree？
--      （提示：B-Tree 的【非叶子节点也存数据】—— 挤掉了什么？）
-- 答:

-- C-3  为什么推荐【自增】主键？用 UUID 当主键会怎样？
-- 答: