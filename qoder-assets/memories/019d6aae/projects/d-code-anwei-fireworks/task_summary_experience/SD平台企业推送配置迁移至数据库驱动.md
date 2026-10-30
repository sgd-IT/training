---
title: "SD平台企业推送配置迁移至数据库驱动"
usage_scenario:
    - "将任意平台推送配置（如NM、SD）从YAML迁移到数据库时的完整重构流程"
    - "排查因YAML配置缺失/过期导致的‘未配置推送企业’类日志问题"
    - "同类系统中消除配置与数据库不一致风险的设计参考"
keywords:
    - "SD推送"
    - "数据库驱动"
    - "配置迁移"
---

## 任务描述
将山东平台（SD）企业信息推送功能由 YAML 配置驱动，全面迁移至数据库驱动：移除所有 YAML 中的 sdupload.companies 配置，代码中不再读取 SDConfigEntity，改为直接查询 em_information 表中 upload_company_code 和 upload_company_pwd 非空的企业记录。

## 执行过程
```mermaid
graph TD
    A[日志报错：未配置推送企业列表] --> B[定位 dev.yml 缺失 companies 配置]
    B --> C[补全 dev.yml companies 列表（临时修复）]
    C --> D[用户指出 company-name 应从 em_information 表获取 → 移除 CompanyConfig.companyName 字段及 YAML 中所有 company-name 行]
    D --> E[用户进一步指出 company-code/token 也应从 DB 获取 → 彻底重构 PushingSDDataService.forEachCompany 逻辑]
    E --> F[移除 SDConfigEntity 依赖，改用 LambdaQueryWrapper 查询 upload_company_code/upload_company_pwd 非空记录]
    F --> G[同步删除 application-dev.yml、application-prod.yml、deploy/.../application-prod.yml 中整个 sdupload 节点]
```

## 任务总结
成功实现 SD 推送配置零 YAML 化：代码层完全解耦 SDConfigEntity，通过数据库字段动态识别企业；三处 YAML 配置文件中 sdupload 节点被彻底清除；企业增删仅需维护 em_information 表，大幅提升可维护性与一致性。
