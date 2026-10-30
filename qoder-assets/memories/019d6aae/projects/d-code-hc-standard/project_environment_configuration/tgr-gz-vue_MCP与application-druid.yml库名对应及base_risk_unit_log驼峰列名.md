---
title: "tgr-gz-vue MCP与application-druid.yml库名对应及base_risk_unit_log驼峰列名"
usage_scenario:
    - "判断tgr-gz-vue是否为本项目库时核对application-druid.yml配置"
    - "查询base_risk_unit_log前确认驼峰列名避免Unknown column报错"
keywords:
    - "application-druid.yml"
    - "base_risk_unit_log"
    - "驼峰列名"
    - "tgr-gz-vue库名映射"
    - "SELECT DATABASE"
source: "auto"
---

MySQL MCP tgr-gz-vue 与 hc-standard 项目 application-druid.yml 数据源的库名对应关系及表结构查询坑：

1. MCP 服务 tgr-gz-vue 对应的库名与 ruoyi-admin/src/main/resources/application-druid.yml 中 master 数据源配置的库名完全一致——开发库 jdbc:mysql://192.168.250.160:3306/tgr-gz-vue，注释掉的正式库 jdbc:mysql://220.203.23.144:3306/tgr-gz-vue。不得因服务名叫 tgr-gz-vue、工作区叫 hc-standard 就断定"这是另一个项目的库"而放弃 MCP 改走命令行；先跑 SELECT DATABASE(), @@hostname 再决定。

2. 表结构坑：base_risk_unit_log 列名为驼峰（riskUnitId / stopTime / activationTime / sync_status），按 start_time、end_time 查询会报 Unknown column；不确定时先查 information_schema.COLUMNS。
