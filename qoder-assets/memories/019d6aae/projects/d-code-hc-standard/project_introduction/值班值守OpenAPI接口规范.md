---
title: "值班值守OpenAPI接口规范"
usage_scenario:
    - "开发值班值守相关功能时确认平台API契约"
    - "编写前端调用代码时确定参数结构和必填项"
    - "生成API文档或对接第三方系统时提供标准接口定义"
    - "编写单元测试或集成测试时构造合法请求体"
keywords:
    - "值班值守"
    - "OpenAPI"
    - "/openapi/basic/onduty/save"
    - "必填参数"
source: "user"
---

项目提供'5.2.4.1新增值班值守'OpenAPI接口:
- 路径: POST /openapi/basic/onduty/save
- 功能: 新增值班值守记录
- 必填参数: dutyBtime(值班开始时间), dutyEtime(值班结束时间), practitionersId(从业人员ID), practitionersName(员工姓名), practitionersPhone(员工电话), practitionersPost(员工岗位), position(职位, 0-带班领导/1-值班人员)
- 接口归属: 5.接口清单/5.2安全基础/5.2.4值班值守
