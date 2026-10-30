---
title: "山东推送企业级Token隔离机制"
usage_scenario:
    - "新增企业配置时确认token是否独立分配"
    - "审查推送请求头生成逻辑是否绑定企业token"
    - "排查认证失败问题时验证token来源是否正确"
    - "安全审计时确认是否存在token越权使用风险"
keywords:
    - "企业token"
    - "Token隔离"
    - "RSA签名"
    - "SD推送"
---

山东推送模块采用企业级Token隔离机制，每个企业配置独立的10位token，推送时严格使用对应企业的专属token进行RSA签名认证，不共享全局token。
