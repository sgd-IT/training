---
title: "SD平台token源从YAML配置迁移至数据库"
usage_scenario:
    - "将任意第三方平台对接配置（如token、密钥、地址）从配置文件迁移至数据库时的全流程参考"
    - "排查因配置未生效导致的推送失败问题，快速定位是否仍依赖旧配置机制"
keywords:
    - "token迁移"
    - "数据库驱动"
    - "配置解耦"
---

## 任务描述
将SD平台企业token来源从YAML配置文件迁移至数据库`em_information`表的`upload_company_pwd`字段，并同步更新Java业务逻辑。

## 执行过程
```mermaid
graph TD
    A[需求:token源从YAML迁移到DB] --> B[定位企业表结构]
    B --> C[通过grep+read确认em_information表及upload_company_code/upload_company_pwd字段]
    C --> D[生成三段式SQL：SELECT预检→逐条UPDATE→SELECT后验]
    D --> E[定位PushingSDDataService.forEachCompany()方法]
    E --> F[移除SDConfigEntity依赖，改用Information实体字段读取token]
    F --> G[清理SDConfigEntity import和@Autowired]
```

## 任务总结
成功实现配置迁移：1）SQL脚本确保9家企业编码与token精准写入数据库；2）Java层彻底解耦YAML配置，`forEachCompany()`方法直接从`Information`实体获取`uploadCompanyCode`和`uploadCompanyPwd`；3）删除冗余依赖，提升系统可维护性。
