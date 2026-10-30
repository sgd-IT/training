---
title: "浮层入口按内容数组做v-if导致空数据时整体不可见被误判为未挂载"
usage_scenario:
    - "排查浮层或通知入口完全不显示"
    - "实现常驻入口类组件的显隐条件"
    - "区分组件未挂载与内容为空两种现象"
keywords:
    - "常驻入口误写成空隐藏"
    - "v-if集合长度渲染"
    - "Teleport浮层排障"
    - "querySelector挂载探针"
    - "空态灰态占位"
source: "auto"
---

Bug class: 浮层/通知类组件把"内容为空"实现成"整体不渲染"，与"常驻入口"设计相悖，导致功能看起来完全不存在。

Root cause: 在 alarm-notice/index.vue 里入口胶囊写成 `v-if="items.length"`、展开面板写成 `v-if="expanded && items.length"`。当页面无新增二三级告警（实测现场：今日待处理 0 条、实时列表全是历史已处置记录）时条目数组恒为空，入口永不渲染；用户连续反馈"椭圆框不出现""没有啊"，排查方向一度被引向"组件没挂载/WS 没推送"，而真实原因只是显示条件写错。

Fix pattern: 入口容器改为无条件渲染 + 内部条目用 v-for/v-if 控制，空态用灰态样式与占位文案表达（如 `:class="{ 'is-empty': !items.length }"` + 「暂无待处置提醒」），使"常驻可见、来消息变亮"成立。排障时用**始终存在的外层容器类名做活体探针**：`document.querySelector('.alarm-notice')` 返回元素即证明组件已挂载（该组件根 div 在 `<Teleport to="body">` 内无条件渲染），从而一步区分"没挂载"与"挂载了但内容为空/位置跑偏"；后者再用 Elements 面板 hover 看高亮区域与计算样式（right/bottom/top、z-index）确认是否偏移或遮挡。

Reusable lesson: Don't gate a persistent entry element on its content list being non-empty because an empty list makes the whole feature invisible and gets misdiagnosed as "component not mounted / data not pushed"; instead keep the outer container always rendered and express emptiness with a disabled state plus placeholder text, and use that always-present container class as the mount probe. Applies when implementing or debugging Teleport/floating UI whose visibility is driven by a collection; does not apply to elements intentionally conditional by design (e.g., one-shot toast that should vanish).
