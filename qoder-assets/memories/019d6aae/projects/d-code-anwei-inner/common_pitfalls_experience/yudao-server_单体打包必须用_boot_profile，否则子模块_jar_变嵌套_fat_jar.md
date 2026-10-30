---
title: "yudao-server 单体打包必须用 boot profile，否则子模块 jar 变嵌套 fat jar"
usage_scenario:
    - "排查单体启动缺 bean 报错"
    - "多模块 fat jar 嵌套类加载失败"
    - "IDEA 大面积无法解析符号"
keywords:
    - "spring-boot repackage 嵌套"
    - "yudao-server 缺 bean"
    - "cloud profile 打包陷阱"
    - "boot profile 单体打包"
    - "fat jar 嵌套检测"
source: "auto"
---

Bug 类别：Spring Boot 多模块 fat jar 嵌套导致模块类加载失败——单体启动报 No qualifying bean（如 PermissionCommonApi），IDEA 大面积"无法解析符号"。
根因：anwei-inner-office 根 pom 默认 profile 为 cloud（skip.repackage=false），执行 mvn package 时各子模块被 spring-boot-maven-plugin repackage 成可执行 fat jar（内部为 BOOT-INF/classes 结构），再被嵌入 yudao-server 的 fat jar；运行时类加载器按 jar!/cn/iocoder/... 找类，而实际路径是 jar!/BOOT-INF/classes/...，模块类全部加载不到。yudao-server.jar 体积异常（2.6GB，正常约 326MB）是显著信号。
修复模式：mvn clean package -P boot -pl yudao-server -am -DskipTests（boot profile 下 skip.repackage=true，子模块只出普通 jar）；必须加 clean，否则 maven-jar-plugin 可能跳过重建、沿用旧的 fat jar 产物。
复用教训：排查此类问题时，先展开 yudao-server.jar 的 BOOT-INF/lib/*.jar 验证嵌套 jar 是否为 BOOT-INF 结构；聚合包体积异常增大即为此类问题的快速判据。
