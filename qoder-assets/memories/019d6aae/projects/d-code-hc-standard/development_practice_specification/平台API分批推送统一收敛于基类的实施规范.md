---
title: "平台API分批推送统一收敛于基类的实施规范"
usage_scenario:
    - "新增或改造平台推送接口遇到请求体过大"
    - "多个API子类存在重复分批循环需要重构"
    - "平台返回502或HTML导致推送解析崩溃时"
keywords:
    - "分批推送"
    - "PlatementBaseApi"
    - "sendPostBatch"
    - "BATCH_SIZE=10"
    - "非JSON响应防御"
source: "auto"
---

## Goal
平台API的分批推送统一收敛到基类 PlatementBaseApi，避免逻辑分散在各子类；同时作为"请求体过大导致平台返回502/HTML"这类问题的标准解法。

## Steps
Step 1: grep_code - 搜索所有含分批循环的 API 类，定位重复点；
Step 2: SearchReplace - 在基类 PlatementBaseApi 中提供 sendPostBatch()（支持 batchSize、retryPolicy 等通用参数），子类把各自的分批循环替换为调用该方法；
Step 3: Bash - mvn compile 验证重构可编译。

## Notes
- 分批大小统一 BATCH_SIZE=10，与作业票 saveOrUpdateStep 的既有实现保持一致；专项检查的 saveTaskResult/saveTaskItemLog 亦按此规格在 API 层内部分批，对 Service 层透明。
- 基类 sendPost 必须对非 JSON 响应做防御（JSONUtil.isTypeJSON 判定后返回 code=-1），Service 层需把 code==-1 纳入同步失败处理，否则平台返回502 HTML 会直接解析崩溃。
- 待办缺口：PlatementRiskCheckUnitApi.saveTask() 仍是全量一次性推送，是 base_risk_task 报"批量操作超过最大限制"的直接原因，需按同一模式改造。
- 卢博祥平台视频推送已适配：refreshVideo() 按作业状态自动选择 getDeviceLive 或 getWorkPlayback，并把成功/失败的平台返回描述写入 base_work_video 表的 sync_result 字段，供前端状态展示与重试判定。
- 禁止硬编码批次大小，禁止把分批循环写在各个子类里。
