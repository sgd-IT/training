---
title: "消防告警二三级轻提示通知匣在PC两屏落地实现"
usage_scenario:
    - "继续调整或排查大屏告警轻提示交互"
    - "复核弹窗按等级分流的实现落点"
    - "为其他大屏页面接入同类通知匣"
keywords:
    - "二三级轻提示"
    - "告警通知匣"
    - "待处置入口常驻"
    - "顶部锚定偏移"
    - "level分流改造"
    - "就高不就低兜底"
source: "auto"
---

## Task description
- Core requirement: 按已定稿的方案实现 PC 端两屏（duty-screen 值守大屏、monitoring-center 监控中心）的二三级告警"轻提示"改造，替代原来的全屏遮罩弹窗；一级保持强弹不变。
- Task background: 现役两套弹窗对所有等级统一排队、统一语音播报，高发时值班员被钉在弹窗里。用户拍板形态 A 后又改为"椭圆入口框 + 点击缩放展开列表 + 最多可见 3 条 + 滚轮滑动（滚动条透明隐藏）"，锚定在两屏各自的"告警趋势"卡左下侧；缺省级别要求"就高不就低"。

## Execution process
1. 范围核实：查证 monitoring-center 为企业账号登录默认落地页（LoginForm.vue 角色改写、HomeRedirect.vue 按角色重定向、ToolHeader.vue 非 super_admin 跳转、remaining.ts 路由注释四条证据），确认必须与 duty-screen 同步改造，不是死页面。
2. 新建 alarm-notice/index.vue 通知匣组件：椭圆胶囊入口（计数 N=0 隐藏）、Teleport 到 body 的浮层定位（offset-left/offset-right 两个可调 prop）、缩放展开动画、定高约 3 条窗口 + 底部 mask-image 渐隐 + 隐藏滚动条、同设备同事件 5 分钟合并计数 ×N、上限 50 条、忽略仅移除不写状态、到达复用 playAlarmSoundOnce 播一声轻提示音；导出 isLightAlarmLevel() 分流判据。
3. 分流判据实现：'2'/'3'/'4'/'5'/major/warning/info → 通知匣；'1'/critical → 全屏弹窗；缺失或未知值一律按一级强弹（就高不就低）。
4. duty-screen 改造（+43/-9）：挂载通知匣（offset-right 424，右栏宽 400 + padding 推算）、WS firewatch-alert-new 与 getRecentUnprocessedAlerts 轮询双入口按判据分流、删除 level 的 `|| '2'` 兜底、条目点击接现有 viewAlarm 详情页。
5. monitoring-center 改造（+52/-12）：同上（offset-left 304），额外覆盖 alarm-push 英文通道；核实该页 alertList 的 level 取自后端 alarmLevel 为英文形态，levelText 沿用既有映射。
6. 一级全屏弹窗链路（队列、15 秒倒计时、闪烁、TTS、稍后/去处置）零改动。
7. 编译验证：vue-tsc --noEmit 全量跑通，新组件与 duty-screen 零错误；monitoring-center 报 3 个 TS6133 未使用变量，经 git diff -U0 核对这些符号不在本次增删行内，判定为仓库既有历史错误（全仓基数 436 条），不予修改。

## Related files
- /anwei-fire-remote-monitor-vue/src/views/eiot/firewatch/alarm-notice/index.vue
- /anwei-fire-remote-monitor-vue/src/views/eiot/firewatch/duty-screen/index.vue
- /anwei-fire-remote-monitor-vue/src/views/eiot/firewatch/monitoring-center/index.vue

## Notes
- "去掉兜底默认值"必须与"判定函数对缺失值向安全侧兜底"成对出现；只做前者会让缺失字段落入宽松分支，语义正好反转。
- 判断某页面是否仍需纳入改造范围，靠代码内硬证据（登录默认路径、根路径重定向、顶部按钮跳转、路由注册注释），不能凭页面名称或主观推测其为废弃页。
- GetProblems 在语言服务未就绪时不可用，需改用命令行 vue-tsc 直判；PowerShell 执行策略会拦截 npx.ps1，改用 node 直跑包内 bin 入口。
- 新增类型报错需先用 git diff 确认相关符号是否落在本次改动行内，再决定修复还是按既有基线放行。
- 通知匣浮层必须独立于大屏右栏面板显隐开关，否则面板收起时告警入口消失。
- 前端内存计数不做持久化，大屏长期不刷新场景下可接受。

