---
title: "缩放舞台内的下拉浮层必须关闭 teleport 否则样式与位置错位"
usage_scenario:
    - "大屏缩放页面里下拉框样式或位置异常时"
    - "为定尺寸舞台页面接入弹层类组件时"
    - "覆写第三方组件浮层主题不生效时"
keywords:
    - "teleported false"
    - "缩放舞台浮层错位"
    - "el-select 深色主题"
    - "scoped deep 命中不到"
    - "大屏自适应"
source: "auto"
---

Bug class：定尺寸缩放舞台（transform: scale 的整屏大屏/海报页）内使用会 teleport 到 body 的下拉、弹窗类浮层组件，导致浮层样式与位置错乱。

Root cause：这类页面把内容放在一个固定设计尺寸（如 1920×1080）的舞台容器里，靠外层 transform: scale(sx, sy) 整体缩放铺满视口。而 Element Plus 的 el-select / el-dropdown / el-popover 默认把下拉面板 append 到 document.body，脱离舞台节点，既不吃舞台的 scale，也不吃 scoped 样式（:deep 选择器命中不到挂在 body 上的面板），于是出现面板字号不随屏缩放、位置偏移、主题回到浅色默认样式。

Fix pattern：给浮层组件显式关闭 teleport（el-select 传 :teleported="false"），让面板渲染在组件自身 DOM 位置内，从而继承舞台的 transform 与 scoped 样式，再用 :deep(.el-select-dropdown) / :deep(.el-select-dropdown__item.is-selected) 等覆写深色主题；改前先确认依赖版本支持该属性（本项目 element-plus 2.14.3 支持，用 Grep 查 package.json 而非凭印象）。

Reusable lesson：不要在缩放舞台页面里按默认配置直接使用会 teleport 的浮层组件，因为面板会跳出舞台坐标系造成缩放与样式双重错位、且反复调 CSS 变量也无法修正；应第一时间关闭 teleport 再写主题覆写。适用于整页用 transform: scale 做自适应的定尺寸大屏、投屏、海报预览类页面；不适用于常规滚动布局的后台页面（那些场景保留 teleport 才能正确避开容器 overflow 裁切）。
