---
title: "deleted字段映射规则"
usage_scenario:
    - "新增或修改涉及deleted字段的数据映射逻辑时"
    - "排查deleted字段值异常问题时"
    - "代码审查中验证deleted字段来源是否符合规范"
keywords:
    - "deleted"
    - "delete_flag"
    - "字段映射"
---

项目中`deleted`字段统一从数据库字段`delete_flag`（对应方法`getDeleteFlag()`）获取，若为空则默认值为0。
