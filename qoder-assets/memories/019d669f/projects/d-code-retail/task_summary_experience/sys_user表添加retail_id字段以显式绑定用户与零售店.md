---
title: "sys_user表添加retail_id字段以显式绑定用户与零售店"
usage_scenario:
    - "当需要为用户显式绑定一个业务实体（如零售店、仓库、区域）以实现精准数据隔离时"
    - "当现有基于部门的数据隔离模型无法满足多对一或一对多的业务关系时"
keywords:
    - "sys_user"
    - "retail_id"
    - "data isolation"
    - "LoginHelper"
    - "RetailDataScopeHelper"
source: "auto"
---

## 背景
零售店与部门ID在数据库中无物理关联，仅靠'dept_id == retail_id'的隐式约定，导致数据隔离失效。本次改造通过在sys_user表添加retail_id字段，将绑定关系显式化。

## 实施步骤
1. 数据库：ALTER TABLE sys_user ADD COLUMN retail_id varchar(64) NULL COMMENT '零售店ID'（实际已执行成功）；
2. 后端实体：在SysUser、SysUserVo、SysUserBo、LoginUser中添加retailId字段；
3. 登录链路：LoginHelper在登录时将retail_id存入sa-token extra；SysLoginService.buildLoginUser将retailId注入LoginUser；
4. 数据隔离：RetailDataScopeHelper.getAccessibleRetailIds()改为优先读取LoginUser.retailId，超管返回null，无绑定用户返回空列表；
5. 前端：在用户管理页index.vue中添加零售店下拉选择器，使用listRetail API加载选项，并在编辑自己时清空retailId。

## 关键细节
- 全局Long序列化配置（BigNumberSerializer）确保retail_id在JSON传输中不失精度；
- 改造后，用户数据权限完全由retail_id决定，dept_id仅用于组织架构展示，解耦清晰。
- sys_user表是用户数据隔离策略的中心载体：通过添加retail_id等业务字段，并配合LoginHelper和RetailDataScopeHelper协同工作，实现灵活、精准的用户-业务实体绑定与数据权限控制；该设计解耦了组织架构（dept_id）与业务实体（retail_id），为未来扩展（如多店归属、跨部门协作）提供了坚实基础。
- 本方案是工程权衡的产物：初始依赖'dept_id == retail_id'的隐式约定因MyBatis-Plus ASSIGN_ID雪花生成器导致脱节；方案A（修改雪花ID）会破坏所有外键约束，成本极高；方案B（添加显式字段）零破坏现有数据、完全向后兼容，故被采纳；其失效条件是业务需求变为一个用户可绑定多个零售店，届时需升级为中间映射表。

## Notes（修正）
MCP DDL执行失败的根因已修正：@benborla29/mcp-server-mysql 的 DDL 权限变量是 ALLOW_DDL_OPERATION（不带库名前缀的SQL只认它），SCHEMA_DDL_PERMISSIONS 仅对带 schema 前缀的 SQL 生效；修改 mcp.json 后必须完全重启 Qoder 让 MCP 服务器进程重新加载环境变量，配置才生效。
