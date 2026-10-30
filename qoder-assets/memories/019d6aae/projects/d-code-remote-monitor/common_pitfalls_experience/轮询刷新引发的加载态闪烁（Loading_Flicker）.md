---
title: "轮询刷新引发的加载态闪烁（Loading Flicker）"
usage_scenario:
    - "排查固定周期出现的界面闪烁"
    - "实现自动刷新或轮询逻辑"
    - "评审加载态与兜底数据写法"
keywords:
    - "Loading Flicker"
    - "Spinner Flash"
    - "轮询闪烁"
    - "静默刷新"
    - "stale-while-revalidate"
source: "user"
---

Bug class: 轮询刷新引发的加载态闪烁（Loading Flicker / Spinner Flash，中文也叫轮询闪烁、加载遮罩闪烁；与 FOUC/FOIT 同属"短暂中间态闪一下"家族，但区别于 CLS 布局位移和 WCAG 的光敏性 flash）。
Root cause: 定时器回调复用了用户动作的加载函数，把绑定 v-loading/骨架屏/按钮 loading 的状态置 true；或轮询每轮先重置 mock/兜底数据再回填接口值。本项目两处实例：alarm-order 处置中心 30 秒轮询切 loading 蒙层；large-screen 每 30 秒先 loadMockData 再回填真值。
Fix pattern: 轮询走静默刷新（加载函数加 silent 参数：不切 loading、失败不弹 toast），loading 只属于首屏/手动刷新/筛选翻页；mock 兜底只在首次铺；弹窗打开时暂停轮询。
Reusable lesson: 固定周期出现的蒙层/骨架屏/按钮转圈或数值"先假后真"跳变，先查定时器回调链路有没有切 loading 或重置兜底数据；适用边界：所有 setInterval/setTimeout 自动刷新场景；不适用：用户显式触发的刷新，loading 反而是正确的反馈。
