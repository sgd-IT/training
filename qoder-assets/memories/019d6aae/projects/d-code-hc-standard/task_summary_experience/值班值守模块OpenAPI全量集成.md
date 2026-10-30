---
title: "值班值守模块OpenAPI全量集成"
usage_scenario:
    - "安全基础模块新增类似平台对接功能（如事故上报、风险单元）时参考整体架构"
    - "排查值班值守相关功能异常时快速定位从Vue到OpenApiConstant的全链路文件"
    - "同类分页+单查功能扩展需复用PlatementOndutyApi设计模式"
keywords:
    - "值班值守"
    - "OpenAPI"
    - "分页查询"
    - "单个查询"
source: "auto"
---

## 任务描述
完成值班值守模块全量OpenAPI集成，覆盖5.2.4.4单个查询与5.2.4.5分页查询功能，实现从前端页面到后端服务再到平台API调用的完整链路。

## 执行过程
```mermaid
graph TD
    A[需求:值班值守API全量集成] --> B[分析OpenAPI Spec确定端点与参数]
    B --> C[首轮实现单个查询：添加Onduty常量类、sendGet方法、PlatementOndutyApi.getById、Controller接口、前端API及Vue页面]
    C --> D[编译验证发现OpenApiConstant语法错误及PlatementOndutyApi缺少List/Map导入]
    D --> E[第二轮实现分页查询：修复常量类语法、补充PlatementOndutyApi.page方法及import、新增Controller/page接口、前端API封装、Vue页面分页区域+逻辑]
    E --> F[完整编译验证通过]
```

## 任务总结
成功集成值班值守全部5个OpenAPI：新增Onduty常量类并修复语法；在PlatementBaseApi补充sendGet；在PlatementOndutyApi实现getById/page；Controller提供/get/{id}和/page接口；前端完成API封装与index.vue双模式查询（单个ID输入+多条件分页表格）。
