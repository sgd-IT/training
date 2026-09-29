-- ============================================================
--  SQL 训练台 · 00_setup.sql
--  建库 + 建表（先 DROP 再建，可反复运行）
--
--  ⚠️ 故意「只建主键，不加任何二级索引」
--     索引由你自己对照着加 —— 加了什么、EXPLAIN 变没变，才是这课的重点
-- ============================================================

CREATE DATABASE IF NOT EXISTS training
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE training;

DROP TABLE IF EXISTS order_item;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS product;
DROP TABLE IF EXISTS customer;
DROP TABLE IF EXISTS seq;

-- ------------------------------------------------------------
-- 客户表：5 万行
--   phone  高选择性（几乎人人不同）→ 适合建索引
--   city   低选择性（只有 8 个值） → 建了索引优化器可能也不走
--   level  低选择性（只有 5 个值）
-- ------------------------------------------------------------
CREATE TABLE customer (
  id         BIGINT      NOT NULL AUTO_INCREMENT COMMENT '客户ID',
  name       VARCHAR(50) NOT NULL COMMENT '姓名',
  phone      VARCHAR(20) NOT NULL COMMENT '手机号',
  city       VARCHAR(30) NOT NULL COMMENT '所在城市',
  level      TINYINT     NOT NULL COMMENT '会员等级 1-5',
  created_at DATETIME    NOT NULL COMMENT '注册时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='客户表';

-- ------------------------------------------------------------
-- 商品表：2000 行
-- ------------------------------------------------------------
CREATE TABLE product (
  id       BIGINT        NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  name     VARCHAR(80)   NOT NULL COMMENT '商品名',
  category VARCHAR(30)   NOT NULL COMMENT '品类',
  price    DECIMAL(10,2) NOT NULL COMMENT '单价',
  stock    INT           NOT NULL COMMENT '库存',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ------------------------------------------------------------
-- 订单表：20 万行
--   order_no   业务唯一单号，但【没有唯一索引】—— 自己去加，看 EXPLAIN 变化
--   status     只有 5 个值（低选择性）
--   remark     80% 是 NULL（练 NULL 与索引的关系）
--   created_at 跨 900 天
-- ------------------------------------------------------------
CREATE TABLE orders (
  id          BIGINT        NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  order_no    VARCHAR(32)   NOT NULL COMMENT '订单号（业务唯一）',
  customer_id BIGINT        NOT NULL COMMENT '客户ID',
  status      TINYINT       NOT NULL COMMENT '0待付款 1已付款 2已发货 3已完成 4已取消',
  amount      DECIMAL(12,2) NOT NULL COMMENT '订单金额',
  city        VARCHAR(30)   NOT NULL COMMENT '收货城市',
  remark      VARCHAR(200)  NULL     COMMENT '备注（大部分为 NULL）',
  created_at  DATETIME      NOT NULL COMMENT '下单时间',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ------------------------------------------------------------
-- 订单明细表：40 万行
-- ------------------------------------------------------------
CREATE TABLE order_item (
  id         BIGINT        NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  order_id   BIGINT        NOT NULL COMMENT '订单ID',
  product_id BIGINT        NOT NULL COMMENT '商品ID',
  qty        INT           NOT NULL COMMENT '数量',
  price      DECIMAL(10,2) NOT NULL COMMENT '成交单价',
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- ------------------------------------------------------------
-- 造数辅助表（0 .. 999999）
-- ------------------------------------------------------------
CREATE TABLE seq (
  n INT NOT NULL,
  PRIMARY KEY (n)
) ENGINE=InnoDB COMMENT='造数用数字表';

SELECT '00_setup.sql 执行完成' AS 结果,
       (SELECT COUNT(*) FROM information_schema.TABLES
         WHERE TABLE_SCHEMA = 'training') AS 表数量;