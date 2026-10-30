---
title: "RuoYi动态菜单需实查sys_menu及作业与承包商模块映射"
usage_scenario:
    - "用户询问某功能在PC端的菜单入口位置"
    - "需要确认路由与权限字符对应关系"
    - "清理sys_menu冗余或停用菜单项"
keywords:
    - "sys_menu实查方法"
    - "作业管理菜单"
    - "承包商管理菜单"
    - "隐藏路由映射"
    - "menu_id perms核对"
source: "auto"
---

项目 RuoYi 动态菜单纯在数据库 sys_menu 表（仓库内无菜单初始化 SQL），定位任何功能的真实入口必须实查 sys_menu：按 component/perms/menu_name 检索并 LEFT JOIN parent_id 取上级名称，逐项核对 menu_id、path、component、perms 四项，不能依据代码路由或功能树资料推断菜单名。

【作业管理】一级菜单（menu_id=2470，path=ticket）：
- 作业报备（2654，path=TicketBb → base/TicketBb/index）
- 特殊作业（2666，path=workSpecial → base/workSpecial/index，权限 base:workSpecial:list，路由 /ticket/workSpecial）— 票证台账/统计能力的唯一挂载点
- 票证视频（2684，path=workVideo → base/workVideo/index）
- 冗余遗留：作业申报-废（2524）、特殊作业-废（2390）仍为 visible=0 显示状态；一般作业（ticketYb）已停用

【企业管理】顶级菜单（path=cbs，即承包商管理模块）：
- 企业管理（path=contractor）：承包商企业列表与信息录入
- 人员管理（path=contractorPeople）：承包商人员信息录入
- 人员证书（path=contractorPeopleCert）：人员资质证书管理
- 项目管理（path=project）：项目信息管理
- 企业资质证书管理为隐藏路由（/base/contractorCert/index/:id），需从"企业管理"列表页点击"资质证书管理"按钮进入

注意：特殊作业页虽有新增/修改/删除/导出等按钮权限记录，但前端工具栏自初版起即为注释状态，实际可达能力与权限配置不一致（详见"票证统计无独立菜单且特殊作业页导出按钮全程注释的入口真相"）。
