---
title: "RESTful DELETE接口路径定义错误"
usage_scenario: []
keywords:
    - "RESTful"
    - "DELETE接口"
    - "路径定义"
    - "OpenApiConstant"
---

RESTful风格DELETE接口应定义为资源路径（如`/openapi/risk_fourchart/{id}`），而非动作路径（如`/openapi/risk_fourchart/saveOrUpdate`）；若在常量类中将DELETE与SAVEORUPDATE共用同一字符串，会导致实际HTTP请求路径错误。（来源：OpenApiConstant.java）
