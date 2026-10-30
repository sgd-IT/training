# SQL 检查清单（五层 + 判定口径）

用法：按层顺序过，命中就记一条。**判定口径**写的是"怎么确认"，不是"听起来像不像"——能查库、能看执行计划的不要靠猜。
默认分级：P0 阻断 / P1 明显风险 / P2 打磨。
`deleted`、`create_time`、`tenant_id` 这类列名在下面只是占位示例，实际以第 0 步探测到的项目既有口径为准；与本项目不一致但改动成本极高的存量列名，不进清单，只在报告末尾"存量遗留"段落说明。

---

## 第一层：表结构与命名

| # | 检查项 | 判定口径 | 分级 |
|---|---|---|---|
| 1.1 | 表名小写下划线、单数、有业务含义 | 出现驼峰、拼音、`test`/`tmp` 进生产 | P1 |
| 1.2 | 有主键且为 `BIGINT` 自增或雪花 | `SHOW CREATE TABLE` 看主键；UUID/`CHAR(32)` 做聚簇主键 | P1 |
| 1.3 | 引擎 InnoDB、字符集 utf8mb4 | `SHOW CREATE TABLE` 末尾 | P1 |
| 1.4 | 排序规则与同库其他表一致 | `information_schema.TABLES.TABLE_COLLATION` 横向对比；不一致会引发跨表 JOIN 隐式转换 | P1 |
| 1.5 | 无物理外键 | `information_schema.KEY_COLUMN_USAGE` 里 `REFERENCED_TABLE_NAME` 非空 | P2 |
| 1.6 | 表 COMMENT 说清业务含义 | 空 COMMENT | P2 |
| 1.7 | 字段数 ≤30~50 | `information_schema.COLUMNS` 计数 | P2 |

## 第二层：字段与类型

| # | 检查项 | 判定口径 | 分级 |
|---|---|---|---|
| 2.1 | 逻辑删除列 + 创建/更新时间列存在且与同库既有表同名同类型 | 与本库同类业务表比对；**新表另起一套列名（库里有 `deleted` 又写 `is_deleted`）= 分裂** | P1 |
| 2.2 | 多租户项目的数据表有租户列，且与同业务域其他表对齐 | 缺列 = 隔离无从落地 | P0 |
| 2.3 | 每列有 COMMENT，说明单位与取值 | 空 COMMENT、`xxx1` 这类无意义名 | P2 |
| 2.4 | 能用 NOT NULL 的都 NOT NULL 且有默认值 | 除时间/可空业务字段外大面积允许 NULL | P1 |
| 2.5 | 金额用 DECIMAL | 出现 FLOAT/DOUBLE 存金额 | P0 |
| 2.6 | 状态/枚举用 TINYINT | 用 VARCHAR 存 0/1，用 INT 存布尔 | P2 |
| 2.7 | 类型宽度合理 | `VARCHAR(255)` 装手机号、`BIGINT` 装年级 | P2 |
| 2.8 | 布尔/删除列的字面量与列类型匹配 | `TINYINT` 写 `0/1`、`BIT(1)` 写 `b'0'/b'1'`；写错不报错但查空集 | P1 |
| 2.9 | 列允许 NULL 时，条件与统计是否兜底 | 该列 `IS_NULLABLE=YES` 却只写 `col = 0`，会漏 NULL 行 | P1 |
| 2.10 | `TEXT/BLOB` 是否必须 | 大字段和主表混在一起、高频列表查询要 SELECT 到 | P1 |

## 第三层：索引

| # | 检查项 | 判定口径 | 分级 |
|---|---|---|---|
| 3.1 | 索引总数 ≤5 | `SHOW INDEX FROM t` 按 `Key_name` 去重计数 | P2 |
| 3.2 | 联合索引列序 = 等值列 → 排序/高区分度 → 范围列 | 拿最常用的 2~3 条查询条件逐条对列序；跳过首列即失效 | P1 |
| 3.3 | 无低区分度单列索引 | `SELECT COUNT(DISTINCT col)/COUNT(*)` 远小于 0.1 还单独建索引 | P1 |
| 3.4 | 关联列 `xxx_id` 有索引 | `SHOW INDEX` 里没有，但 JOIN / 子查询在用它 | P1 |
| 3.5 | 唯一业务约束有 `uk_` | 只有代码判重，并发下会重 | P0 |
| 3.6 | 索引命名跟项目既有风格 | 新索引命名随意、与同库其他表风格不一致 | P2 |
| 3.7 | 无重复索引（前缀包含关系） | `(a)` 与 `(a,b)` 同时存在，前者的等值查询被后者覆盖 | P2 |
| 3.8 | 零使用率索引是否清理 | `performance_schema.table_io_waits_summary_by_index_usage` 中 `COUNT_STAR=0` 且非唯一索引 | P2 |
| 3.9 | 新增索引前算过写侧成本 | 该表 DML 频率、写操作是否命中索引列、列是否频繁翻转、已有索引数边际（≤0~1 个偏加，≥5 个偏不加） | P2 |
| 3.10 | 有无更便宜的替代 | 能并入现有索引最左前缀、或做成覆盖索引一顶多条；否则才新开单列索引 | P2 |
| 3.11 | 缺索引是否正在拖死别的表的执行计划 | 关联列无索引 → 被驱动表退化成 Block Nested Loop → 驱动表 `ORDER BY … LIMIT` 失去索引顺序，`Extra` 出现 `Using temporary; Using filesort` | P0 |

