---
title: "WSPlayer RTSP DESCRIBE 404 diagnosis across video-surveillance and remote-console"
usage_scenario:
    - "When diagnosing WSPlayer-based video black screen before first frame"
    - "When investigating RTSP 404 errors in ICC/MTS-integrated video systems"
    - "When identifying frontend defects that mask media service failures"
keywords:
    - "WSPlayer"
    - "RTSP 404"
    - "DESCRIBE"
    - "ICC"
    - "MTS"
    - "video black screen"
source: "auto"
---

## Task description
- Core requirement: diagnose the root cause of intermittent black screen on first video playback across two pages (/video-surveillance and /remote-console?deviceId=7)
- Task background: both pages use WSPlayer V1.3.8 + ICC + WebSocket, not flv.js/SRS; failure occurs before first frame, at RTSP DESCRIBE step returning 404 with User-DefCode: 20019; user provided six candidate causes (IDR/GOP, autoplay, H265, flv.js/MSE, init size, stream disconnect)

## Execution process
1. Browser agent performed two rounds of limited-time playback testing on /video-surveillance: all four default enterprise cameras failed with identical pattern — start-video HTTP 200 → WS connect → OPTIONS 200 → DESCRIBE ~3s → RTSP 404 → SDK error 20019 → WS close 1006 → zero frames
2. Browser agent performed two rounds on /remote-console?deviceId=7: three cameras failed identically, confirming same failure chain; captured CSeq/method/state timing and verified StartVideo URL matches actual DESCRIBE URI
3. Debug agent traced code paths: both pages share WsPlayerWrapper.vue → PlayerManager.js → ICC /MTS/Video/StartVideo; confirmed streamType=2 (auxiliary stream) is used consistently
4. Browser agent confirmed two frontend defects: (a) WsPlayerWrapper uidSeed resets per instance causing duplicate DOM IDs (e.g., wsplayer-172...), leading to only one SDK instance rendering; (b) realError triggers immediate emit(close) and parent component unmounts player, causing error UI to vanish before user sees it
5. Both agents concluded current failure is upstream — not browser codec/autoplay, not MSE config, not GOP — but requires ICC/MTS logs to distinguish device connection, channel mapping, or auth issues

## Related files
- /src/views/eiot/video/components/WsPlayerWrapper.vue
- /src/views/eiot/firewatch/remote-console/components/VideoPanel.vue
- /src/icc/PlayerManager.js

## Notes
- Frontend ID collision does not explain why single-path retry still fails with 404; it is a secondary defect that affects debugging visibility
- Error masking prevents users from seeing the actual 20019 error; this must be fixed to aid future diagnostics
- Main/auxiliary stream switch was attempted but blocked by UI unavailability during failure state; cannot confirm if streamType=1 would succeed

## Task overview
Partially completed: confirmed identical RTSP DESCRIBE 404 failure pattern across both pages, ruled out six common frontend candidates, identified two frontend implementation defects (ID collision, error masking), and established that root cause lies upstream in ICC/MTS service — requiring log evidence to resolve device/channel/auth mapping issues.