## Task overview
已完成：新增通知匣组件并完成两屏分流改造，共 3 个文件；一级弹窗逻辑未受影响；编译验证本次改动文件零新增错误。后端经核实零代码改动（WS payload 已带 level/alarmKind）。尚未验证：真机造数（烟雾应仍强弹、防拆应进通知匣、5 分钟内重复应合并）、浮层 offset 初值的视觉微调、以及线上值班员实际使用哪类账号。

## Notes（后续会话修订）
- 【入口显示条件已改】上文第 2 步"计数 N=0 隐藏"与定稿的"常驻入口"设计相悖，导致值班员看不到功能入口（实测现场：待处理 0 条时椭圆框永不渲染，用户反馈"没有啊"）。现改为**常驻显示**：0 条时椭圆框以灰态呈现并显示计数 0，`.alarm-notice` 根容器始终存在于 body，可作为"组件是否挂载"的 Console 探针。
- 【文案口径已改】入口文案由「告警 N」改为「**待处置 N**」，空态由「暂无未处置提醒」改为「**暂无待处置提醒**」（纯模板字符串改动，不涉及逻辑）。工作台 dashboard 的 3 处演示数据「待处置」未动。
- 【duty-screen 锚点已迁移】不再锚在"告警趋势"卡左下侧，改为锚在右栏"**今日警情**"卡左上角外侧并与卡顶边对齐：offset-right=414（右栏宽 400 + main-content padding 10 + 4px 缝）、offset-top=66（顶栏 .header 高 56 + padding 10）。为此组件新增 `offset-top` prop 与"顶部锚定"模式（设定后面板改为向下展开，列方向反转 + 动画 transform-origin 改 top center、enter-from 改 translateY(-8px)）；未设 offset-top 时保持原底部锚定行为。
- 【monitoring-center 未迁移】其"今日警情"卡位于左栏最左侧（卡左缘距屏幕仅 10px），外侧无空间放椭圆入口，故仍保持 offset-left=304 的原底部锚定位置。
- 【排查口诀】椭圆框不出现按四步定位：① `document.querySelector('.alarm-notice')` 判挂载；② Network→WS 看 firewatch-alert-new 帧及其 content.level；③ 对照 isLightAlarmLevel 分流判定；④ 刷新页面清内存态 alertShownIds 以排除去重命中。
- 【@open 绑定现状已改（2026-10-13 实测）】上文第 4 步"duty-screen 条目点击接现有 viewAlarm 详情页"不成立：duty-screen 的 AlarmNotice 挂载长期**漏绑 @open**，点击只走组件内部 removeItem（条目消失、无任何动作）；只有 monitoring-center 绑定了 @open="handleNoticeOpen"（打开本页详情弹窗）。现已为 duty-screen 补绑 @open 并抽公共 jumpToAlarmScene(deviceId)（有用传设备跳 remote-console，否则查关联摄像头跳 video-surveillance），与一级弹窗"立即处置"共用。排查"点了没反应/条目消失"先核对各屏是否绑了 @open。
- 【一级弹窗已非零改动（2026-10-13）】上文第 6 步"一级全屏弹窗链路零改动"已被后续会话推翻：alertShownIds 永久去重导致一级告警弹一次后不再重弹（15s 消失后须重挂页面才恢复），现改为 heavyAlertLastShownAt + isHeavyRepeatDue 的 60 秒周期重弹（未处置期间循环，处置后停止），duty-screen 与 monitoring-center 同构实现；alertShownIds 语义收窄为仅服务通知匣。
- 【通知匣整体已下线（最新，以此为准）】本条所描述的「待处置 N」通知匣已在后续需求中被**从 duty-screen 与 monitoring-center 双双删除**：模板 <AlarmNotice> 浮层、alarmNoticeRef、alertShownIds、onNoticeOpen/handleNoticeOpen 全部移除，两屏的轮询与 WS 回调收敛为"仅一级告警 popupRef.enqueue 入队全屏弹窗"，二三级不再有通知入口（duty-screen 只在「待处理」格子闪 pending-toast）。上述关于入口显示条件、文案、锚点 offset、@open 绑定、排查口诀的各条修订**均已失效**，仅作历史演进记录保留。仍然有效的是：isLightAlarmLevel 作为一级/二三级分流判据被两屏继续从 alarm-notice/index.vue 具名导入（该文件默认导出组件已成无人使用的死代码）、"就高不就低"的缺省兜底口径、以及一级弹窗 60 秒周期重弹机制。
