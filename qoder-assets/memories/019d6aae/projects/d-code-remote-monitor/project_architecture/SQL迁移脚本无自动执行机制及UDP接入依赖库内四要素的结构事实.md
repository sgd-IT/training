---
title: "SQL迁移脚本无自动执行机制及UDP接入依赖库内四要素的结构事实"
usage_scenario:
    - "规划发版需要在目标库执行哪些迁移脚本"
    - "排查代码已上线但功能在线上不可用"
    - "核实索引或配置类DDL是否真的生效"
keywords:
    - "SQL手工迁移"
    - "idx_product_key缺失"
    - "UDP接入四要素"
    - "组件status与config"
    - "脚本入库不等于生效"
source: "auto"
---

消防远程值守的数据库变更没有 Flyway/Liquibase 自动迁移机制：`_sql/mysql/` 下按 VyyyyMMdd__描述.sql 命名的脚本（截至 2026-10-30 共 29 个）全部靠人工在目标库执行，脚本进入 git 仓库不等于线上库已生效。由此产生两类易误判：一是"发版要跑哪几个 SQL"必须以生产库实测为准而非看仓库文件列表——2026-10-30 用 MCP 查生产确认 eiot_product 只有 PRIMARY(id)、idx_product_key 不存在，才断定 V20261029 待执行；二是用户口头暂缓某脚本（"先不弄"）只表示不入 git 提交，与该脚本是否已在库中落地是两件事，需各自核实。

大华 UDP 终端接入是"代码与配置分离"的典型链路：代码侧（组件、解析器、推送）已随 master 上线，但功能真正可用还依赖 V20261027__udp_device_onboarding.sql 把四要素写进库——eiot_component 中 type='udp' 那一行的 status 与 config、UDP 专用产品 product_key 为 DhUdpF7xQm2Kp931、对应物模型、告警规则与告警配置。2026-10-30 实测生产这四项计数全为 0 且该组件记录（id=7，名称「官方 UDP 组件 (Java8)」）处于 status=0、config=null 状态，即终端上报无人接。判定该类"代码上了但功能没上"时，必须同时看组件的 status 与 config 两个字段（只看 status 会漏掉 config 为空的情况），并按 type='udp' 过滤 eiot_component 而非凭名称猜测。执行该脚本后还需重启 server 容器：ThingComponent 虽按 5 分钟轮询 DB 配置，但组件 status 由 0 变 1 需重启才会加载实例；脚本开头声明的两个 SET 会话变量（租户 id 与告警消息模板 id）须在执行前按目标环境逐一确认取值。
