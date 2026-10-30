---
title: "数据访问层无XML与列名风格例外的结构事实"
usage_scenario:
    - "核对代码字段与库列差异时确定扫描范围"
    - "排查Unknown column报错但库中已有该字段"
    - "识别手工建表与脚本建表来源"
keywords:
    - "无Mapper XML全注解"
    - "DO字段扫描覆盖"
    - "notify_message驼峰列名"
    - "TableField反引号转义"
    - "exist=false缺列标记"
source: "auto"
---

消防远程值守后端（anwei-fire-remote-monitor）数据库访问层的两个结构性事实（2026-10-23 经 information_schema 全量对账 + 全仓 grep 核验）：

1. **全仓无 Mapper XML，SQL 全部由 MyBatis-Plus 注解与 LambdaQueryWrapper 生成**：搜索 resources/mapper 目录无匹配文件，`EiotNotifyMessageMapper`、`FireCallRecordMapper` 等均为纯接口 + default 方法。推论：判断"代码需要哪些库列"只需扫描 `**/dal/dataobject/**/*DO.java` 的字段（含 @TableName / @TableField 解析），即可近似等价于代码对库结构的全部诉求，无需再解析 XML；反之若将来引入手写 @Select 列名，此覆盖假设失效。

2. **存在列名命名风格例外，不能假定全库 snake_case**：`notify_message` 表的实际列名是驼峰 `messageType`（同表其余列为 snake_case），DO 字段未加 @TableField 时 MyBatis-Plus 会按 `message_type` 拼 SQL 而报 Unknown column。排查"库里明明有该字段却报缺列"时应先查 information_schema.COLUMNS 看实际列名风格，而非先怀疑字段不存在。另 `eiot_virtual_device` 用 @TableField("`trigger`") 反引号转义保留字，做自动化比对时须剥掉反引号再比列名。

3. **DO 上的 @TableField(exist = false) 是库表缺列的临时绕过标记**：如 FireMaintenanceUnitDO 把 creator/updater 标为 exist=false 并在注释写明"数据库表缺失该列"，可作为"该表系手工建表、非脚本产出"的识别信号。
