---
title: "项目SVG资源路径与命名规范：大屏功能图标与通用组件图标"
usage_scenario:
    - "新增大屏看板功能时选择图标格式和存放路径"
    - "替换现有图标时确定文件命名和引用方式"
    - "维护UI一致性时检查SVG资源是否按规范加载"
keywords:
    - "SVG图标"
    - "大屏看板"
    - "/static/"
    - "src/assets/svg"
    - "icon-xxx.svg"
source: "auto"
---

大屏看板模块的4个功能图标（数据中心、值班大屏、告警大屏、设备大屏）需使用SVG格式，存放于`/static/`目录下，文件名分别为`icon-data-center.svg`、`icon-duty-screen.svg`、`icon-alarm-screen.svg`、`icon-device-screen.svg`，页面中通过`v-if="item.svg"`优先渲染SVG，降级显示emoji。

项目SVG资源统一存放在`src/assets/svg/`目录，命名规范为`icon-xxx.svg`（如`icon-alarm.svg`、`icon-electric.svg`），用于组件图标引用。
