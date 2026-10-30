---
title: "小程序video控制栏定原生、放弃自绘覆盖层"
usage_scenario:
    - "小程序视频页需要调整控制栏按钮或样式时"
    - "有人再次提议自绘播放器控制栏时"
    - "评估播放类UI定制的成本收益时"
keywords:
    - "原生控制栏"
    - "自绘覆盖层"
    - "全屏按钮位置"
    - "uni-app video"
    - "复杂度权衡"
source: "auto"
---

Conclusion: 小程序消控室监控页（uni-app video）的控制栏采用微信原生 controls=true，不再自绘覆盖层；全屏按钮位置等原生排列由微信固定（播放→进度条→时间→全屏），无法调整，作为既定前提接受。

Rationale (trade-off): 自绘控制栏（controls=false + ctrl-layer 覆盖层 + 播放/暂停/全屏图标 + 自动隐藏定时器 + onVideoPause/onFullscreenChange 事件接管）仅为挪动全屏按钮位置这一项诉求，代价是约 148 行新增代码与衍生复杂度（如 pause 事件与自愈 healing 状态互相干扰需在 onVideoPlay 里先捕获 healingNow 再复位），收益与成本严重不匹配；原生控制栏自带转圈并与缓冲自愈的 pause→play 复位天然兼容。

Rejected alternatives: 保留自绘并继续打磨图标与间距——被用户两次明确要求"用原生的""用回原来的"否决；改造原生样式——微信平台能力上不可行。

Applicable / expiry conditions: 适用于本项目所有 uni-app/微信小程序 video 播放页面的控制栏决策；仅当出现原生控制栏无法满足的硬需求（如必须遮挡/替换某按钮、必须自定义层级）时才重新评估自绘，且须先确认该需求确实无法通过 show-center-play-btn、enable-progress-gesture、is-live 等原生属性达成。
