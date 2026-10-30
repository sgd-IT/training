---
title: "移动端首页header添加企业名称和用户名称显示"
usage_scenario:
    - "当需要在移动端首页.header区域添加企业与用户名称动态显示时复用此UI增强方案"
    - "当排查首页header信息显示异常时快速定位数据源与模板绑定逻辑"
keywords:
    - "移动端首页"
    - "header显示"
    - "企业名称"
    - "用户名称"
    - "profile接口"
source: "auto"
---

## Task description
- Core requirement: 在移动端首页.header区域（蓝色渐变背景）中添加企业名称和用户姓名的显示，布局与参考图一致
- Task background: 首页入口文件为 pages/index/index.vue；用户信息需从 /system/user/profile/get 接口获取，data.nickname 为用户姓名，data.dept.name 为企业名称；.header 原有蓝色渐变背景需保留并适配新内容

## Execution process
1. pages/index/index.vue：新增 userInfo、companyName、userName 响应式数据字段
2. pages/index/index.vue：新增 fetchProfile() 方法，调用 /system/user/profile/get 接口，从 data.nickname 取用户名，data.dept.name 取企业名称
3. pages/index/index.vue：在 .header 模板内新增 .header-company 和 .header-user 两个文本元素，分别绑定 companyName 和 userName
4. pages/index/index.vue：修改 .header 样式为 flex 纵向布局，设置 .header-company 为白色粗体 36rpx，.header-user 为半透明白色 28rpx
5. pages/index/index.vue：在 onLoad 生命周期中调用 fetchProfile() 方法

## Related files
- /pages/index/index.vue

## Notes
- None

## Task overview
成功完成移动端首页header的企业名称和用户名称显示功能：新增了用户信息获取逻辑、模板渲染和样式适配，确保文字清晰可见并与蓝色渐变背景协调。
