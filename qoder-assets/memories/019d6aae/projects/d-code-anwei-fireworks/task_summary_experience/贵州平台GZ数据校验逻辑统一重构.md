---
title: "贵州平台GZ数据校验逻辑统一重构"
usage_scenario:
    - "对其他省份或平台的数据推送模块进行类似校验逻辑集中化改造"
    - "当发现多个地方重复实现相同校验逻辑时，参考本方案设计统一校验器"
    - "排查GZDataFilter中冗余方法或SetGZUploadInfo中重复校验时快速定位清理范围"
    - "审查application.yml等配置文件时定位GZ校验器启用开关或参数依赖"
keywords:
    - "GZ校验"
    - "统一校验器"
    - "GZDataValidator"
source: "auto"
---

## 任务描述
重构贵州平台（GZ）数据校验逻辑，将分散在 PushingGZDataService 和 SetGZUploadInfo 中的重复校验，统一收口至新建的 GZDataValidator 类，并清理 GZDataFilter 中已迁移的冗余 filter 方法。

## 执行过程
```mermaid
graph TD
    A[需求:统一GZ数据校验] --> B[新建GZDataValidator.java]
    B --> C[提升GZDataFilter.isValidIdcard为public供调用]
    C --> D[在PushingGZDataService中替换3处filter调用为validateXxxList]
    D --> E[在SetGZUploadInfo中删除person/car出入记录的inline校验共45行]
    E --> F[从GZDataFilter中删除3个filterValidXxx方法及3个private辅助方法]
    F --> G[保留GZDataFilter中身份证校验/标准化/字段兜底等基础工具方法]
    G --> H[编译验证通过]
```

## 任务总结
成功建立分层校验体系：GZDataValidator 负责推送前批量过滤（覆盖12类数据），GZDataFilter 降级为纯工具类（仅提供 isValidIdcard/normalizeIdcard/resolveXXX 等底层能力），PushingGZDataService 和 SetGZUploadInfo 不再承担校验职责，代码更清晰、可维护性显著提升。
