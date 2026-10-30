---
title: "隐患信息推送OpenAPI接口规范"
usage_scenario:
    - "对接外部园区平台时实现隐患数据同步"
    - "开发或重构隐患推送服务时对照字段映射和业务规则"
    - "编写接口文档或进行联调测试时参考请求体结构"
keywords:
    - "隐患推送"
    - "OpenAPI"
    - "saveOrUpdateBatch"
    - "双重预防机制"
---

系统提供`/openapi/check_info/saveOrUpdateBatch`接口用于新增或修改隐患信息，对应业务编号5.4.6.2，属于双重预防机制中任务排查模块。该接口接收隐患对象数组，关键必填字段包括id、outerId、riskMeasureId、businessState、hazardCode、dangerName、dangerLevel、dangerSrc、dangerManageType、dangerDesc、dangerManageDeadline、hazardDangerType、dangerImages、registrant、registTime、liablePerson、hazardCategory、checkRegistTime、handleSource、latitude、longitude、governmentDeptName、acceptanceDeptName、riskUnitId、createBy、createTime、updateBy、updateTime。
