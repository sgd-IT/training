---
title: "YudaoServerApplication 单体唯一启动入口与两种运行方式"
usage_scenario:
    - "需要启动 anwei-inner 后端时选择入口与命令"
    - "IDEA 里误用各模块 ServerApplication 启动前"
    - "重启后端前确认 48080 端口与 profile"
keywords:
    - "YudaoServerApplication"
    - "单体启动入口"
    - "48080 端口"
    - "java -jar yudao-server.jar"
    - "prod profile 默认激活"
source: "auto"
---

anwei-inner-office 后端为 yudao 框架多模块工程，采用**单体启动**：唯一入口是 yudao-server 模块的 `cn.iocoder.yudao.server.YudaoServerApplication`，一个进程内承载 system / infra / bpm / oa / hrm / crm / wms / asset 等全部业务模块，监听端口 48080。application.yaml 已写死 `spring.profiles.active: prod`，命令行无需再加 `--spring.profiles.active`。

可用启动方式两种：
- IDEA：直接 Run YudaoServerApplication，前提是各模块 `target/classes` 有编译产物；若外部执行过 mvn clean/package，须先 Build → Rebuild Project，否则报缺 bean 或 ClassNotFoundException（找不到主类）。
- 命令行：在 anwei-inner-office 目录执行 `java -jar yudao-server\target\yudao-server.jar`（该 jar 需用 `-P boot` 打包）。

workspace.xml 中的 BpmServerApplication、CrmServerApplication、HrmServerApplication 等各模块启动配置属框架自带的微服务玩法，本项目不采用，逐个启动会引发端口与依赖问题。重启前需确认 48080 未被旧的 java 进程占用。
