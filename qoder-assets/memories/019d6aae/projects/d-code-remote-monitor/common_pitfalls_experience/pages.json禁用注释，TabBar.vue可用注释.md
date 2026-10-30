---
title: "pages.json禁用注释，TabBar.vue可用//注释"
usage_scenario:
    - "在pages.json中添加注释后IDE报JSON语法错误"
    - "需要临时禁用uni-app页面路由但避免破坏JSON格式"
    - "区分uni-app不同配置文件对注释的支持差异"
keywords:
    - "pages.json"
    - "JSON注释"
    - "uni-app"
    - "TabBar.vue"
source: "auto"
---

uni-app 的 pages.json 文件虽被框架运行时解析支持 `//` 和 `/* */` 注释，但其本质是标准 JSON，所有 JSON 校验器（如 IDE、linter）会因注释报错：`JSON 中不允许有注释`。正确做法：pages.json 中禁止任何形式注释，需临时禁用路由时应直接删除对应条目；而 Vue/JS/TS 文件（如 TabBar.vue）可安全使用 `//` 行注释。（来源：SearchReplace, Read）
