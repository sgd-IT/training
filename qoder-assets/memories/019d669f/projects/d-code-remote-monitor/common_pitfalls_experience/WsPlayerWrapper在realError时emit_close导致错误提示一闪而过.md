---
title: "WsPlayerWrapper在realError时emit close导致错误提示一闪而过"
usage_scenario:
    - "排查视频播放报错信息瞬间消失时"
    - "设计可复用播放器组件错误处理时"
keywords:
    - "realError"
    - "emit close"
    - "错误UI掩盖"
    - "closeVideo"
    - "WSPlayer错误处理"
source: "auto"
---

Bug 类型：前端错误提示被立即卸载掩盖。
根因：realError 处理函数调用 closeVideo(false) → emit('close') → 父组件卸载播放器 DOM 节点 → 报错信息在用户读到之前就消失。
修复模式：把 realError 分支里的 emit('close') 改为 emit('error', errorInfo)，由父组件分别负责错误展示与卸载清理。
可复用经验：不要在 realError 时 emit('close')，因为它会卸载播放器并隐藏错误 UI；应 emit 专用 'error' 事件携带错误详情，让父组件持久展示错误并提供恢复选项。适用于必须保留用户可见错误状态的媒体播放器组件错误处理；不适用于无 UI 反馈的纯清理回调。
