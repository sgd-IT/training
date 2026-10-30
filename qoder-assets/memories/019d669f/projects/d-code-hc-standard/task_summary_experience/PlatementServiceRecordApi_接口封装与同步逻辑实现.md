---
title: "PlatementServiceRecordApi 接口封装与同步逻辑实现"
usage_scenario:
    - "When implementing a new Platement sub-feature requiring platform synchronization"
    - "When adding a new CRUD module following the existing duty schedule pattern"
keywords:
    - "PlatementServiceRecordApi"
    - "service record"
    - "platform synchronization"
    - "PlatementValidator"
    - "dynamic attachment form"
source: "auto"
---

## Task description
- Core requirement: implement the OpenApiConstant + PlatementServiceRecordApi interface encapsulation, followed by backend CRUD + synchronization logic (mirroring the duty schedule pattern), and finally the PC page
- Task background: the project already has a mature Platement module with established patterns (e.g., PlatementValidator for validation, PlatementBaseApi for API calls); the new ServiceRecord feature must follow these conventions; the backend must support local database storage and platform synchronization via a dedicated API

## Execution process
1. OpenApiConstant.java: add the new service record save endpoint constant
2. ServiceRecordDto.java: create the 16-field DTO to match the platform's API specification
3. PlatementServiceRecordApi.java: implement the save() method, which performs validation before calling the platform API
4. PlatementValidator.java: add validateServiceRecord() to enforce mandatory fields and type-specific required attachments (0→construction plans/emergency plans; 1→service contracts/service reports; 2→repair contracts/repair records)
5. BaseServiceRecord.java: define the domain entity with Excel annotations and @NotBlank constraints on thirdPartyUnitId and serviceTarget
6. BaseServiceRecordMapper.java & Mapper.xml: implement the database access layer with LEFT JOIN to fetch contractor names
7. BaseServiceRecordServiceImpl.java: implement the business logic, including UUID generation, enterprise code auto-fill, and the core sync() method that uploads attachments to the platform, exchanges them for fileIds, and updates the local sync_status/sync_result
8. ServiceRecordController.java: expose the REST endpoints (/base/serviceRecord) for list/getInfo/add/edit/remove/sync/syncBatch/export
9. serviceRecord.js & index.vue: implement the frontend API and the dynamic form that changes required attachment fields based on the selected service type
10. Menu SQL: generate and execute SQL to insert the '服务记录' menu and its four permission buttons into both the test and development databases
11. Added OpenApiConstant.ServiceRecord.update constant and PlatementServiceRecordApi.update() method with shared validation
12. Added synced column to base_service_record table on both 220.203 and 250.160 databases using mysql.exe and alter_synced.sql
13. Updated BaseServiceRecord domain, Mapper XML (resultMap, insert, update), and BaseServiceRecordServiceImpl.updateSyncResult() to support synced field
14. Corrected historical data: UPDATE base_service_record SET synced='1' WHERE sync_status='1' on both databases
15. Verified column existence and comment via information_schema.columns query
16. Cleaned up temporary file/alter_synced.sql
17. Maven compiled successfully (mvn_exit:0)

## Related files
- /ruoyi-common/src/main/java/com/ruoyi/common/platement/OpenApiConstant.java
- /ruoyi-common/src/main/java/com/ruoyi/common/platement/entity/ServiceRecordDto.java
- /ruoyi-common/src/main/java/com/ruoyi/common/platement/api/PlatementServiceRecordApi.java
- /ruoyi-common/src/main/java/com/ruoyi/common/platement/validator/PlatementValidator.java
- /ruoyi-system/src/main/java/com/ruoyi/base/domain/BaseServiceRecord.java
- /ruoyi-system/src/main/java/com/ruoyi/base/mapper/BaseServiceRecordMapper.java
- /ruoyi-system/src/main/resources/mapper/base/BaseServiceRecordMapper.xml
- /ruoyi-system/src/main/java/com/ruoyi/base/service/impl/BaseServiceRecordServiceImpl.java
- /ruoyi-admin/src/main/java/com/ruoyi/web/controller/base/ServiceRecordController.java
- /ruoyi-ui/src/api/base/serviceRecord.js
- /ruoyi-ui/src/views/base/serviceRecord/index.vue
- file/alter_synced.sql

## Notes
- None

## Task overview
Successfully implemented the full PlatementServiceRecordApi feature: completed all three phases — interface encapsulation, backend CRUD + synchronization logic, and PC page — and configured the corresponding menus and permissions in both the test and development databases. Maven compilation passed, and the new class files were generated. The save/update interface distinction logic was fully implemented and verified across both database environments, with historical data corrected and temporary artifacts cleaned.

## Notes
- 上述第 3、7 步的代码形态已在后续冗余重构中变更（2026-09-23 实测）：PlatementServiceRecordApi 的 save/update 不再是两个独立方法体，而是各自一行转发到私有 post(entity, url)，校验仍统一走 PlatementValidator.validateServiceRecord；delete 保持独立并走 validateServiceRecordDelete。
- BaseServiceRecordServiceImpl 不再保留手写的 uploadFiles + toUploadPath（约 28 行重实现），改为复用 PlatementFileApi.uploadLocalFiles：图片类扩展名走 type=1 通道、其余文档走 type=2 通道，并新增本地上传目录路径合法性校验（无效路径直接报"附件路径无效"）。删除平台侧记录的路径改为复用 PlatementBaseApi.requireSuccess。
- sync() 中"结果为空或 code!=200"的手写判断被有意保留未替换为 requireSuccess，因为需要把平台 msg 回写到 sync_result 后再抛异常；后续优化不要再试图用 requireSuccess 简化这一处。
- PlatementValidator.validateServiceRecord 已由约 111 行手写 if-throw 压缩为基于 requireText/maxLength 等 helper 的实现（约 54 行），但全部错误消息文案与服务类型对应必传附件的口径保持不变。
