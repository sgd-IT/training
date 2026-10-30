---
title: "eiot-biz cross-module dependency boundary and application.yaml SSO/callpush config integrity"
usage_scenario:
    - "在 eiot-biz 模块新增用户相关逻辑时"
    - "修改 server 的 application.yaml 配置时"
    - "查找代码生成器 codegen 相关配置项时"
keywords:
    - "cross-module boundary"
    - "eiot-biz"
    - "application.yaml"
    - "SSO"
    - "callpush"
    - "enjoyiot.codegen"
    - "platform.info.base-package"
source: "auto"
---

The project enforces strict cross-module dependency boundaries: eiot-biz must not import or use system-biz's DAL-layer classes such as AdminUserMapper. Additionally, application.yaml (server/src/main/resources/) must retain critical config blocks to prevent runtime breakage: the SSO login configuration block (sso-login, ticket/create) and callpush whitelist (prevents 401 auth failures and capability-exposure platform integration breaks), and the enjoyiot.codegen block (front-type: 20 = Vue3 standard template, unit-test-enable: false) plus platform.info.base-package (= com.enjoyiot), which together drive the infra code generator's output package paths. When editing application.yaml, never drop these blocks; when locating codegen-related config, check the enjoyiot.* prefix in this file.
