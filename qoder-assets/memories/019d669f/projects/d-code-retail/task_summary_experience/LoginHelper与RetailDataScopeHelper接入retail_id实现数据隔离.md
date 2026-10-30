---
title: "LoginHelper与RetailDataScopeHelper接入retail_id实现数据隔离"
usage_scenario:
    - "当需要为用户显式绑定一个业务实体（如零售店、仓库、区域）以实现精准数据隔离时"
    - "当现有基于部门的数据隔离模型无法满足多对一或一对多的业务关系时"
keywords:
    - "LoginHelper"
    - "RetailDataScopeHelper"
    - "retail_id"
    - "data isolation"
    - "login chain"
source: "auto"
---

## Task description
- Core requirement: 接登录链路（LoginHelper/RetailDataScopeHelper 改读 retail_id）
- Task background: 零售店与部门ID在数据库中无物理关联，仅靠'dept_id == retail_id'的隐式约定导致数据隔离失效；sys_user表已添加retail_id字段，现需将该字段贯穿登录链路，使LoginHelper和RetailDataScopeHelper优先读取retail_id进行权限控制

## Execution process
1. LoginUser.java：添加private String retailId;字段，用于在sa-token会话中序列化存储
2. SysUserVo.java：添加private String retailId;字段，MyBatis自动映射retail_id列
3. SysLoginService.buildLoginUser：在构建LoginUser时注入user.getRetailId()
4. LoginHelper.java：添加RETAIL_KEY常量、login()方法中setExtra(RETAIL_KEY, loginUser.getRetailId())、getRetailId()方法读取extra
5. RetailDataScopeHelper.java：改写getAccessibleRetailIds()逻辑，优先读取LoginHelper.getRetailId()，超管返回null，未绑定用户返回空列表

## Related files
- d:\code\retail\anwei-fireworks-retail\ruoyi-api\src\main\java\org\dromara\system\api\model\LoginUser.java
- d:\code\retail\anwei-fireworks-retail\ruoyi-modules\ruoyi-system\src\main\java\org\dromara\system\domain\vo\SysUserVo.java
- d:\code\retail\anwei-fireworks-retail\ruoyi-admin\src\main\java\org\dromara\web\service\SysLoginService.java
- d:\code\retail\anwei-fireworks-retail\ruoyi-common\ruoyi-common-satoken\src\main\java\org\dromara\common\satoken\utils\LoginHelper.java
- d:\code\retail\anwei-fireworks-retail\ruoyi-modules\ruoyi-fireworks\src\main\java\org\dromara\fireworks\utils\RetailDataScopeHelper.java

## Notes
- 已登录的旧token不包含retailId extra，需重新登录才能生效；RetailDataScopeHelper不再回退deptId，数据权限完全由retail_id决定

## Task overview
Successfully connected the login chain to use retail_id for data isolation: added retailId field to LoginUser and SysUserVo; injected it during login in SysLoginService; stored and retrieved it via LoginHelper's extra mechanism; and updated RetailDataScopeHelper to exclusively use retailId for scope resolution. The change is complete and ready for testing.
