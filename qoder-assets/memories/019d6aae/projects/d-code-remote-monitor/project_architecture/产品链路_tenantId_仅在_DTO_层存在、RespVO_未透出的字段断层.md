---
title: "产品链路 tenantId 仅在 DTO 层存在、RespVO 未透出的字段断层"
usage_scenario:
    - "需要按产品所属租户驱动前端后续加载时"
    - "排查接口响应缺少租户字段的原因"
    - "判断DTO是否已有tenantId字段时"
keywords:
    - "ProductRespVO"
    - "TenantModel"
    - "tenantId未透出"
    - "BeanUtils同名拷贝"
    - "产品租户推导设备租户"
source: "auto"
---

eiot 产品链路的租户字段只在部分层透出：module-eiot-api 的 `com.enjoyiot.module.eiot.api.product.dto.Product` 继承 `com.enjoyiot.module.eiot.api.TenantModel`（含 tenantId），ProductDO 继承 TenantBaseDO，但 controller/admin/product/vo/ProductRespVO.java **没有** tenantId 字段，导致 BeanUtils.toBean 转换后前端拿不到产品所属租户（前端 src/api/eiot/product/index.ts 的 ProductVO 同样缺该字段）。因此凡是需要"按产品租户"决定后续行为的场景（如新增设备时按 product.tenantId 加载该租户的空间下拉，因设备租户=产品租户），必须先给 RespVO 补 tenantId 字段并同步前端 VO，再改消费逻辑；补字段无需写映射代码，BeanUtils 按同名拷贝。另注意：TenantModel 是 module-eiot-api 里的租户基类，判断某个 DTO 是否本就有 tenantId 要看它的 extends 链，而不是只看自身字段声明。
