---
title: "前端定时轮询必须静默刷新不得切换 loading 遮罩"
usage_scenario:
    - "实现或修改前端定时轮询刷新逻辑"
    - "排查列表周期性闪烁或加载蒙层闪现"
    - "评审带加载态的自动刷新代码"
keywords:
    - "静默轮询"
    - "v-loading遮罩"
    - "周期性闪烁"
    - "mock兜底数据"
    - "silent参数"
source: "user"
---

前端定时轮询刷新必须走静默模式：轮询路径不得切换绑定 v-loading、骨架屏或按钮 loading 的加载状态，加载态只允许首屏加载、手动刷新、筛选翻页等用户显式动作触发；轮询失败只打日志、不弹 toast。轮询同样不得重置 mock/兜底数据，兜底只在首次加载铺一次，之后保持上一份成功数据，避免每轮先回落假值再回填造成闪烁。实现方式：给加载函数加 silent 参数（如 refreshAlarms(notify, silent)），轮询传 silent=true；详情/处置弹窗打开时暂停轮询。该约定已写入前端仓库 ARCHITECTURE.md 的「设计与放置规则」。
