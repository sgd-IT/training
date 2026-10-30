---
title: "LuBoxiang平台视频URL组成与来源分析"
usage_scenario:
    - "When diagnosing video push failures or missing video URLs"
    - "When integrating new video sources that must conform to the same LuBoxiang H5 URL format"
    - "When modifying the video lifecycle embedding logic in BaseWorkSpecialServiceImpl"
keywords:
    - "videoUrl"
    - "LuBoxiang platform"
    - "getDeviceLive"
    - "getWorkPlayback"
    - "base_work_video"
    - "videoPath"
source: "auto"
---

## Task description
- Core requirement: analyze the composition and source of `videoUrl` in the project
- Task background: The `videoUrl` is stored in the `base_work_video` table and originates from the LuBoxiang platform (58.19.176.195:8888); understanding its generation logic is critical for debugging video push failures and integrating new video sources

## Execution process
1. Identified the `base_work_video` table structure and key fields (`videoPath`, `sync_status`, `sync_result`) via mapper XML inspection
2. Traced the `refreshVideo()` method in `BaseWorkSpecialServiceImpl.java` to locate three distinct URL acquisition scenarios: `startWork()` → `getDeviceLive`, `endWork()` → `getWorkPlayback`, and manual refresh branching on work status
3. Extracted URL parsing logic: `getDeviceLive` returns `data.h5Url`; `getWorkPlayback` returns `data[0].url`
4. Verified the final H5 URL format: `https://yuanhuanvideo.keyersoft.com/h5/#/pages/work/workDetail/manualDetail?source=1&data={platform_data_id}`
5. Confirmed data flow: LuBoxiang API → `videoPath` in DB → push to园区 platform `/openapi/work/video/saveOrUpdateVideo`

## Related files
- d:\code\hc-standard\ruoyi-system\src\main\resources\mapper\base\BaseWorkVideoMapper.xml
- d:\code\hc-standard\ruoyi-system\src\main\java\com\ruoyi\base\service\impl\BaseWorkSpecialServiceImpl.java

## Notes
- None

## Task overview
Successfully completed full analysis of `videoUrl` composition and source: identified all three acquisition scenarios, extracted precise URL parsing rules from LuBoxiang API responses, confirmed the final H5 URL format, and mapped the end-to-end data flow from platform API to database storage to园区 platform push.
