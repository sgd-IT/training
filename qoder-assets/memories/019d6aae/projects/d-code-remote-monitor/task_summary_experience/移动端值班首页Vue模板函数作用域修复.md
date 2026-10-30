---
title: "移动端值班首页Vue模板函数作用域修复"
usage_scenario:
    - "UniApp 移动端 Options API 页面报模板函数未定义"
    - "向 export default 组件引入工具函数供模板使用"
    - "排查 pages/ 目录下 Vue2 风格组件作用域问题"
keywords:
    - "Vue Options API"
    - "template function scope"
    - "fmtTime methods暴露"
    - "UniApp pages组件"
    - "export default作用域"
source: "auto"
---

## Task description
- Core requirement: fix the runtime error `Property "fmtTime" was accessed during render but is not defined on instance` in `pages/duty/index.vue`
- Task background: the component uses Vue Options API (`export default { ... }`) and imports `fmtTime` from `@/utils/card`, but imported functions are not automatically available in the template; they must be explicitly exposed via `methods`

## Execution process
1. Identified the error message and location: `fmtTime(...)` used in template at lines 48/52, but function undefined on instance
2. Verified `fmtTime` is correctly imported at line 70 and exported from `@/utils/card`
3. Confirmed the root cause: Vue Options API requires imported utility functions to be explicitly added to `methods` to be accessible in the template
4. Applied fix: added `fmtTime` to the `methods` object in `pages/duty/index.vue`
5. Verified no other pages use `fmtTime` — confirmed via grep across `pages/`

## Related files
- /pages/duty/index.vue

## Notes
- None

## Task overview
Successfully fixed the Vue template scoping issue: added `fmtTime` to the `methods` object in `pages/duty/index.vue`, enabling its use in the template. The fix was verified and no sibling pages were found with the same pattern.
