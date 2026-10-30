---
title: "PC处置中心更名、报警类型筛选新增与快捷控制面板删除"
usage_scenario:
    - "再次改造处置中心页面头部或筛选区"
    - "删除大组件后盘点关联死代码"
    - "按CSS类串定位待删UI元素"
keywords:
    - "处置中心更名"
    - "报警类型筛选"
    - "alarmType入参"
    - "快捷控制面板删除"
    - "死代码引用计数"
source: "auto"
---

## Task description
- Core requirement：PC 处置中心页（原"火警处置中心"）三项改动 —— ①页面名与菜单名去掉"火警"改为"处置中心"；②在 section-card header 的"实时刷新"左侧新增报警类型筛选；③删除 panel-card response-panel（快捷控制面板）与标题栏 type="primary" plain size="small"（加载预案）按钮。
- Task background：前端仓库 anwei-fire-remote-monitor-vue，目标文件 src/views/eiot/firewatch/alarm-order/index.vue（原 3239 行）；后端 /eiot/firewatch/alarm-order/page 已支持 alarmType 入参（FireAlarmOrderServiceImpl.mapAlarmTypeToLevel → eiot_alert_record.level），值域 fire/fault/risk/offline/false_alarm/info；侧边菜单无 SQL 记录，菜单名来自路由 meta.title。

## Execution process
1. alarm-order/index.vue：页面 h2 标题"火警处置中心"→"处置中心"；router/modules/remaining.ts 中该路由 meta.title "火警处置"→"处置中心"并同步注释。
2. 新增筛选：在"实时刷新"标签左侧插入 el-select（选项对齐后端 mapAlarmTypeToLevel 值域），新增 alarmTypeFilter ref（放在分页参数之后、refreshAlarms 之前以避免 TDZ 风险）与 handleAlarmTypeChange（切换时回到第 1 页再拉取），refreshAlarms 透传 alarmType 参数，零后端改动。
3. 删除两块 UI：panel-card response-panel 整块 + 加载预案按钮；先 Grep 逐一核查标识符引用计数，确认 handleNotify/handleMute/handleDispatch/showTimelineDialog 仍被 handleKeyboard 复用而保留，其余仅被删除块引用的 handleLinkage/handleReport/handleInspect/handleCallback/handleReview/handleReset/handleFalseAlarmQuick 七个函数、Connection/Upload/User/Search/Document/RefreshLeft/TurnOff/CircleClose/Stopwatch 九个图标 import、DevelopingTag import、.quick-response 样式块一并删除。
4. 连带清理：删 handleInspect 后 useRouter 的 router 变为未引用（Grep 确认仅剩 route.query.id 使用），移除 const router = useRouter() 与 useRouter import，避免引入新告警。
5. 验证：GetProblems 语言服务首次未就绪 → 重试后通过；剩余 6 个 unused-var 警告（isAdmin/userStore/notifyFormRef/dispatchFormRef/sendSmsByTemplate/repairOrderId）经比对确认为改动前既有，未引入新问题。

## Related files
- /src/views/eiot/firewatch/alarm-order/index.vue
- /src/router/modules/remaining.ts

## Notes
- .eslintrc.js 中 no-unused-vars 为 off，删除 UI 块不会因残留死代码报错，因此死代码必须靠人工逐个 Grep 引用计数来判定去留，不能依赖 lint。
- 用一次包含十余个候选词的长 alternation Grep 会被结果条数上限截断（显示 Found 15 matches 但实际更多），判断"是否仅此一处引用"需拆成单标识符逐个 Grep，否则会误判为无引用。
- 用户以类 CSS 选择器串（如 el-button--primary is-plain small）指代元素时，需先在模板中定位唯一匹配项再动手；本次用 plain 全量 Grep 复核，确认剩余命中均为 el-tag effect="plain"，该按钮形似唯一。
- 删除几十行模板会使同文件 script/style 行号整体偏移，先前 Grep 得到的行号随即失效，须重新定位后再改。

## Task overview
已完成：页面与菜单统一更名为"处置中心"；实时警情区新增后端驱动的报警类型筛选（复用已有 alarmType 入参，零后端改动）；快捷控制面板与加载预案按钮及其全部死代码（7 函数 + 10 import + 样式块 + 连带的 router）清理干净；类型检查无错误、无新增告警。待用户确认点：若侧边栏菜单实为数据库 system_menu 驱动则需另行 SQL 更新（当前路由为 hidden 静态注册，通常页签名即 meta.title）。Git 提交尚未执行，待用户确认。
