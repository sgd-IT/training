---
title: "消防告警弹窗按level级别分流方案讨论与设计"
usage_scenario:
    - "后续实施告警弹窗级别分流改造时参考本次讨论的设计方案"
    - "评审告警弹窗相关UI/UX设计方案时核对是否符合本次讨论的分流原则"
keywords:
    - "告警弹窗"
    - "level级别"
    - "分流方案"
    - "duty-screen"
    - "monitoring-center"
source: "auto"
---

## Task description
- Core requirement: 讨论并设计一套方案，根据告警的 level 级别（一级/二三级）来划分报警弹窗的行为，以解决值班员处置不过来和弹窗中无法进行其他操作的问题。
- Task background: 现役 duty-screen 和 monitoring-center 两套弹窗逻辑同款，均为全屏遮罩式模态弹窗，所有等级告警统一排队、统一语音播报，导致高发时值班员被钉在弹窗里；用户要求一级保持不变，二三级需要变化。

## Execution process
1. 现状盘点：确认 duty-screen/index.vue（440-525行模板，662-800行逻辑）与 monitoring-center/index.vue（268-334行模板，468-542行逻辑）为同款实现；完整行为链为 WS推送/轮询 → alertQueue → 串行弹出 → 15秒倒计时 → 语音播报；根因是全屏遮罩（`.alarm-popup-overlay` CSS）和无等级区分的串行队列。
2. 分流设计：基于已有的 `getPopupSeverity()` 判据，将 `critical`（一级）维持现状，`major/warning/info`（二三级）改为非模态轻提示；明确改造后行为对照表（形态、可操作性、队列、时长、声音）。
3. 形态选型：提出三个选项：A（角落通知卡+待处置角标，推荐）、B（汇总弹窗）、C（不弹仅角标）；推荐A方案，因其改造量最小且能保障值班员全程可操作。
4. 机制细节：确定双通道队列（一级可抢占）、缺省级别"就高不就低"（按一级强弹）、自动消隐不写处置状态、建议顺带做同设备时间窗合并、声音分级（静默/轻提示音/聚合播报）等关键点。
5. 待拍板清单：明确需用户决策的6个事项，包括形态选择、声音策略、交互方式、缺省规则、合并策略及改造范围确认。

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor-vue\src\views\eiot\firewatch\duty-screen\index.vue
- d:\code\remote-monitor\anwei-fire-remote-monitor-vue\src\views\eiot\firewatch\monitoring-center\index.vue

## Notes
- None

## Task overview
已完成一次完整的、有实质性产出的方案讨论。输出了现状事实、分流设计、三种形态选型（含推荐）、必须定掉的机制细节以及待拍板清单，为后续开发提供了清晰的蓝图。

## Notes（2026-10-15 浏览器实测补充）
- 本方案设计已在最新代码上端到端实测通过：一级（热成像/烟火）走 .alarm-popup-overlay 全屏弹窗，15 秒倒计时自动关、未处置每 60 秒原样重弹、多条一级串行排队（显示"第 N/M 条"）；二级（离岗/通道占用）只进通知匣（.notice-pill「待处置 N」胶囊 + .notice-panel 深色面板，持久不自动消隐、点击条目才移除），另有 pending-toast「二级报警，请处置」反复闪现与轻提示音，全程无全屏弹窗——分流与设计一致。
- 造数样本实测可用：POST http://192.168.250.130:48080/app-api/open/camera-alert/report，form 参数 data={"productKey":"nk7kzc4eCTsrT67H","eventType":N,"tenantId":166,"devSno":"..."}，免登录返回 code:0。租户 166 四台摄像头 dn：烟火 BL094C4PCU38258(id8)、通道占用 CD008CCPAG564D9(id12)、离岗 CD008CCPAGDD657(id13)、热成像 CE0DD7APAGA8B59(id17)；dn 匹配表为 eiot_device_info.dn。
- 处置闭环实测：『去处置』→ handle_status=2 + read_flg=1、弹窗关闭、经 findTransferDeviceId 跳 /remote-console?deviceId=7&alertDeviceId=N 且不再重弹；『稍后处置』→ handle_status=1、不跳转，但约 2 分钟后仍会重弹（未退出弹窗轮询判据 status<2）。
- 通知匣条目同设备同事件按 5 分钟窗口聚合显示 ×N。
- 环境限制实录：Qoder Browser 子代理宿主窗口可能变为不可见（visible=false/attached=false）致截图全失败但 CDP/DOM 仍可用；后台标签 visibilityState=hidden 会节流定时器使弹窗节奏变慢；原生 click 工具超时，须用 JS evaluate el.click()。

## Notes（2026-10 后续会话修订，以此为准）
- 【方案主体已被实现推翻】上文第 3~4 步推荐的 A 形态「角落通知卡 + 待处置角标（通知匣）」最终被下线：两屏不再有通知匣入口，二三级告警在 monitoring-center 直接忽略（不入弹窗、不 toast），在 duty-screen 仅闪「待处理」pending-toast。阅读本方案时不要把"通知匣"当作当前实现。
- 【仍然有效的三条核心口径】① 一级=critical 全屏强弹、二三级=轻提示或不提示的分流原则；② 缺省级别"就高不就低"（未知/缺失一律按一级强弹）；③ 自动消隐不写处置状态。分流判据函数 isLightAlarmLevel 仍在 alarm-notice/index.vue 中被两屏具名导入使用。
- 【新增可判定维度】本次核实后端 firewatch-alert-new 的 payload 除 level 外还携带 alarmKind(1火警/2故障/3隐患/4离线/5误报/6信息)、tier、alarmType、subDeviceName、alertName，若要按类别而非等级分流，应优先用 alarmKind。
