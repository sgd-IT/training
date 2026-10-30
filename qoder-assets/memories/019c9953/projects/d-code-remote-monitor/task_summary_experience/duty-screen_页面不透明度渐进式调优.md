---
title: "duty-screen 页面不透明度渐进式调优"
usage_scenario:
    - "解决大屏/值守端页面地图或底图透出UI组件的问题"
    - "对Vue组件中多处rgba背景进行批量、比例化不透明度调整"
    - "深色主题UI中强化面板层级感与信息聚焦度"
keywords:
    - "duty-screen"
    - "不透明度"
    - "rgba"
    - "深色主题"
---

## 任务描述
在 duty-screen 页面中渐进式提高多个 UI 元素的背景不透明度，解决地图内容透过面板显示的问题。

## 执行过程
```mermaid
graph TD
    A[需求:提升duty-screen不透明度] --> B[Grep定位所有rgba透明度定义]
    B --> C[Read确认关键样式位置与上下文]
    C --> D[首轮SearchReplace：alpha ×1.3 计算并替换]
    D --> E[第二轮SearchReplace：对已升高的alpha再×1.3，header/panel-card达1.0]
    E --> F[验证修改后视觉效果：Header与卡片完全不透明，地图不再透出]
```

## 任务总结
成功完成两轮不透明度增强：header 和 panel-card 的渐变背景 alpha 值从 0.70/0.65/0.70 经两次 ×1.3 后升至 1.0（完全不透明）；ent-header-bar 及其 hover 状态 alpha 也同步提升，显著改善深色主题下地图与UI层叠的可读性。
