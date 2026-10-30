---
title: "JCXX主要负责人电话兜底策略"
usage_scenario:
    - "处理JCXX类企业信息上报逻辑时补充缺失字段"
    - "编写或重构企业信息映射函数时确定电话字段优先级"
    - "排查省平台校验失败问题时参考兜底规则"
keywords:
    - "JCXX"
    - "主要负责人电话"
    - "mobile兜底"
    - "字段映射"
---

当JCXX主要负责人电话（principal_person_phone）为空时，使用企业移动电话（mobile）字段进行兜底填充。
