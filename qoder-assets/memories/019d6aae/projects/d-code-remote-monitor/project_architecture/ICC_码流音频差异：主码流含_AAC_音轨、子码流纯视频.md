---
title: "ICC 码流音频差异：主码流含 AAC 音轨、子码流纯视频"
usage_scenario:
    - "排查小程序视频无声音"
    - "调整视频码流类型"
    - "评估主码流带宽与起播代价"
keywords:
    - "ICC 码流"
    - "主码流音频"
    - "AAC 音轨"
    - "streamType"
    - "拾音"
source: "user"
---

在 ICC（大华）HLS 链路上，音轨只存在于主码流。实测方法为拉 m3u8 → 取 ts 分片 → 解析 PAT/PMT 的 ES 列表：同一通道 streamType=1（主码流）的 ES 为 HEVC(0x24) 或 H.264(0x1b) + AAC(0x0F, AAC-LC 8000Hz 单声道)，streamType=2（子码流）只有视频轨；同源 FLV 流的 audio tag 数为 0，即换协议也拿不到音频。因此小程序消控室监控要拾音必须点名取主码流：/eiot/video/camera/live/url 新增可选 streamType 参数覆盖 video_camera.quality，移动端 getCameraLiveUrl(cameraId, '1')。不动数据库 quality 字段，是为了避免 PC 端 WSPlayer/VideoPlayer 被一起拖到主码流。代价：主码流 2.2-3.3Mbps（ts 分片 545-814KB/2s），子码流约 0.3Mbps（76KB/2s），首片更大导致起播更慢，移动端 PLAY_TIMEOUT 已由 10s 放宽到 40s。
