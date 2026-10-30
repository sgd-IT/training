---
title: "data-center多模块ECharts图表化统一改造"
usage_scenario:
    - "将任意文本列表卡片（如排名、趋势、分布）升级为ECharts图表时复用基础设施模板"
    - "在Vue组件中集成ECharts并确保resize响应与内存安全"
    - "处理多图表共存时的ref管理、实例生命周期和渲染时机问题"
keywords:
    - "ECharts"
    - "图表化"
    - "data-list替换"
source: "auto"
---

## 任务描述
将 data-center 各分析模块中残留的 data-list 文本卡片（物业告警排名、设备类型分布、各物业设备数量、值班人员排班排名、巡检/工单趋势）统一替换为 ECharts 图表。

## 执行过程
```mermaid
graph TD
    A[识别待改造卡片] --> B[并行读取5个Vue组件文件]
    B --> C[AlarmAnalysis：物业告警排名→横向柱状图]
    C --> D[DeviceAnalysis：设备类型分布→环形饼图 + 各物业设备数量→横向柱状图]
    D --> E[DutyAnalysis：值班人员排班排名→横向柱状图]
    E --> F[PatrolAnalysis：巡检趋势→堆叠折线图]
    F --> G[WorkOrderAnalysis：工单趋势→堆叠折线图]
    G --> H[每文件注入统一基础设施：registerRef/initChart/resize监听/onUnmounted清理]
    H --> I[每图表采用nextTick×2+setTimeout(100)确保容器尺寸就绪]
    I --> J[所有图表使用.chart-fill-XXX类名匹配Card高度]
```

## 任务总结
成功完成5模块图表化升级：建立可复用的 ECharts 基础设施模板（ref注册、实例管理、resize响应、自动销毁），并针对不同数据结构选用最优图表（横向柱状图用于排名、环形饼图用于分布、堆叠折线图用于双指标趋势），所有图表均支持响应式与动态更新。

## Notes
- 本记忆的 .chart-fill 类名 + registerRef/initChart 模板是历史快照：该批图表后因 Vue 3 slot 作用域容器尺寸问题被整体移除过一轮。
- 最新有效结论（数据中心重规划轮）：图表基础设施改由 useChart.ts composable 统一收口（resize 监听配对解绑、KeepAlive activated 时统一 resize、onUnmounted dispose），容器尺寸靠行内 style + flex 链每层 min-width:0/min-height:0 保证，弃用 nextTick×2+setTimeout(100) 这类经验性延时兜底。Tab 切换改用 KeepAlive+动态组件避免重复发请求。
