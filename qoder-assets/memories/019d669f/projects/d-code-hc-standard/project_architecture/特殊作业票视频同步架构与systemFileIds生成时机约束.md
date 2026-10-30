---
title: "特殊作业票视频同步架构与systemFileIds生成时机约束"
usage_scenario:
    - "排查特殊作业票视频或回放数据缺失问题"
    - "定位systemFileIds生成时机与视频上传阶段"
    - "诊断APP端完工后回放无法预览的链路"
keywords:
    - "systemFileIds"
    - "endWork"
    - "startWork"
    - "视频同步架构"
    - "APP回放链路"
source: "auto"
---

特殊作业票同步架构的已验证结构性约束：
- systemFileIds（票据文件标识）仅在 endWork() 阶段生成并设置，startWork() 不产生；票据文件只在工作完成后上传。
- PlatementTicketNewApi.startTicket() 中的 videos 数组被硬编码为空列表；视频数据必须经 saveOrUpdateVideo 接口单独推送，不能嵌入主票据推送。

## APP 回放链路诊断结论（实测验证）
- APP 回放展示链路：getWorkSpecial → baseWorkVideoMapper.selectWorkVideoList(outerId) → APP FragmentJobSlipMonitoring 将 videoList[0].videoPath 塞入 WebView 加载；完工后 videoPath 即卢博祥 getWorkPlayback 返回的 h5 回放页（source=1&data=<录像记录id>）。
- "完工后回放看不了"（所有票、APP 端）根因在设备侧：直接调卢博祥 /open/v2/getWorkPlayback 验证，跨两天两张票（ZY-426082708361124、ZY-126082809550894）回放流 rtpCount=0、videoFrameCount=0、progress=0，等待 15 秒仍为 0，且每次调用流 ID 递增（平台反复新建拉流会话）而设备始终不回传录像数据；同一设备 getDeviceLive 正常返回，排除设备离线。
- 结论边界：代码链路（endWork 推送、refreshVideo）均正常返回 200，问题属布控球 SD 卡录像配置/GB28181 回放功能（供应商侧），改代码无法修复。

（3012 开始结束时间相等报错的核心坑点与修复方式见「LuoboXiang saveTicketAndDevice 3012 error from identical work start/end times」。）
