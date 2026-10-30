---
title: "Mobile-end account 403 error root cause analysis and resolution"
usage_scenario:
    - "Diagnosing 'no permission for this operation' errors in multi-tenant systems"
    - "Understanding tenant-isolated role assignment in RBAC models"
    - "Troubleshooting MCP service availability and fallback workflows"
keywords:
    - "mobile-end 403"
    - "cross-tenant role assignment"
    - "tenant-isolated system_user_role"
    - "MCP service discovery"
    - "mysql CLI fallback"
source: "auto"
---

## Task description
- Core requirement: diagnose why a mobile-end account ("demo account") assigned the "super_admin" role still displays "no permission for this operation" on all pages
- Task background: the account is assigned to tenant 166 ("Guangdong Anwei Safety Technology Co., Ltd.") and has the super_admin role; the diagnosis involves verifying multi-tenancy isolation, role assignment mechanisms, and permission point validation logic

## Execution process
1. Verified that the account's tenant_id=166 via system_users table query, confirming it belongs to the enterprise tenant rather than the platform tenant (tenant_id=1)
2. Confirmed that system_user_role table contains a tenant_id column and is not in the tenant.ignore-tables list, meaning role queries are tenant-isolated — assigning a role from tenant 1 to a user in tenant 166 results in no role being found during login
3. Identified the root cause: the "super_admin" role is defined under tenant 1; when the user logs in with tenant_id=166, the system queries system_user_role with tenant_id=166 and finds zero roles, causing all @PreAuthorize checks to fail and return 403
4. Validated MCP service status: confirmed the active MySQL MCP instance is "anwei_inner_office" connecting to the anwei_fire_remote_monitor database, and documented its dynamic nature (service name may drift, requiring mcps directory listing + DATABASE() verification before use)
5. When MCP was unavailable, used mysql.exe CLI with verified credentials (192.168.250.160:3306, root/t@m7Iici2i) as a fallback for read-only queries, following the documented three pitfalls (long options, UTF-8 BOM for PowerShell scripts, avoiding cmd wrappers)

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor\server\src\main\resources\application.yaml
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-system\module-system-biz\src\main\java\com\enjoyiot\module\system\service\permission\PermissionServiceImpl.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\framework\spring-boot-starter-biz-tenant\src\main\java\com\enjoyiot\framework\tenant\core\db\TenantDatabaseInterceptor.java

## Notes
- The "super_admin" role is only effective for users belonging to tenant_id=1; assigning it to users of other tenants has no effect
- To grant full permissions, create the user under the platform tenant (tenant_id=1) and assign the super_admin role there
- MCP service names are not static; always verify the current active service by listing mcps directory and checking SERVER_METADATA.json before use

## Task overview
Successfully diagnosed the root cause of the mobile-end 403 error: cross-tenant role assignment failure due to tenant-isolated system_user_role queries; clarified that role assignment must match the user's tenant_id; documented the correct procedure for creating fully privileged accounts and the MCP service discovery workflow.
