---
title: "移动端告警详情页深头浅身重设计与去AI味落地"
usage_scenario:
    - "再次改造移动端告警详情页或复用其视觉结构"
    - "排查头图与卡片间距或导航栏滚动联动问题"
    - "对消防端UI做去AI味配色收敛时参考"
keywords:
    - "告警详情页"
    - "深头浅身"
    - "去AI味"
    - "NavBar主题联动"
    - "现有SVG图标"
source: "auto"
---

## Task description
- Core requirement: 对移动端「告警详情」页做视觉重设计并落地，历经四轮反馈：卡片与头图间距太挤 → 采用提案 → 头图配色有AI味 → 火警徽章用现有图标 → 最终以真实页面为准。
- Task background: 提案文件 algorithmic-art/alarm-detail-redesign-v1.html（浏览器MCP截图验证），真实页面 anwei-fire-remote-monitor-mobile/pages/alarm-detail/index.vue（原296行，重写后574行）；数据走既有接口 /eiot/firewatch/alarm-order/get。

## Execution process
1. 间距修复：首卡 margin-top 由 -18px 压盖改为 12px 留白，头图内下边距 16px→18px，玻璃条到卡片总间距约 10px→30px。
2. 前置盘点：NavBar 组件确认原生支持 theme(light/dark/transparent) 与 border 属性；AlarmOrderRespVO 确认 tier/alarmDescription 字段可用；convertToRespVO 确认 alarmDescription=details、tier 仅新语义记录填充；static SVG 图标确认彩色填充（icon-fire #d84315、icon-fault #e65100）。
3. 去AI味改造：头图底色由四段蓝渐变拉平为纯 #1e39b1；删除光晕、霓虹阴影、内高光；「严重」药丸改纯 #d84315；徽章改半透明白方块+原色图标；分节色条改纯色。
4. 图标对齐：提案图内手绘火苗替换为 icon-fire.svg 原版矢量路径（viewBox 1024，徽章50px/图标28px比例）；真实页面本就走 TYPE_ICON_MAP 按 alarmType 加载现有图标。
5. 页面落地：index.vue 全量重写（模板+脚本+样式），无后端改动；onPageScroll 超 180px 时导航栏由透明切白底；新增展示 tier（Ⅳ档）入副标题、alarmDescription（原报摘要）入事件信息卡并做清洗（去"来源:"前缀、去20位设备长ID）。
6. 验证：GetProblems 语言服务未就绪，改为人工回读文件三段（模板/脚本/样式共574行）确认结构完整无重复。

## Related files
- /algorithmic-art/alarm-detail-redesign-v1.html
- /anwei-fire-remote-monitor-mobile/pages/alarm-detail/index.vue

## Notes
- 并行读取时误读 browser-use 的 SERVER_METADATA.json（应为 NavBar 组件），批量 Read 前需核对每个目标路径。
- 负边距压盖头图与头图内部小下边距会相互抵消导致实际间距远小于预期，调整间距须把两者合并计算。
- .vue 文件在语言服务未就绪时无法自动检查，需回读文件人工复核写入完整性。

## Task overview
已完成：提案图与真实页面均收敛为"深头浅身"版（纯品牌蓝头图+现有彩色SVG图标+纯色状态色+三卡片+时间线），零后端改动、零新增依赖；用户确认以真实页面为采用版本，效果图转为评审留档。待用户在微信开发者工具核验滚动导航栏切换与非火警类型兼容，git 提交尚待用户确认（拟用 feat(alarm-detail): …）。
