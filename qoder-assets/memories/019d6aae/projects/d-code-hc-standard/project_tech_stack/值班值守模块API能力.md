---
title: "值班值守模块API能力"
usage_scenario:
    - "新增或修改值班值守功能时确认平台侧API支持范围"
    - "对接平台API时验证各操作是否可用"
    - "编写接口文档或技术方案时引用标准API路径"
keywords:
    - "值班值守"
    - "RESTful"
    - "API"
    - "CRUD"
source: "auto"
---

值班值守模块已实现完整的CRUD RESTful API能力：
- 新增：POST /openapi/basic/onduty/save
- 修改：POST /openapi/basic/onduty/update
- 删除：DELETE /openapi/basic/accident/{id}
- 单个查询：GET /openapi/basic/onduty/{id}
