---
title: "视频公共摄像头跨租户并集查询的结构事实与PC端缺口"
usage_scenario:
    - "为摄像头列表与取流接口补跨租户公共设备放行"
    - "排查普通租户看不到或播不了公共摄像头"
    - "评估PC端是否需同步支持公共摄像头"
keywords:
    - "公共摄像头租户级判定"
    - "getAppPage跨租户并集"
    - "executeIgnoreWithResult"
    - "数据权限isIgnore放行"
    - "PC端getPage未覆盖"
source: "auto"
---

消防远程值守后端仓库（anwei-fire-remote-monitor）的小程序端已实现"公共摄像头"跨租户可见能力（截至最近一次改造，待部署生效）：VideoCameraService 定义常量 PUBLIC_CAMERA_TENANT_ID = 166（识别口径为"租户"而非设备编码——166 租户下所有摄像头自动视为公共），并声明 getAppPage；VideoCameraServiceImpl.getAppPage 先取本租户可见列表，再用 TenantUtils.executeIgnoreWithResult 跨租户补查 tenantId=166 的记录，合并后按 deviceId#channelId 去重（优先保留 166 公共记录），最后内存分页；AppVideoCameraController 列表走 getAppPage，getLiveUrl 对普通租户 getById 拿不到的记录用 executeIgnoreWithResult 重取、仅当 shared.tenantId==PUBLIC_CAMERA_TENANT_ID 才放行，避免越权。注意框架忽略租户的方法名是 TenantUtils.executeIgnoreWithResult（历史文档误写作 executeIgnoreResult）。跨租户补查之所以不被二次拦截：TenantAreaTenantIdDataPermissionRule 在 TenantContextHolder.isIgnore() 为 true 时直接 return null 放行。仍未覆盖的结构性缺口：PC 企业端 controller/admin/video/VideoCameraController.getPage 与 getLiveUrl 仍走原租户过滤模板（currentTenantId==1 才忽略租户），不含公共摄像头并集，如需支持要另行处理编辑/删除越权。admin 与 app 两条取流链路历史上采用相同读取模板、camera==null 即抛"摄像头不存在"，app 侧现已补兜底、admin 侧未补。
