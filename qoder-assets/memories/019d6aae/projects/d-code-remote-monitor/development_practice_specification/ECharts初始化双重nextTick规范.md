---
title: "ECharts初始化双重nextTick规范"
usage_scenario:
    - "修复ECharts容器尺寸为0的报错"
    - "在含v-if/v-else的图表组件中初始化ECharts"
    - "排查图表不渲染或空白问题"
keywords:
    - "ECharts"
    - "nextTick"
    - "v-if"
    - "渲染时机"
    - "clientWidth"
source: "auto"
---

在Vue组件中初始化ECharts时，若图表容器位于v-if/v-else条件渲染的slot内，需执行双重await nextTick()：第一个等待父组件DOM更新，第二个等待子组件slot真实渲染完成，确保.chart-fill容器获得有效clientWidth/clientHeight。
