---
title: "移动端接口准入：平台位校验替代菜单权限点且 App 控制器直调 Service"
usage_scenario:
    - "新增或修改后端App接口的@PreAuthorize注解时"
    - "排查/app-api接口403拒绝问题"
    - "调整user_type平台位门禁口径"
keywords:
    - "app-api准入"
    - "hasMobilePlatformAccess"
    - "平台位掩码"
    - "移动端账号"
    - "菜单权限点"
source: "auto"
---

Conclusion: /app-api 移动端业务接口的权限口径改为“账号允许移动端即可访问”，即 @PreAuthorize("@ss.hasMobilePlatformAccess()")——按 system_users.user_type 平台位判定（含移动位放行，即 2/3；null 按 PC 兜底），不再要求菜单权限点；配套把 module-eiot 的 15 个 App 控制器全部由“注入 admin 控制器转发”改为直调 Service。
Rationale (trade-off): 移动端账号在其角色里没有全部菜单权限点记录，沿用 admin 权限串会大面积 403；平台位口径与登录门禁（checkLoginPlatform）一致，语义为“能登小程序就能用小程序接口”；直调 Service 可规避方法级安全下经 admin 代理的二次校验（另见对应陷阱记忆）。代价：每次校验多一次 system_users 主键查询（无缓存）；维护保养仪表板与大屏统计等少量聚合逻辑在 App/Admin 两侧成为两份拷贝，需同步维护。
Rejected alternatives: 保留 admin 控制器委托仅改注解——无效，代理仍二次拦截；为移动端角色补建全部菜单权限 SQL——配置面大且逐租户维护；仅删注解让任意登录用户可用——未按 user_type 2/3 收敛。
Applicable / expiry conditions: 适用于本项目 /app-api 全部业务接口；校验实现位于 SecurityFrameworkServiceImpl（ss Bean），依赖 AdminUserRespDTO 的 userType 字段透出。若未来引入独立移动端账号体系、会员体系，或需要更细的移动端权限点，需重估该口径。auth 三端点保持 @PermitAll；video 两接口与 system profile 维持“登录即可访问”未加平台位校验。
