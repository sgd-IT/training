---
title: "mysql CLI 跑配置SQL脚本的三条约束与UDP组件生产现状"
usage_scenario:
    - "编写需在mysql CLI执行的配置或迁移SQL脚本时"
    - "脚本经source执行报语法或字符错误时排查"
    - "核对UDP组件在生产与测试库的启用状态差异时"
keywords:
    - "CONCAT函数拼接"
    - "mysql客户端分号切句"
    - "udp组件生产禁用"
    - "centerAddr缺失"
    - "source执行SQL脚本"
source: "auto"
---

通过本地 mysql.exe CLI 以 source 方式执行仓库 _sql/mysql/ 下的配置脚本时，实测确认三条约束：

1. MySQL 里字符串拼接必须用 CONCAT() 函数；加号运算符对字符串会被当作数值相加，会把多行 JSON 字面量拆坏并报 "Truncated incorrect DOUBLE value"。脚本内所有需要拼接变量的地方一律改用 CONCAT。

2. mysql 客户端按分号切分语句，因此：其一，多行字符串字面量在 source 模式下切点不可控（曾报 ASCII null byte appeared、Unknown command 等错），需把所有字符串字面量压成单行；其二，嵌入 SQL 的 JS translate 脚本体内不能含分号，应改写成无分号的单个 return 表达式。

3. 生产库 eiot_component 中 type 为 udp 的行处于 status=0 且 config 为空，即大华 UDP 服务在生产环境默认未启用；测试库对应行已启用并配好 port48888、host 0.0.0.0、protocolMode dahua、defaultProductKey DhUdpF7xQm2Kp931、enableAutoRegister true，但缺 centerAddr。落地脚本需补 centerAddr 值 020000000001 并置 status=1。UDP 产品 DhUdpF7xQm2Kp931 在测试库存在（tenant_id=1、keep_alive_time=7200），生产库不存在。

适用边界：本仓库经 mysql CLI 执行的配置或迁移 SQL 脚本编写与调试；不适用：纯 MCP 单条查询（MCP 不支持多语句与变量）。
