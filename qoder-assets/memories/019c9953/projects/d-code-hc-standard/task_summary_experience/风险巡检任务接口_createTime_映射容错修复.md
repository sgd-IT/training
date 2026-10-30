---
title: "风险巡检任务接口 createTime 映射容错修复"
usage_scenario:
    - "修复数据库时间字段映射到String类型时因连接中断导致的ResultSet读取异常"
    - "为状态更新类接口（如巡检、审批）添加可读性异常响应机制"
    - "同类弱网/高延迟环境下SQL时间字段格式化映射实践"
keywords:
    - "createTime映射"
    - "DATE_FORMAT"
    - "巡检任务"
    - "异常响应"
---

## 任务描述
修复风险巡检任务接口 `/api/risk/updTaskRecordStatus` 中因数据库连接不稳定导致的 `createTime` 字段映射报错问题。

## 执行过程
```mermaid
graph TD
    A[需求:修复createTime映射报错] --> B[定位相关文件：ApiRiskController、BaseRiskTaskRecordMapper.xml等]
    B --> C[读取ApiRiskController确认日志对象可用性]
    C --> D[在BaseRiskTaskRecordMapper.xml中将check_time/updateTime/createTime统一改为DATE_FORMAT格式化表达式]
    D --> E[在ApiRiskController.updCheckStatus方法中添加try-catch]
    E --> F[捕获异常并记录详细日志+返回可读错误响应]
```

## 任务总结
成功提升接口健壮性：SQL 层通过 `DATE_FORMAT(check_time,'%Y-%m-%d %H:%i:%s')` 统一时间字段格式，避免 DATETIME → String 直接映射失败；代码层增加结构化异常处理，使测试阶段能直接获取错误原因，无需排查服务器日志。
