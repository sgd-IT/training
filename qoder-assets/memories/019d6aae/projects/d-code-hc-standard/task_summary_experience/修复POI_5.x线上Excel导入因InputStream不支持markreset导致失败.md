---
title: "修复POI 5.x线上Excel导入因InputStream不支持mark/reset导致失败"
usage_scenario:
    - "Spring Boot项目中POI 5.x Excel导入在生产环境报UNKNOWN或不识别文件类型"
    - "排查本地与线上环境差异导致的流处理异常"
    - "优化Excel工具类以增强对不同Servlet容器InputStream的兼容性"
keywords:
    - "POI 5.x"
    - "InputStream mark/reset"
    - "Excel导入"
    - "环境差异"
source: "auto"
---

## 任务描述
解决同一Excel文件（16KB）本地可导入、线上报错的问题，根因是线上Servlet容器返回的InputStream不支持mark/reset，导致POI 5.x无法识别文件类型。

## 执行过程
```mermaid
graph TD
    A[现象:本地OK线上报错] --> B[排除Nginx截断] 
    B --> C[聚焦POI 5.x的WorkbookFactory.mark/reset依赖]
    C --> D[确认线上InputStream.markSupported=false]
    D --> E[在ExcelUtil.importExcel中用IOUtils.toByteArray缓冲流]
    E --> F[改用ByteArrayInputStream传给POI，100%支持mark/reset]
    F --> G[同步移除Controller中冗余的魔数校验]
    G --> H[添加诊断日志：文件大小、文件头、markSupported状态]
```

## 任务总结
成功修复Excel导入环境差异问题：在ExcelUtil中强制将原始InputStream读入byte[]并转为ByteArrayInputStream，彻底绕过原始流兼容性问题；同时精简Controller逻辑，增加关键诊断日志，使后续同类问题可直接通过日志定位根因。
