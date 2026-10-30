---
title: "多仓库分批次Git提交与推送流程"
usage_scenario:
    - "当需要将大型功能变更拆分为多个可审核、可回滚的Git提交时"
    - "当涉及多仓库协同开发，需保证各仓库提交节奏一致时"
keywords:
    - "Git提交"
    - "多仓库"
    - "分批次"
    - "Angular规范"
    - "推送"
source: "auto"
---

## Task description
- Core requirement: 将代码变更按逻辑分组，拆分为多个独立的Git提交批次，并分别推送到远程仓库
- Task background: 后端仓库(anwei-fire-maintenance)和前端仓库(anwei-fire-maintenance-admin)均存在大量变更；用户明确要求“分批次，不要堆一起”，强调提交的原子性和可读性；需确保每批提交内容内聚、语义清晰，并在用户确认后执行

## Execution process
1. 后端仓库第1批：精简维保计划领域模型字段（WbMaintenancePlan.java/Bo/Vo）
2. 后端仓库第2批：增强维保任务模型（WbTask.java/Bo/Vo），新增计划关联/设备/执行人/进度等字段
3. 后端仓库第3批：标准项模型扩展、计划-标准项关联实体、服务层增强及10个SQL数据库脚本
4. 前端仓库第4批：系统品牌定制，更新标题、Logo及配套样式文件
5. 前端仓库第5批：同步更新维保模块API类型定义（types.ts/index.ts）及ESLint配置
6. 前端仓库第6批：增强维保计划/任务/维修单/标准项页面及首页
7. 所有6批提交完成后，分别对两个仓库执行git push，推送成功

## Related files
- ruoyi-modules/ruoyi-weibao/src/main/java/org/dromara/weibao/domain/WbMaintenancePlan.java
- ruoyi-modules/ruoyi-weibao/src/main/java/org/dromara/weibao/domain/WbTask.java
- ruoyi-modules/ruoyi-weibao/src/main/java/org/dromara/weibao/domain/WbStandardItem.java
- .env.development
- src/api/weibao/maintenancePlan/types.ts
- src/views/weibao/maintenance/plan/index.vue

## Notes
- None

## Task overview
成功执行了跨双仓库的6批次Git提交与推送：后端3批（模型重构→模型增强→标准项+服务+SQL）、前端3批（品牌定制→API类型→页面增强），所有提交均严格遵循Angular规范并经用户确认，最终全部推送至远程master分支。
