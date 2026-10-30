---
title: "客户管理API响应数据结构"
usage_scenario:
    - "前端开发客户列表页面时映射响应字段"
    - "后端编写或修改CustomerController返回逻辑时保持字段一致性"
    - "编写单元测试验证API返回结构是否符合预期"
keywords:
    - "客户API"
    - "contractEndDate"
    - "deviceCount"
    - "contractStatus"
    - "响应结构"
---

客户管理API（/weibao/customer/list）返回数据结构包含以下关键字段：
- `contractEndDate`: 合同到期日（date类型，可为null）
- `contractStatus`: 合同状态（string类型，可为null）
- `deviceCount`: 设备数量（number类型）
- `abbr`: 客户简称（string类型，可为null）
- `address`: 项目地址（string类型）
- `contactPerson`: 联系人（string类型）
- `contactPhone`: 联系电话（string类型）
- `name`: 客户名称（string类型）
- `status`: 客户状态（string类型）
