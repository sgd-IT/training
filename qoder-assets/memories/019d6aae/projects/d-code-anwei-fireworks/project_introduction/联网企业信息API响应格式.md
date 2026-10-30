---
title: "联网企业信息API响应格式"
usage_scenario: []
keywords:
    - "API响应格式"
    - "联网企业"
    - "body数组"
source: "user"
---

联网企业信息API响应格式为JSON对象，包含code、data、sign、body四个字段；其中body字段为JSON数组，数组元素为包含timing/ip/port/user/pass/company_code/company_name/area_code/area_name/node_name/node_id/network_time/network_protocol等字段的企业节点信息对象。
