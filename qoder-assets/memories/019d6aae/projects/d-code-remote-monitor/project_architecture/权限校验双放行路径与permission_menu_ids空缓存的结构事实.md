---
title: "权限校验双放行路径与permission_menu_ids空缓存的结构事实"
usage_scenario:
    - "新增@PreAuthorize端点后规划上线配套动作"
    - "排查接口403或补菜单后仍被拒绝的问题"
    - "判断运行中后端是否为旧编译代码"
    - "实现仅超级管理员可见的新菜单页面"
keywords:
    - "hasAnyPermissions放行路径"
    - "permission_menu_ids缓存"
    - "system_menu权限菜单"
    - "IDEA旧代码判定"
    - "iot:device-info按钮权限"
    - "getRoleMenuListByRoleId超管全量"
source: "auto"
---

本仓库（anwei-fire-remote-monitor，yudao 风格框架）权限与运行时代码装载的已验证结构事实：

- 权限判定链：Controller 的 @PreAuthorize("@ss.hasPermission('x')") → SecurityFrameworkServiceImpl.hasPermission → PermissionServiceImpl.hasAnyPermissions，仅两条放行路径：(1) 权限串在 system_menu 有对应记录且用户角色经 system_role_menu 与该菜单有交集；(2) RoleServiceImpl.hasAnySuperAdmin 命中角色 code=super_admin 直接放行（无租户限制）。权限串在 system_menu 无记录时是严格拒绝而非默认放行——新增端点写完注解必须同步补菜单 SQL，否则除超管外全 403。
- 权限缓存不变量：MenuServiceImpl.getMenuIdListByPermissionFromCache 使用 @Cacheable(value=RedisKeyConstants.PERMISSION_MENU_ID_LIST)，Redis key 实测格式为 permission_menu_ids:<权限串>（db 见 application-dev.yaml，本机 localhost:6379 db 11），空 List 也会被缓存并带 TTL；补建菜单后必须手动 DEL 该 key，否则校验持续用旧空值。getRoleMenuListByRoleId 无缓存实时查库，新 menuId 的角色分配首次访问即生效。
- 运行代码版本判定：后端由 IDEA 启动 ServerApplication（javaagent idea_rt.jar），进程不会自动装载启动之后编译出的 class；对比进程 StartTime 与模块 target/classes 下 .class 的 LastWriteTime 即可判定运行的是否旧代码，方法签名变更必须重启。
- 顺带事实：eiot 设备详情页隶属"设备中心"菜单树（2814→2827 设备管理→2828/2829/2830 按钮权限 iot:device-info:*），页面菜单本身 permission 为空（登录即可见），编辑能力由按钮权限 2830 控制；system_role_menu.tenant_id 惯例填角色自身所属租户。

## Notes
- 【2026-10-19 实测补充】"仅超管可见"页面的最简做法已验证：新菜单只插 system_menu（type=2，permission 留空）即可，无需写任何 system_role_menu——PermissionServiceImpl 中账号持有 super_admin 角色时 getRoleMenuListByRoleId 走 hasAnySuperAdmin 分支直接返回 getMenuList() 全量菜单，超管自动可见；其他角色因无该菜单的角色关联而既看不到入口也过不了接口层。注意菜单有缓存/会话快照，超管需重新登录才能看到新入口。案例：基础设施→API 日志(id=1083)下的"使用统计"(id=3232)。
