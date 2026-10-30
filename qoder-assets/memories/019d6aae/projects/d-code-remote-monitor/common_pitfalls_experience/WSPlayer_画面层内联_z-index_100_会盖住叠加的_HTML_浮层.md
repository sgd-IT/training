---
title: "WSPlayer 画面层内联 z-index 100 会盖住叠加的 HTML 浮层"
usage_scenario:
    - "在WSPlayer播放器画面上叠加HTML控件或下拉框"
    - "DOM中元素存在但界面肉眼不可见的问题排查"
    - "值班室/报警弹窗等页面浮层与el-select层级样式调整"
keywords:
    - "WSPlayer 层级遮挡"
    - "z-index 100"
    - "浮层不可见"
    - "wsplayer-item 内联样式"
    - "el-select 下拉框不可见"
source: "auto"
---

问题类型：在 WSPlayer 画面上叠加 HTML 浮层（下拉框/标签/角标）时，浮层在 DOM 里存在但肉眼不可见，容易被误判为"数据没渲染"。
根因：WSPlayer.js 的窗口布局函数 pe() 给每个 .wsplayer-item 画面窗口内联写 z-index，从 100 起倒序递减（单窗口=100），浮层 z-index ≤ 100 时被画面层整个盖住。
修复模式：叠加在 WSPlayer 上的浮层 z-index 必须大于 100（AlarmVideoDialog 的 .camera-tags 用 200；加载 spinner 为 2e9，属加载期瞬态可忽略）。
补充事实：WSPlayer 播放时画面自带通道名 OSD（底部）与时间戳（右上），这些由闭源 SDK 绘制、前端样式管不到；因此"禁用摄像头状态标识/时间戳"那类规范只适用于 VideoPlayer(dhflv/dhhls) 体系页面，不要据此去改 WSPlayer 侧的叠层。
复用经验：排查"元素存在但看不见"先查层叠顺序再怀疑数据；该坑仅适用于在 WSPlayer 上叠控件，普通页面浮层不受影响。
