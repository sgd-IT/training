# 正反例（建表 / 索引 / 查询 / 迁移脚本 / EXPLAIN）

示例表名与列名均为占位。**落笔前先按 SKILL.md 第 0 步确认项目既有口径**，然后整篇只使用那一套。

## 一、建表：两套命名风格，选与库内一致的那套

### 风格 A：`deleted` + `create_time` + `creator`

```sql
CREATE TABLE IF NOT EXISTS `device_space_map`
(
    `id`          BIGINT      NOT NULL AUTO_INCREMENT COMMENT '主键',
    `tenant_id`   BIGINT      NOT NULL DEFAULT 0 COMMENT '租户编号',
    `device_id`   BIGINT      NOT NULL COMMENT '设备ID',
    `space_id`    BIGINT      NOT NULL COMMENT '空间ID',
    `sort`        INT         NOT NULL DEFAULT 0 COMMENT '同空间内排序，升序',
    `status`      TINYINT     NOT NULL DEFAULT 0 COMMENT '状态：0启用 1停用',
    `create_time` DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME    DEFAULT NULL COMMENT '更新时间',
    `creator`     VARCHAR(64) DEFAULT NULL COMMENT '创建者',
    `updater`     VARCHAR(64) DEFAULT NULL COMMENT '更新者',
    `deleted`     TINYINT     NOT NULL DEFAULT 0 COMMENT '是否删除：0否 1是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_device_space` (`tenant_id`, `device_id`, `space_id`),
    KEY `idx_tenant_space` (`tenant_id`, `space_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '设备与空间点位映射';
```

### 风格 B：`is_deleted` + `created_at` + `created_by`

```sql
CREATE TABLE IF NOT EXISTS `device_space_map`
(
    `id`         BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `tenant_id`  BIGINT       NOT NULL DEFAULT 0 COMMENT '租户编号',
    `device_id`  BIGINT       NOT NULL COMMENT '设备ID',
    `space_id`   BIGINT       NOT NULL COMMENT '空间ID',
    `sort`       INT          NOT NULL DEFAULT 0 COMMENT '同空间内排序，升序',
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME     DEFAULT NULL COMMENT '更新时间',
    `created_by` VARCHAR(64)  DEFAULT NULL COMMENT '创建人',
    `updated_by` VARCHAR(64)  DEFAULT NULL COMMENT '更新人',
    `is_deleted` TINYINT      NOT NULL DEFAULT 0 COMMENT '是否删除：0否 1是',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_device_space` (`tenant_id`, `device_id`, `space_id`),
    KEY `idx_tenant_space` (`tenant_id`, `space_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci
  COMMENT = '设备与空间点位映射';
```

**同一个库里不能两套并存**——混用会导致每次写 SQL 都要先翻表结构，且 ORM 的自动填充、逻辑删除插件只能认一套。

### 反面写法

| 写法 | 问题 |
|---|---|
| `id VARCHAR(32)`（UUID） | 聚簇索引随机插入，页分裂 + 写放大 |
| `is_deleted BIT(1) DEFAULT NULL` | 与同库 `TINYINT NOT NULL DEFAULT 0` 混两套；NULL 还要额外兜底 |
| `status INT` | 状态只有几个枚举值，INT 白占 3 字节 |
| `price DOUBLE` | 金额必须 DECIMAL |
| 无 COMMENT | 半年后没人知道 `sort` 是升序还是权重 |
| 已有 `(tenant_id, space_id)` 还单独建 `space_id`、`device_id` 索引 | 重复占写开销 |
| 只代码里判"设备是否已在该空间" | 并发下必重，缺 `uk_` |
| 不写 `IF NOT EXISTS` | 脚本重跑直接报错中断，后续语句不执行 |

## 二、索引：从查询倒推列序

先把真实条件列出来：

```sql
-- 高频查询：某租户某空间下的设备列表，按排序号
SELECT device_id, sort FROM device_space_map
WHERE tenant_id = #{tenantId} AND space_id = #{spaceId} AND deleted = 0
ORDER BY sort;
```

`(tenant_id, space_id)`：两个等值列在前，`ORDER BY sort` 只在小结果集内排序（可接受）。
要连排序一起吃掉，用 `(tenant_id, space_id, sort)`——等值 → 排序，`Extra` 里的 `Using filesort` 消失。

反面写法：

| 写法 | 问题 |
|---|---|
| `KEY idx_space(space_id)` 单列 | 查询必然带 `tenant_id`，单列索引只能补齐半个条件 |
| `KEY idx_status(status)` | 区分度极低，优化器多半不用，纯增写开销 |
| `KEY idx_time(create_time)` 但查询写 `DATE(create_time) = ?` | 函数包列，索引失效 |
| `(create_time, tenant_id)` 却按租户 + 时间范围查 | 范围列在前，后面的 `tenant_id` 用不上 |

## 三、查询写法对照

### 1. 深分页

```sql
-- 坏：翻到第 5000 页，前 100000 行读出来再丢掉
SELECT id, title FROM order_record
WHERE tenant_id = 1 AND deleted = 0
ORDER BY id DESC LIMIT 100000, 20;

-- 好：游标分页（前端传上一页最后一条 id）
SELECT id, title FROM order_record
WHERE tenant_id = 1 AND deleted = 0 AND id < #{lastId}
ORDER BY id DESC LIMIT 20;
```

游标分页不能跳页。前端必须有页码时不能这么改，改建覆盖索引 + 精简 `COUNT`。

### 2. 时间范围

```sql
-- 坏：函数包列 / 丢毫秒尾巴
WHERE DATE(create_time) = '2026-10-29'
WHERE create_time BETWEEN '2026-10-29 00:00:00' AND '2026-10-29 23:59:59'

-- 好：左闭右开
WHERE create_time >= '2026-10-29 00:00:00' AND create_time < '2026-10-30 00:00:00'
```

### 3. 布尔/删除列的字面量

```sql
-- 列是 TINYINT
WHERE deleted = 0

-- 列是 BIT(1)
WHERE deleted = b'0'

-- 列允许 NULL 时，统计与批量更新要兜底
WHERE (deleted = 0 OR deleted IS NULL)
```

写错类型不会报错，只是**查不到数据**。动手前先 `SHOW COLUMNS FROM <表> LIKE 'deleted'`。

### 4. 手写 SQL 的租户条件

```java
// 坏：@Select 走不到 ORM 的租户拦截器，跨租户串数据
@Select("SELECT * FROM order_record WHERE deleted = 0")
List<OrderRecordDO> listAll();

// 好：手写 SQL 自己带租户
@Select("SELECT id, title, level FROM order_record " +
        "WHERE tenant_id = #{tenantId} AND deleted = 0")
List<OrderRecordDO> listByTenant(@Param("tenantId") Long tenantId);
```

### 5. OR 改 UNION ALL

```sql
-- 坏：OR 两侧列不同，优化器常退化成全表扫
SELECT id FROM device_info WHERE device_code = #{kw} OR device_name = #{kw};

-- 好：各自能走索引
SELECT id FROM device_info WHERE device_code = #{kw}
UNION ALL
SELECT id FROM device_info WHERE device_name = #{kw} AND device_code <> #{kw};
```

### 6. 批量统计不要一条 SQL 塞几万 IN

```sql
-- 坏：IN 上万个值，包体超限 + 解析开销
SELECT COUNT(*) FROM order_record WHERE device_id IN (...);

-- 好：按维度聚合一次，或分批（每批 500）在应用层累加
SELECT tenant_id, COUNT(*) FROM order_record
WHERE create_time >= #{start} AND create_time < #{end} AND deleted = 0
GROUP BY tenant_id;
```

## 四、迁移脚本模板

目录与命名换成项目既有约定（`migration/`、`db/migration/`、`_sql/mysql/` 都行，跟随仓库现状）。

```sql
-- ======================================================================
-- 设备空间点位映射
-- 影响：新增表 1 张；无存量数据变更
-- 回滚：DROP TABLE IF EXISTS `device_space_map`;
-- ======================================================================

CREATE TABLE IF NOT EXISTS `device_space_map` ( ... );

-- 加列（幂等）：5.7 没有 ADD COLUMN IF NOT EXISTS，用 information_schema 守卫
SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'device_info' AND COLUMN_NAME = 'remark');
SET @ddl = IF(@col_exists = 0,
              'ALTER TABLE `device_info` ADD COLUMN `remark` VARCHAR(255) DEFAULT NULL COMMENT ''备注''',
              'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 加索引（幂等）：同理，查 information_schema.STATISTICS
SET @idx_exists = (SELECT COUNT(*) FROM information_schema.STATISTICS
                   WHERE TABLE_SCHEMA = DATABASE()
                     AND TABLE_NAME = 'device_info' AND INDEX_NAME = 'idx_tenant_code');
SET @ddl = IF(@idx_exists = 0,
              'ALTER TABLE `device_info` ADD KEY `idx_tenant_code` (`tenant_id`, `device_code`)',
              'SELECT 1');
PREPARE stmt FROM @ddl; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 插初始数据（幂等）
INSERT INTO `dict_type` (`name`, `type`, `status`, `created_by`, `created_at`, `is_deleted`)
SELECT '设备物理类型', 'device_type', 0, 'admin', NOW(), 0
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `dict_type` WHERE `type` = 'device_type' AND `is_deleted` = 0);

-- 数据修正前先看影响面（保留成注释也行，方便回溯）
-- SELECT COUNT(*) FROM order_record WHERE (deleted = 0 OR deleted IS NULL) AND handle_status = 0;

-- 验证
-- SHOW CREATE TABLE `device_space_map`;
-- SELECT COUNT(*) FROM `dict_type` WHERE `type` = 'device_type';
```

要点：
- 每条 DDL 都要能重跑，否则第二次执行会中断后面所有语句。
- 数据修正脚本先 `SELECT` 桩再 `UPDATE`，把影响行数写进注释。
- 回滚语句写在头部注释里，别等到出事才想。

## 五、EXPLAIN 判读示例

```
id  select_type  table  type   key        rows    Extra
1   SIMPLE       r      index  PRIMARY    10      Using where
```

翻译：走的是主键（`type=index` 全索引方向扫描），`rows=10` 只是 LIMIT 让它读 10 行就停——**列表本身没毛病**。

接口还是慢，就去看同一次请求里那条 `COUNT`：

```
id  select_type  table  type  key   rows    Extra
1   SIMPLE       r      ALL   NULL  1200000 Using where
```

这才是耗时来源：无覆盖索引、全表扫 + 回表。改法二选一：
1. 建覆盖索引 `(tenant_id, deleted, handle_status, create_time)`，让 `COUNT` 的 `Extra` 出现 `Using index`；
2. 前端不需要精确总数时关掉自动 count，直接省掉这条 SQL。

坐实"到底读了多少行"用会话计数，比 `rows` 预估可信：

```sql
SHOW SESSION STATUS LIKE 'Handler_read_next';     -- 远大于 LIMIT 值就是在白读
SHOW SESSION STATUS LIKE 'Sort_merge_passes';     -- >0 表示排序落盘
```
