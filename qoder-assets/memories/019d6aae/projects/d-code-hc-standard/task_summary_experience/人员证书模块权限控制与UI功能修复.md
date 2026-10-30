---
title: "人员证书模块权限控制与UI功能修复"
usage_scenario:
    - "当需要为人员证书模块添加或修改权限控制逻辑时"
    - "当需要修复人员证书录入功能的前端交互问题时"
    - "当排查人员证书相关按钮不可见或操作失败的根本原因时"
keywords:
    - "人员证书"
    - "权限控制"
    - "v-hasPermi"
    - "前端按钮"
    - "证书录入"
source: "auto"
---

## Task description
- Core requirement: 1) Support modification of personnel certificate information (Issue #14); 2) Fix personnel certificate entry functionality (Issue #15)
- Task background: The personnel certificate module had hard-coded role-based permissions (`v-hasRole=['admin']`) preventing non-admin users from editing; the certificate entry dialog lacked support for certain certificate types and had incorrect status handling; both issues blocked business workflow

## Execution process
1. Backend: Changed `@PreAuthorize("@ss.hasRole('admin')")` to `@PreAuthorize("@ss.hasPermi('base:contractorPeopleCert:edit')")` in BaseContractorPeopleCertController.java
2. Frontend toolbar: Uncommented the '修改' button and updated its `v-hasPermi` directive to match the new permission string
3. Frontend row actions: Replaced `v-hasRole="['admin']"` with `v-hasPermi="['base:contractorPeopleCert:edit']"` on the row-level edit button
4. Frontend query dialog: Uncommented the '添加证书' operation column button
5. Frontend handleAddCert: Extended certificate type mapping to include more emergency department codes and added `status: 1` to all entries
6. Frontend form validation: Made `filePath` non-required to support cases where no file upload is needed

## Related files
- /ruoyi-admin/src/main/java/com/ruoyi/web/controller/base/BaseContractorPeopleCertController.java
- /ruoyi-ui/src/views/base/contractorPeopleCert/index.vue

## Notes
- None

## Task overview
Successfully completed both personnel certificate tasks: 1) Enabled certificate editing for all users with the 'base:contractorPeopleCert:edit' permission by unifying backend and frontend permission checks; 2) Fixed certificate entry by expanding type support, adding status, and relaxing file upload requirements. All changes were verified and error-free.

## 补充说明（后续会话）
- 上述修复完成后，人员证书模块又出现新的功能缺陷：在"添加作业人员证件信息"流程中，一旦选择作业人即返回"数据异常"提示，导致证件信息无法录入。该问题涉及作业人选取到证件保存的链路（承包商人员/证件表关联），此前记录的权限与类型映射修复并未覆盖此场景，仍需单独定位。
