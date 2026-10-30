---
title: "anwei-fireworks-retail-admin 前端深度优化任务执行归档"
usage_scenario:
    - "When performing a comprehensive UI/UX overhaul on a Vue3 + Element Plus + UnoCSS frontend project"
    - "When removing AI-flavored design elements and establishing a domain-appropriate color system for a safety-monitoring platform"
keywords:
    - "UI/UX optimization"
    - "color system"
    - "login page redesign"
    - "dead code removal"
    - "anwei-fireworks-retail-admin"
source: "auto"
---

## Task description
- Core requirement: 对 anwei-fireworks-retail-admin 前端项目进行深度优化，重点是 UI/UX 视觉升级（去 AI 味、重构色彩体系、登录页重设计）、代码复用分析与清理、架构与性能建议
- Task background: 项目基于 Vue3 + Element Plus + UnoCSS；UI 存在明显模板味（#409EFF 蓝、玻璃拟态、粒子动画、unsplash 外链图）；utils/index.ts 是 jQuery 时代遗留死代码；login.vue 包含未使用的 social login 和粒子逻辑；色彩体系未贴合烟花爆竹安监平台的监管属性

## Execution process
1. settings.ts: 主色 theme 从 #409EFF 改为 #1F4E8C（藏青），圆角 radiusBase 从 14 改为 6
2. _theme.scss: 全局替换所有 rgba(64,158,255,...) 为藏青系变量；页面底色设为 #f2f4f7；新增压低明度的语义色（success #2e7d46 / warning #c26a0a / danger #b5332e / info #5b6b7c）
3. login.vue: 重写 template 为分屏布局（左侧纯色藏青品牌区 + 右侧浅灰表单区）；删除 doSocialLogin、particleStyle、默认凭据、unsplash 外链图；重写 style 为平色无渐变无特效
4. uno.config.ts: 删除空的 presetWebFonts({fonts:{}}) 配置及 import
5. DeleteFile: 删除 src/utils/index.ts 整个文件（grep 全项目确认无引用）
6. Bash: 将 .qoder/rules/07-frontend-reference.md 复制到本项目 .qoder/rules/ 目录下

## Related files
- /src/settings.ts
- /src/assets/styles/tokens/_theme.scss
- /src/views/login.vue
- /uno.config.ts
- /src/utils/index.ts
- /.qoder/rules/07-frontend-reference.md

## Notes
- None

## Task overview
Successfully completed the deep frontend optimization: replaced the template blue with a regulatory-grade deep navy color system; completely rewrote the login page to eliminate AI aesthetics (glassmorphism, particles, external image); deleted the entire dead utils/index.ts file; copied frontend reference rules into the project; all changes are verified and ready for deployment.
