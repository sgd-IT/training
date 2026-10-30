---
title: "dynamic-datasource下Hikari与Lettuce连接池的正确属性前缀"
usage_scenario:
    - "核对多数据源连接池参数为何不生效"
    - "编写或评审新环境application.yml数据源段"
    - "排查数据库连接数不足或线程挂起"
keywords:
    - "dynamic-datasource 3.3.1"
    - "spring.datasource.dynamic.hikari"
    - "min-idle/max-pool-size"
    - "lettuce连接池"
    - "静默不生效"
source: "auto"
---

anwei-internet-java 使用 baomidou dynamic-datasource-spring-boot-starter 3.3.1 管理多数据源（master/iot），其 HikariCP 参数唯一生效前缀是 spring.datasource.dynamic.hikari.*，字段名为 min-idle 与 max-pool-size（通过解包该 jar 的 META-INF/spring-configuration-metadata.json 核实）。以下两种常见写法均被静默忽略、不报错也不生效：spring.datasource.hikari.minimum-idle / maximum-pool-size（Spring Boot 原生写法，因 DataSourceAutoConfiguration 被 dynamic 跳过而无绑定者）；spring.datasource.dynamic.minimum-idle / maximum-pool-size（缺 hikari 层且字段名错）。项目现有 application-dev.yml 即犯后者，实际长期以默认 10 连接运行。Redis 侧同理：Boot 2.1.5 + 无 jedis 依赖，只有 spring.redis.lettuce.pool.* 生效，spring.redis.jedis.pool.* 整段无效。核对任意环境的连接池配置时须按这两个正确前缀检查。
