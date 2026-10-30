---
title: "数据中心模块ECharts支持与mock兜底实现"
usage_scenario:
    - "新增或改造数据可视化模块时校验并补全ECharts插件注册"
    - "后端接口未就绪阶段，为前端图表提供可运行的 mock 支持方案"
    - "排查图表渲染失败时，优先检查插件注册完整性与模板合法性"
keywords:
    - "ECharts"
    - "mock兜底"
    - "数据中心"
source: "auto"
---

## 任务描述
为数据中心模块完善 ECharts 图表支持并实现 API 空数据时的 mock 兜底机制。

## 执行过程
```mermaid
graph TD
    A[需求:图表可用+mock兜底] --> B[校验ECharts插件注册]
    B --> C[发现TreemapChart缺失→补全index.ts]
    C --> D[清理index.vue中非法<script>标签]
    D --> E[创建统一mock.ts文件]
    E --> F[在AlarmAnalysis.vue中导入mock并添加fallback]
    F --> G[在DeviceAnalysis.vue中导入mock并添加fallback]
    G --> H[在DutyAnalysis.vue中导入mock并添加fallback]
    H --> I[在PatrolAnalysis.vue中导入mock并添加fallback]
    I --> J[在WorkOrderAnalysis.vue中导入mock并添加fallback]
    J --> K[ESLint验证全部通过]
```

## 任务总结
成功构建数据中心 mock 体系：1）修复 ECharts Treemap 注册缺失与模板非法 script 标签；2）创建结构化 mock.ts（覆盖告警/设备/值班/巡检/工单5大维度）；3）5个分析组件均实现 'API返回空则自动切换mock' 的健壮 fallback 逻辑；4）零 ESLint 错误。
