---
title: "空间管理 /list 接口与设备地址下拉框实现进度"
usage_scenario:
    - "继续开发空间管理 update/delete/page 端点"
    - "联调设备地址与值班室点位下拉框数据源"
    - "排查前端空间下拉无数据或选项不显示"
keywords:
    - "FireSpaceController"
    - "space list API"
    - "spaceId关联"
    - "设备地址下拉框"
    - "值班室点位下拉框"
    - "el-select"
    - "tenantName填充"
    - "TenantDatabaseInterceptor"
source: "auto"
---

空间管理功能续建（承接创建链路后）：后端补 GET /eiot/firewatch/space/list（FireSpaceController/Service/ServiceImpl，权限 eiot:firewatch:space:query，Service 层显式过滤 + orderByAsc(id)）。前端 src/api/eiot/firewatch/space.api.ts（SpaceApi.getSpaceList），deviceinfo/detail.vue 设备地址为 el-select 绑 spaceId。

【2026-10-13 更新：space_id 外键】eiot_device_info 新增 space_id；DeviceInfoServiceImpl.initSpaceAddr 按设备租户查 FireSpaceDO 重拼 addr 快照；/list 加可选 tenantId：系统租户管理员（tenantId==1）不传查全部、传则按指定租户过滤；企业租户强制本租户。

【2026-10-13 空间管理页面与菜单落地】system_menu 3229「空间管理」挂 2814 设备中心（sort=1 紧贴设备管理 2827 后）；前端页面 src/views/eiot/space/index.vue（yt-crud：列表+新增+编辑+删除；pageHide 不分页；"所属企业"筛选下拉放新增按钮右侧 addRight 插槽（为 yt-table-fun/yt-crud 新增的扩展插槽）；列含详细位置 position）。菜单分配 6 角色（155,161,164,165,169,170）。

【2026-10-13 增删改落地】后端新增：FireSpaceUpdateDTO（id @NotNull + building/floor 必填）；Service/Controller 加 updateFireSpace(FireSpaceUpdateDTO)/deleteFireSpace(Long id)，端点 PUT /update、DELETE /delete?id=。关键决策：①删除走物理删除——eiot_space 唯一索引 uk_tenant_space(tenant_id,building,floor,area,position) 不含 deleted，软删会占位导致同组合无法重建，故 FireSpaceMapper 加 @Delete 物理删方法（先例 DeviceCameraRelationMapper）；②update 做存在校验+唯一键查重（排除自身，抛 SPACE_EXISTS）+与 create 同口径的 trim 归一（null→"" 全量覆盖语义，remark null 不更新）；③delete 做设备引用校验（EiotDeviceInfoMapper 查 space_id，命中抛新错误码 SPACE_USED_BY_DEVICE=2_006_007_202），防设备 addr 悬空后编辑设备报 SPACE_NOT_EXISTS。权限菜单 3230 空间更新/3231 空间删除（type=3 挂 2827），角色分配 155/170（与 create 同口径）。admin 跨租户可维护任意空间（租户拦截器对系统租户放行），企业租户自动限定本租户。新权限串无历史缓存故无需清 Redis。前端 SpaceApi.updateSpace(PUT)/deleteSpace(params id)；表格开 edit/del 按钮，tenantName 列 editHide（租户不可变），onSave 区分 add/update，onDelete 走 @del-fun。

【补充观察】设备地址 el-select 的使用场景不止设备详情页：值班室相关页面同样以 el-select 呈现空间/点位选项（如占位文案「值班室中间左」这类空间名），排查"下拉无数据/选中项不显示"时应同时确认所查空间的 tenantId 归属与该页面调用的接口口径是否一致。