## 第四层：SQL 写法

| # | 检查项 | 判定口径 | 分级 |
|---|---|---|---|
| 4.1 | 无 `SELECT *` | 列表/详情/导出 SQL 逐条看 | P1 |
| 4.2 | 条件列不包函数、不做运算 | `DATE(col)=`、`SUBSTRING(col)`、`col+1>`、`IFNULL(col)` | P1 |
| 4.3 | 无隐式类型转换 | 字符串列传数字、`varchar` 不加引号、跨表 collation 不一致 | P1 |
| 4.4 | 深分页改游标 | `LIMIT` 偏移 >10000 仍在用 offset | P1 |
| 4.5 | 更新/删除带 WHERE 且 WHERE 命中索引 | 无 WHERE = P0；有 WHERE 但列无索引 = 行锁退化表锁 | P0/P1 |
| 4.6 | 无前导模糊、`!=`、`NOT IN` 当主过滤 | `LIKE '%x'`、`NOT IN (子查询)` 用于主过滤 | P1 |
| 4.7 | `OR` 是否可改 `UNION ALL` | 两个分支各自能走索引时，`OR` 常退化成全表 | P1 |
| 4.8 | `IN` 元素 ≤1000 | 逐条数；大批量应走临时表或分批 | P1 |
| 4.9 | `JOIN` ≤3 张表、驱动表小 | 数表数量；小结果集表放驱动位 | P2 |
| 4.10 | 参数化，无 `${}` 拼接 | `<mapper>` 里搜 `${` | P0 |
| 4.11 | 无 `ORDER BY RAND()`、无无 LIMIT 的全表查询 | 搜 `RAND(` | P1 |
| 4.12 | 手写/原生 SQL 带租户条件与逻辑删除条件 | ORM 拦截器只兜标准链式查询；`@Select`、手写 `UNION`/子查询、白名单表要自己写 | P0 |
| 4.13 | 时间范围用左闭右开 | `>= 起 AND < 次日`，不要 `BETWEEN '... 00:00:00' AND '... 23:59:59'`（丢毫秒尾巴） | P1 |
| 4.14 | 分页的 `COUNT` 是否可省 | 前端不需要精确总数时关掉自动 count 或换估算 | P1 |

## 第五层：迁移脚本与上线安全

| # | 检查项 | 判定口径 | 分级 |
|---|---|---|---|
| 5.1 | 放在项目既有的迁移目录、命名跟既有脚本 | 与同目录其他迁移文件比对编号与命名 | P2 |
| 5.2 | 可重复执行 | 建表 `IF NOT EXISTS`、插数据 `NOT EXISTS`/派生表守卫；纯 `INSERT` 反复跑会翻倍 | P1 |
| 5.3 | 末尾带验证 SQL（注释形式也可） | 无自检语句，上线后无法判断成败 | P2 |
| 5.4 | 大表 DDL 有锁表评估 | 千万级表 `ALTER TABLE ... ADD COLUMN/INDEX` 无窗口说明；5.7 无 `INSTANT` | P0 |
| 5.5 | 数据变更脚本先 SELECT 后 UPDATE | 只有 UPDATE、没有前置 SELECT 确认影响行数 | P1 |
| 5.6 | 逻辑删除/状态批量更新带 NULL 兜底 | 列允许 NULL 时写 `(col = 0 OR col IS NULL)` | P1 |
| 5.7 | 脚本与代码同批提交、顺序明确 | 多个迁移脚本之间有依赖但无说明 | P2 |
| 5.8 | 回滚路径存在 | 加列/改语义无回滚 SQL 或开关 | P1 |
| 5.9 | 版本特性与目标库匹配 | 用了 CTE / 窗口函数 / `EXPLAIN ANALYZE` / `INSTANT` 但目标库是 5.7 | P0 |

---

## EXPLAIN 判读口径（别只看 key）

| 字段 | 看什么 | 危险信号 |
|---|---|---|
| `type` | 访问类型 | `ALL`（全表）、`index`（全索引扫描）要能解释成因 |
| `key` | 用了哪个索引 | **非空不等于没问题**，只说明用它定位，不代表只读了目标行 |
| `rows` | 预估扫描行数 | 远大于结果行数；与实际返回量差几个数量级 |
| `filtered` | 过滤后占比 | 极低说明大部分行白读 |
| `Extra` | 附加动作 | `Using filesort`、`Using temporary`、`Using join buffer`；**没有 `Using index` 就是回表** |

补充硬指标（比 EXPLAIN 更能定责）：

```sql
SHOW SESSION STATUS LIKE 'Handler_read_next';     -- 实际读了多少行，远大于 LIMIT 值就是在白读
SHOW SESSION STATUS LIKE 'Handler_read_rnd_next'; -- 全表扫的行数
SHOW SESSION STATUS LIKE 'Sort_merge_passes';     -- >0 表示排序落盘
```

带自动 count 的分页框架（MyBatis-Plus、PageHelper 等）必须**把列表 SQL 与 `COUNT` SQL 分开计时**：列表 `ORDER BY id DESC LIMIT` 常走主键反向扫描、读够 N 行就停（`type=index`、`rows=10`，看着完美），耗时大头往往在 `COUNT` 的非覆盖索引全表回表。只 EXPLAIN 列表那条必然误判。

MySQL 8.0.18+ 可用 `EXPLAIN ANALYZE` 拿实测耗时；5.7 只能 `SET profiling=1` + `SHOW PROFILE FOR QUERY n`。
