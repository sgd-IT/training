---
title: "移动端全局UI优化：NavBar居中标题、告警中心重构与我的页面精简"
usage_scenario:
    - "当需要回顾本次移动端UI优化的详细变更内容时"
    - "当需复现或参考移动端UI一致性改造规范时"
keywords:
    - "移动端UI"
    - "NavBar居中"
    - "告警中心重构"
    - "我的页面精简"
    - "微信授权登录"
source: "auto"
---

## Task description
- Core requirement: 全面优化移动端UI体验
- Task background: 用户要求提交移动端git，经检查发现本次修改涉及多个页面的UI一致性提升和功能精简，包括NavBar标题对齐方式、告警中心布局、首页KPI卡片样式、登录页错误处理、个人中心菜单结构等

## Execution process
1. components/NavBar/NavBar.vue: 将标题字体大小从32rpx调整为38rpx，字重从600调整为700，实现更醒目的视觉效果
2. pages/alarm-center/index.vue: 重构告警中心页面，将NavBar标题模式改为居中对齐，移除搜索栏和分类筛选功能，重新设计告警卡片样式（使用px单位替代rpx），新增状态徽章和分页提示
3. pages/index/index.vue: 调整首页企业名称字体大小并添加溢出截断处理，将KPI卡片背景色改为白色并添加阴影效果，优化告警概览卡片样式
4. pages/login/index.vue: 优化微信授权登录流程，增加loading态文字提示，改进授权拒绝时的错误提示，实现tenantId持久化存储，提升错误信息友好度
5. pages/profile/device-list.vue: 将NavBar标题模式改为居中对齐
6. pages/profile/index.vue: 将NavBar标题模式改为居中对齐，移除第一个冗余菜单组，新增退出登录按钮，将“人员权限”菜单项替换为“问题反馈”
7. pages/stats/index.vue: 将NavBar标题模式改为居中对齐

## Related files
- components/NavBar/NavBar.vue
- pages/alarm-center/index.vue
- pages/index/index.vue
- pages/login/index.vue
- pages/profile/device-list.vue
- pages/profile/index.vue
- pages/stats/index.vue

## Notes
- None

## Task overview
成功完成移动端全局UI优化：统一所有页面NavBar标题为居中对齐；重构告警中心页面，简化交互并提升视觉一致性；优化首页KPI卡片样式；增强登录页错误处理能力；精简个人中心菜单结构并新增退出登录功能。
