---
title: "开发机数据库环境与MCP连接映射（含DDL限制与对外正式服务）"
usage_scenario:
    - "执行建表或改表DDL时选择通道"
    - "查询确认当前环境连接的是哪个库"
    - "盘点前后端APP各端连接指向的环境"
keywords:
    - "tgr-gz-vue"
    - "数据库实例映射"
    - "SCHEMA_DDL_PERMISSIONS"
    - "mysql.exe命令行"
    - "MCP连接落点验证"
    - "java -jar 启动"
    - "验证码Redis登录"
    - "园区平台正式地址"
    - "外部MQTT与短信"
source: "user"
---

本机数据库环境：(1) 项目 application-druid.yml 数据源：**【2026-09-28 三端发布切换后：生效=220.203.23.144:3306/tgr-gz-vue（正式库 anwei1-0001），160 被注释】——原为 160 生效/220 注释，切换起因：用户为"三端更新"将发布指向正式环境；注意本机 IDEA 后端一旦重启即直连正式库，开发调试勿对本机 28080 做测试写操作。三端发布指向核查结论（2026-09-28）：后端数据源=220（已切）；PC 端 .env.production 为 VUE_APP_BASE_API='/prod-api' 相对路径（真实落点在部署服务器 nginx 反代，仓库中无 nginx.conf，vue.config.js 的 localhost:28080 仅 devServer 开发代理）；APP 端 HttpInterface.IP（全工程唯一定义点，RetrofitUtils.baseUrl 引用）由 http://192.168.250.130:28080/（测试机）改为 **http://220.203.23.144:28080/**（2026-09-28 已改，需重新构建 APK 生效）。(2) MCP 服务器配置在用户级文件 C:\Users\Administrator\.qoder\mcp.json：名为 hc-standard-test 的 MCP 指向 tgr-gz-vue@192.168.250.160（= 测试库，用户口语"mcptest 的库"）；名为 tgr-gz-vue 的 MCP 指向 220.203.23.144（= 正式库）；两个 MCP 均未开 ALLOW_DDL_OPERATION，DDL 会被拒绝。(3) 建表/改表等 DDL 需用命令行：D:\Tool\mysql-8.0.46-winx64\bin\mysql.exe -h <host> -uroot -D tgr-gz-vue -e "source <sql路径>"；SQL 用 shell（非 IDE 工具）写成 UTF-8 无 BOM 的 .sql 文件再 source，避免中文注释经命令行传参乱码。(4) 本机 localhost:3306 另有本地 MySQL（root/123456，服务名 mysql），无 tgr-gz-vue 库，与本项目无关。
