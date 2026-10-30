---
title: "卢博祥视频URL字段解析与H5拼接格式"
usage_scenario:
    - "解析卢博祥平台返回字段拼装视频地址"
    - "排查视频URL为空或播放页打不开"
    - "核对推送园区平台的视频链接格式"
keywords:
    - "getDeviceLive"
    - "h5Url"
    - "getWorkPlayback"
    - "manualDetail"
    - "platform_data_id"
source: "auto"
---

卢博祥平台视频地址取值规则：实时流取 getDeviceLive 响应的 data.h5Url；回放取 getWorkPlayback 响应的 data[0].url。最终推给园区平台的 H5 播放地址格式为 https://yuanhuanvideo.keyersoft.com/h5/#/pages/work/workDetail/manualDetail?source=1&data={platform_data_id}，其中 data 参数为平台侧数据ID。

视频推送的触发时机与生命周期嵌入见"作业票平台同步为状态触发式且删除类变更默认不在补推覆盖"；推送结果的 sync_result 字段写入逻辑见"平台API分批推送统一收敛于基类的实施规范"。
