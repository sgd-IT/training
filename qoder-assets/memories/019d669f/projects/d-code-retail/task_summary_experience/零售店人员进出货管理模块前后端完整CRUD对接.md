---
title: "零售店/人员/进出货管理模块前后端完整CRUD对接"
usage_scenario:
    - "当需要为 fireworks 模块中多个业务实体（零售店、人员、进出货）统一实现完整前后端 CRUD 对接时"
    - "当数据库菜单 component 路径与前端页面路径需严格对齐时"
keywords:
    - "retail"
    - "person"
    - "inventory"
    - "CRUD"
    - "对接"
    - "fireworks:retail:list"
    - "fireworks:person:list"
    - "fireworks:inventory:list"
source: "auto"
---

## Task description
- Core requirement: implement full frontend-backend CRUD integration for three modules: "零售店管理", "人员管理", and "进出货管理", ensuring strict alignment on RESTful path, permission identifiers, data isolation, and field-level validation.
- Task background: backend controllers exist at `/fireworks/retail`, `/fireworks/person`, `/fireworks/inventory`; database menus define component paths as `store/retailInfo/index`, `store/person/index`, `store/inventory/index`; frontend had no corresponding API or page files; backend data isolation was incomplete (only list queries enforced scope, single-item operations did not).

## Execution process
1. Backend: added data scope validation to RetailInfoServiceImpl, PersonInfoServiceImpl, and InventoryRecordServiceImpl to enforce retailId ownership for queryById, insert, update, and delete operations.
2. Frontend: created API layer (types.ts + index.ts) for all three modules under `@/api/fireworks/{retail|person|inventory}`.
3. Frontend: created pages under `@/views/store/{retailInfo|person|inventory}/index.vue` to match database menu component paths.
4. Retail page: implemented province/city/district cascader using `/fireworks/area/tree`; used ImageUpload for license and schematic fields.
5. Person page: integrated `useDict('fireworks_person_type')` for person type options; implemented conditional certificate fields based on `isCertified`.
6. Inventory page: implemented type radio (1=进货/2=出货); dynamically updated supplier label; enforced positive drugWeight and quantity.

## Related files
- d:\code\retail\anwei-fireworks-retail\ruoyi-modules\ruoyi-fireworks\src\main\java\org\dromara\fireworks\service\impl\RetailInfoServiceImpl.java
- d:\code\retail\anwei-fireworks-retail\ruoyi-modules\ruoyi-fireworks\src\main\java\org\dromara\fireworks\service\impl\PersonInfoServiceImpl.java
- d:\code\retail\anwei-fireworks-retail\ruoyi-modules\ruoyi-fireworks\src\main\java\org\dromara\fireworks\service\impl\InventoryRecordServiceImpl.java
- d:\code\retail\anwei-fireworks-retail-admin\src\api\fireworks\retail\types.ts
- d:\code\retail\anwei-fireworks-retail-admin\src\api\fireworks\retail\index.ts
- d:\code\retail\anwei-fireworks-retail-admin\src\api\fireworks\person\types.ts
- d:\code\retail\anwei-fireworks-retail-admin\src\api\fireworks\person\index.ts
- d:\code\retail\anwei-fireworks-retail-admin\src\api\fireworks\inventory\types.ts
- d:\code\retail\anwei-fireworks-retail-admin\src\api\fireworks\inventory\index.ts
- d:\code\retail\anwei-fireworks-retail-admin\src\views\store\retailInfo\index.vue
- d:\code\retail\anwei-fireworks-retail-admin\src\views\store\person\index.vue
- d:\code\retail\anwei-fireworks-retail-admin\src\views\store\inventory\index.vue

## Notes
- None

## Task overview
Successfully completed full CRUD implementation for retail/person/inventory modules: backend data isolation gaps fixed across all three services; frontend APIs and pages created with correct paths (`store/retailInfo/index`, `store/person/index`, `store/inventory/index`) matching database menus; all pages compile and pass type checking; no runtime errors detected.

## Notes
- The RetailInfo entity class maps to the database table `fireworks_retail_info`
