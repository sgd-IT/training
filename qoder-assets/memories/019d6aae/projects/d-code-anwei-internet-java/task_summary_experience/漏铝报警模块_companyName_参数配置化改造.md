---
title: "漏铝报警模块 companyName 参数配置化改造"
usage_scenario:
    - "将接口参数迁移至配置中心或配置文件的类似改造"
    - "多环境（dev/test/prod）下统一新增配置项并确保一致性"
    - "排查因配置缺失导致的空指针或文案拼接异常"
keywords:
    - "companyName"
    - "配置化"
    - "LoulvProperties"
    - "loulv前缀适用边界"
    - "配置缺失空指针"
source: "auto"
---

## 任务描述
将漏铝报警功能中的 `companyName` 参数从接口请求参数移出，改为统一由配置文件 `loulv.companyName` 管理，并同步迁移 `audioSaveDir` 路径配置。

## 执行过程
```mermaid
graph TD
    A[需求: companyName 配置化] --> B[新建 LoulvProperties.java 绑定 loulv.*]
    B --> C[修改 JiagoController.java 移除 companyName 入参]
    C --> D[修改 JiagoDeviceService.java 注入并读取配置]
    D --> E[向 application-dev.yml 追加 loulv 配置段]
    E --> F[向 application-test.yml 追加 loulv 配置段]
    G[向 application-prod.yml 追加 loulv 配置段]
    H[H --> I[清理 dev.yml 中重复配置段]]
    I --> J[增量编译验证 monkey-monitor-api 模块]
```

## 任务总结
成功实现参数配置化：新建 `LoulvProperties` 类绑定 `loulv.*`，Controller 去除 `companyName` 入参，Service 改为注入读取，三环境 yml 均补充 `loulv.companyName` 和 `loulv.audioSaveDir`；特别注意 `companyName` 必须显式配置（Java 字段无默认值），否则播报文案为空。

## Notes
- 本条改造的 `loulv.*` 前缀与 `LoulvProperties`/`JiagoController`/`JiagoDeviceService` 类只存在于 monkey-aluminium-api 项目版本。2026-10 在 anwei-internet-java 工作区全库检索 `loulv`、`companyName`、`audioSaveDir`、`LoulvProperties` 均无任何匹配，该工作区的漏铝配置前缀为 `monkey.leak.*`（MonkeyLeakProperties）与 `dmpk.*`（DMPKEntity），漏铝接口实现为 modules.mon 包的 JiagoVideoLeakageController。在 anwei-internet-java 排查漏铝配置缺失时不要按 `loulv.*` 查找。
- 可迁移的方法论结论仍然有效：@ConfigurationProperties 绑定的 Boolean/String 字段若 Java 侧无默认值且 yml 未配，会分别导致拆箱 NPE 与文案为空，新增配置项必须三环境同步补齐。
