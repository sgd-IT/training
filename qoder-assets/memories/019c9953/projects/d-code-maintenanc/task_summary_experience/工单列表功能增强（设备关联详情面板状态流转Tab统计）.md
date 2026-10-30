---
title: "工单列表功能增强（设备关联/详情面板/状态流转/Tab统计）"
usage_scenario:
    - "维修工单模块新增设备关联与状态机功能时参考整体改造流程"
    - "排查工单列表负责人名为空或Tab数量不更新问题时定位fillNames与statusCount实现"
    - "同类业务模块（如巡检单、维保单）需要状态流转与数量统计时复用STATUS_FLOW与groupBy模式"
keywords:
    - "工单列表"
    - "状态机"
    - "设备关联"
    - "Tab统计"
---

## 任务描述
增强工单列表功能，实现设备表关联、6Tab工单详情抽屉、状态机驱动的状态流转、各状态Tab数量统计。

## 执行过程
```mermaid
graph TD
    A[需求:工单功能增强] --> B[分析现有代码与问题]
    B --> C[发现负责人名为空、设备未关联]
    C --> D[确认字段翻译机制与ServiceException用法]
    D --> E[后端新增状态机changeStatus接口及groupBy统计]
    E --> F[前端API层新增statusCount/changeStatus方法]
    F --> G[前端页面集成设备下拉联动、6Tab详情抽屉、状态按钮动态渲染、Tab数量标签]
    G --> H[修复TS类型错误：el-button type联合类型声明]
    H --> I[全量落盘验证编译]
```

## 任务总结
成功交付4项增强：① 设备选择改为先选客户再加载设备下拉，自动带出设备名/位置；② 新增el-drawer 6Tab详情面板（2个真实Tab+4个占位Tab）；③ 后端STATUS_FLOW Map定义0→1→2→3状态机，前端按当前状态动态显示「处理/完成/验收」按钮；④ 后端queryStatusCount groupBy统计+前端tabCount动态渲染，增删/流转后自动刷新。
