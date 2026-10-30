---
title: "移动端多页面图标统一优化：首页企业代办、我的页面与TabBar"
usage_scenario:
    - "When unifying icon usage across multiple mobile pages"
    - "When optimizing icon semantics for better user understanding"
keywords:
    - "uni-icons"
    - "mobile app"
    - "icon optimization"
    - "TabBar"
    - "profile page"
    - "home page"
source: "auto"
---

## Task description
- Core requirement: unify and optimize icons across multiple mobile pages — home page 'enterprise tasks', profile page menu items, and the bottom TabBar alarm tab
- Task background: the project uses uni-icons for most UI elements; the home page used letter icons (D/E/F/I/R/T), the profile page used generic icons (home/help/gear), and the TabBar alarm tab displayed an unwanted badge '3'; all needed semantic and visual consistency

## Execution process
1. pages/index/index.vue: replaced 6 letter icons in the 'enterprise tasks' section with semantic uni-icons (camera-filled, home-filled, navigate-filled, flag-filled, gear-filled, notification-filled)
2. pages/profile/index.vue: optimized 4 menu item icons for better semantic fit (map-filled for 'project switch', compose for 'feedback', gear-filled for 'settings', help-filled for 'help')
3. components/TabBar.vue: removed the badge '3' from the alarm tab by setting badge to 0, making it invisible per the v-if="t.badge" condition

## Related files
- /pages/index/index.vue
- /pages/profile/index.vue
- /components/TabBar.vue

## Notes
- None

## Task overview
Successfully unified and optimized icons across three key mobile pages: the home page's enterprise tasks now use semantic uni-icons; the profile page's menu items have more precise icon choices; the TabBar alarm tab no longer displays the unwanted badge '3'. All changes maintain visual consistency and improve user experience.
