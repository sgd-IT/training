---
title: "Cross-tenant role assignment failure in multi-tenancy RBAC"
usage_scenario:
    - "When assigning roles to users in a multi-tenant RBAC system"
    - "When troubleshooting 'no permission for this operation' errors after role assignment"
keywords:
    - "cross-tenant role assignment"
    - "system_user_role tenant isolation"
    - "403 no permission"
    - "multi-tenant RBAC"
source: "auto"
---

Bug class: Cross-tenant role assignment failure in multi-tenancy RBAC
Root cause: The system_user_role table has a tenant_id column and is not in the tenant.ignore-tables list, so queries are tenant-isolated; assigning a role from tenant 1 to a user in tenant 166 results in zero roles being found during login.
Fix pattern: Assign roles only within the same tenant as the user's system_users.tenant_id; for full permissions, create the user under the platform tenant (tenant_id=1) and assign the super_admin role there.
Reusable lesson: Don't assign roles from one tenant to users of another tenant because it causes all @PreAuthorize checks to fail with 403 "no permission for this operation"; instead assign roles within the same tenant. Applies when managing roles in a multi-tenant RBAC system with tenant-isolated association tables; does not apply to non-tenant-isolated systems or single-tenant deployments.
