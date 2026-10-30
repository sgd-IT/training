---
title: "@RestControllerAdvice必须指定produces=application/json"
usage_scenario:
    - "全局异常处理器返回JSON时出现HttpMediaTypeNotAcceptableException"
    - "浏览器访问不存在路径触发404但异常处理失败并报错"
    - "Spring Boot项目中GlobalExceptionHandler无法正常返回R对象"
keywords:
    - "RestControllerAdvice"
    - "produces"
    - "MediaType.APPLICATION_JSON_VALUE"
    - "HttpMediaTypeNotAcceptableException"
---

Spring `@RestControllerAdvice` 默认不指定 `produces`，当客户端 `Accept` 头不包含 `application/json`（如浏览器发 `text/html`）时，异常处理器返回 JSON 会因媒体类型不匹配抛出 `HttpMediaTypeNotAcceptableException`。正确做法是在注解中显式声明 `@RestControllerAdvice(produces = MediaType.APPLICATION_JSON_VALUE)`，强制所有异常响应为 JSON 格式。（来源：Spring Web）
