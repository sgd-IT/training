---
title: "丹唛派克IP音柱服务集成规范"
usage_scenario: []
keywords:
    - "丹唛派克"
    - "DMPK"
    - "RestTemplate"
    - "语音播报"
    - "Basic Auth"
source: "user"
---

项目集成丹唛派克（DMPK）IP音柱服务，通过RestTemplate调用其/v1/speech接口实现语音播报；采用Basic Auth认证（token Base64编码），请求体为JSON格式，包含device_codes、text、vcn等字段；配置通过DMPKEntity实体类绑定application.yml中的dmpk前缀配置。
