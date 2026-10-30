---
title: "新增@PreAuthorize权限串未建菜单致403且空列表缓存需手动清除"
usage_scenario:
    - "非超管调用新接口返回403权限拒绝"
    - "补建权限菜单后仍被拒需清缓存"
    - "判定运行中后端进程是否旧编译代码"
keywords:
    - "PreAuthorize权限缺失"
    - "system_menu未建"
    - "permission_menu_ids空缓存"
    - "super_admin放行"
    - "服务未重启旧代码"
source: "auto"
---

Bug class: yudao 风格 @PreAuthorize 权限串未在 system_menu 建记录导致非超管一律 403，且空结果被 Redis 缓存造成"补了菜单仍不生效"的假象。

Root cause: SecurityFrameworkServiceImpl.hasPermission → PermissionServiceImpl.hasAnyPermissions 只有两条放行路径：(1) 权限串在 system_menu 有对应菜单且角色-菜单交集命中；(2) hasAnySuperAdmin 对 super_admin 角色 code 直接放行。若开发新接口时只写了 @PreAuthorize 注解而未插入权限菜单 SQL（本项目空间管理即如此遗留），则除超管外所有用户 403。同时 getMenuIdListByPermissionFromCache 用 @Cacheable(value=permission_menu_ids, key=#permission) 会把**空列表也缓存**（带 TTL），此后即使补建菜单，旧空缓存仍使校验持续失败。另一独立成因：IDEA 启动的服务进程不会自动装载事后编译的类（对比进程 StartTime 与 target/classes 的 LastWriteTime 可判定），方法签名变更无法热替换，必须重启。

Fix pattern: (1) INSERT system_menu（type=3 按钮权限，挂到功能页面菜单下）+ INSERT system_role_menu 分配（tenant_id 按角色自身租户填）；(2) redis-cli DEL "permission_menu_ids:<权限串>" 清掉空缓存（key 格式为缓存名单冒号权限串，db 见 application-dev.yaml）；(3) 重启后端服务；(4) 判据速查：Redis 里存在 permission_menu_ids:<x> 且值为 ["java.util.ArrayList",[]] 即证明该权限从未建过菜单且已被拒过一次。

Reusable lesson: Don't 认为新增 @PreAuthorize 接口后只需前端联调即可；instead 上线前必须核对权限串在 system_menu 是否有记录并同步清 permission_menu_ids 缓存。适用于本框架严格权限模式下的所有新端点（含 space/duty-phone 等 firewatch 扩展）；不适用于 super_admin 账号场景（其绕过菜单校验，只会暴露代码版本问题而非 403）。
