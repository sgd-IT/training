---
title: "告警弹窗查看视频改用WSPlayer整包复用实现ws拉流"
usage_scenario:
    - "改造告警弹窗视频播放方式或拉流协议时"
    - "评估是否复用WSPlayer整包而非改后端字段时"
    - "迁移剩余PC端HLS落点到WSPlayer时"
keywords:
    - "告警弹窗查看视频"
    - "WSPlayer复用"
    - "ws拉流"
    - "getVideoCamera补全"
    - "AlarmVideoDialog"
source: "auto"
---

## Task description
- Core requirement: 用户要求"拉流改为ws类型的，主要用于告警弹窗的查看视频路径"，随后追加"直接复用就好"。
- Task background: 前端 anwei-fire-remote-monitor-vue 中，告警弹窗链路为 AlarmPopupDialog.vue（被 duty-screen、monitoring-center 引用）→「查看视频」→ AlarmVideoDialog.vue → getLiveUrl(cameraId) → 后端 /eiot/video/camera/live/url 返回 {url:hls, flvUrl}，播放器用 VideoPlayer.vue（HTTP-FLV 7886 优先，H.265 降级 HLS）。项目另有一套现成的 ws 播放体系 WsPlayerWrapper.vue（WebSocket+RTSP+WASM 软解），已被 video-surveillance、video-manage、remote-console/VideoPanel 使用，其入参 camera 需 deviceId/channelId/quality 拼 4 段式 channelCode。

## Execution process
1. 现状盘点：读 IccOpenApiClient.java（type=hls 硬编码）、VideoCameraController.getLiveUrl、AlarmVideoDialog.vue、WsPlayerWrapper.vue、VideoPanel.vue，确认两条 ws 路线——(A) 后端 ICC type 改 ws_flv 仍用 dhflv；(B) 整包换用 WSPlayer。
2. 初版按 B 走但动了后端：给 AlarmAssistCameraVO 加 deviceId/channelId/quality 三字段 + FireWatchAlarmAssistServiceImpl.loadCameras 填充 + alarmAssist.api.ts 补类型。
3. 用户"直接复用就好"触发回滚：撤销上述后端 VO/Service/前端类型三处改动，后端零改动。
4. 最终落地仅改 AlarmVideoDialog.vue：模板 VideoPlayer→WsPlayerWrapper(:num=1 :show-control=false @close)，脚本用 computed activeCamera + watch(selectedCameraId) 调 getVideoCamera 详情补全 deviceId/channelId/quality（抄 remote-console VideoPanel 既有模式），onClosed 清空状态。
5. 验证：mvn -pl module-eiot/module-eiot-biz compile BUILD SUCCESS（回滚后重编译）；node node_modules/eslint/bin/eslint.js 对 AlarmVideoDialog.vue + alarmAssist.api.ts 通过（EXIT_CODE=0）。语言服务未就绪时 GetProblems 不可用，改人工读文件+单文件 eslint 兜底。

## Related files
- /src/views/eiot/firewatch/components/AlarmVideoDialog.vue（唯一最终改动文件）

## Notes
- "直接复用"判据：能靠前端调用现成接口(getVideoCamera)补齐数据就不动后端 VO/Service，避免跨端扩散改动。
- 边界：超管跨租户时 getVideoCamera 可能查不到详情（与远程控制中心同一边界），此时播放器遮罩报错；若真遇到再往 AlarmAssistCameraVO 加回三字段（约3行）。
- npx eslint 在 PowerShell 被执行策略拦截，改用 node 直调 node_modules/eslint/bin/eslint.js。

## Task overview
已完成：告警弹窗查看视频切换为 WSPlayer 体系实现 ws 拉流，全程复用现有组件与接口，后端零改动；ESLint 与后端编译均通过。改动曾停在工作区待确认提交。

## 备注（后续核对补充）
- 本任务后 WSPlayer 覆盖点共 4 处：video-surveillance、video-manage、remote-console/VideoPanel、AlarmVideoDialog；PC 端仅剩告警工单 alarm-order/index.vue「现场视频」仍为纯 HLS（只取 urlRes.url，未用 flvUrl），是同款改法的下一个候选迁移点。
- 后续实测确认 WSPlayer(RTSP over WS) 拉流稳定约 90%，而 HLS/FLV 经常失败，故"照抄 AlarmVideoDialog 改法迁移剩余 PC HLS 落点"作为既定方向成立；移动端因小程序不支持 WASM 无法套用本改法。
