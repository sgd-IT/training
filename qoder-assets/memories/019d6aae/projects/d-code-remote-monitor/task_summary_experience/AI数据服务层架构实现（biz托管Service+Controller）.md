---
title: "AI数据服务层架构实现（biz托管Service+Controller）"
usage_scenario:
    - "为外部AI或第三方系统封装内部业务数据接口时确定分层边界"
    - "当项目规范要求API模块仅含DTO、业务逻辑必须在biz模块时的落地实践"
    - "排查跨模块服务调用失败问题时参考正确的依赖注入和包路径配置"
keywords:
    - "AI数据接口"
    - "分层架构"
    - "module-ai-biz"
source: "auto"
---

## 任务描述
为AI服务提供设备与告警数据接口，明确要求Service接口和实现均放在module-ai-biz中，module-ai-api仅保留DTO，Python AI通过HTTP调用Controller。

## 执行过程
```mermaid
graph TD
    A[需求变更:Service放biz] --> B[核查module-ai-api/biz依赖关系]
    B --> C[确认eiot服务可被biz直接引用]
    C --> D[精读TodayAlarmStatisticsRespVO和EiotDeviceInfoDO字段定义]
    D --> E[在module-ai-api创建3个DTO: AiDataPageReqDTO/AiDeviceDetailDTO/AiAlarmDetailDTO]
    E --> F[在module-ai-biz创建AiDataService接口和AiDataServiceImpl实现]
    F --> G[在module-ai-biz创建AiDataController暴露REST接口]
    G --> H[验证springdoc-openapi依赖确保Swagger可用]
```

## 任务总结
成功构建面向AI的数据服务层：严格遵循“api仅DTO、biz含Service+Controller”架构，DTO精准映射eiot业务VO/DO字段，ServiceImpl通过依赖注入调用fireDashboardService/fireAlarmOrderService/eiotDeviceInfoMapper，Controller统一返回CommonResult<data>格式，完全满足外部AI服务HTTP调用需求。
