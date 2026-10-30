---
title: "处置中心AI智能分析面板由静态占位改为真实大模型逐单研判"
usage_scenario:
    - "续建或调整处置中心AI研判面板与接口"
    - "为告警类页面接入大模型结构化输出"
    - "评估AI能力复用LlmClient还是对话接口"
keywords:
    - "告警AI研判面板"
    - "AlarmAnalysisService"
    - "结构化JSON输出"
    - "规则降级兜底"
    - "selectedAlarm watch触发"
    - "ai_analysis闲置列"
source: "auto"
---

## Task description
- Core requirement: 整改 PC 处置中心右侧「AI智能分析」面板，让它真正用大模型分析数据，而不是显示写死的占位分数。
- Task background: alarm-order/index.vue 的 analysisItems 硬编码为 火情风险60/人员聚集20/设备异常10/其他风险10，与选中警情无关；aiSuggestion 字段虽在列表映射里但从未渲染。后端已有 LlmClient（ai.llm.* 配置、qwen3.5-35b）、AiChatApi.chatOnce（月报在用）、ChatController 的 [TOOL_CALL] 多轮通道，以及 fire_alarm_order 表闲置的 ai_analysis(TEXT)/ai_suggestion(varchar500) 两列。用户明确"不一定按这个来，只需要用ai做点什么"，放弃方案点选直接落地。

## Execution process
1. 前置盘点：确认工单 id == eiot_alert_record.id（doGetAlarmOrder 用 alertRecordMapper.selectById），getAlarmOrder 已带租户过滤可安全复用；现成 FireWatchAlarmAssistService.getAlarmAssist(alertRecordId) 能提供报警设备、安装空间、同区域设备（含 alarming/online）、关联摄像头，够拼证据链；测试库 fire_alarm_order 为 0 行、eiot_alert_record 有 33455 行。
2. 维度重定义：把无数据支撑的「人员聚集/其他风险」换成「误报风险/蔓延风险」，四项 key 固定为 fire/falseAlarm/device/spread，理由用 hover title 透出。
3. 新增 AlarmAnalysisService（module-ai-biz/service）：组事实清单 → 规则基线评分 → LlmClient.chatOnce(system,user,temperature=0.2,maxTokens=900,读超时30s) → 剥代码围栏后 Jackson readTree 严格解析 risks/verdict/summary → clamp 到 0-100；模型失败或 JSON 不可解析时整体降级为 ruleVerdictType + ruleReason，source 标 "rule" 供前端明示降级。
4. 证据项：告警类型/级别/状态、设备名与类型、空间路径、在线与报警中状态、最近数据时间、同区域总数/报警中/离线数、摄像头路数、该设备近7天报警次数、近30天误报次数；历史计数走 TenantUtils.executeIgnoreWithResult 按 deviceId 聚合。
5. AlertRecordMapper 新增 countDeviceAlerts(deviceId, beginMs, endMs)：单次扫描返回 total 与 SUM(alarm_kind='false_alarm') 的 false_count，避免两次 COUNT。
6. LlmClient 改造：post(ObjectNode body) 拆出 post(body, readTimeoutMs) 重载并加 getLlmModel()，原 chatOnce 签名不变，避免影响对话与月报通道。
7. 缓存取舍：内存 ConcurrentHashMap + 10 分钟 TTL + force 参数强制重算，不写 ai_analysis/ai_suggestion 列（模型输出非权威结论，写业务表需先定保留期与重算策略）。
8. 新增 AlarmAnalysisController（POST /ai/analysis/alarm/{orderId}?force）+ AlarmAnalysisRespVO；沿用既有约定不加 @PreAuthorize，登录态即可访问。
9. FireAlarmOrderServiceImpl.convertToRespVO 补 vo.setAlertRecordId(record.getId())——该字段此前全仓无赋值点，VO 恒为 null，前端 mapAlarmToDisplay 一直在取空值；AI 侧同时保留 orderId 兜底。
10. 前端：新建 src/api/ai/analysis/index.ts；alarm-order/index.vue 删掉 analysisItems/confidenceColors 静态数据，改绑 aiResult，未选中显示提示、加载中/失败可重试；触发方式从 selectAlarm 内联调用改为 watch(() => selectedAlarm.value?.id)，因 confirmFire/markFalseAlarm/playVideo/viewDetail/openAlarmFromQuery/键盘切换共 8 处会直接改写 selectedAlarm，逐个入口加调用易漏。
11. 验证：mvn -o -pl module-ai/module-ai-biz,module-eiot/module-eiot-biz -am compile 三次 BUILD SUCCESS；node node_modules/vue-tsc/bin/vue-tsc.js --noEmit 仅剩仓库既有 3 条 TS6133；_temp 下 .mjs 直连真实模型跑同一套 prompt，HTTP 200、约 1.1s 返回、四个 key 齐全且为纯 JSON；MySQL MCP 实测新计数 SQL 返回 total=23/false_count=0。

## Related files
- /anwei-fire-remote-monitor/module-ai/module-ai-biz/src/main/java/com/enjoyiot/module/ai/service/AlarmAnalysisService.java
- /anwei-fire-remote-monitor/module-ai/module-ai-biz/src/main/java/com/enjoyiot/module/ai/controller/admin/AlarmAnalysisController.java
- /anwei-fire-remote-monitor/module-ai/module-ai-biz/src/main/java/com/enjoyiot/module/ai/controller/admin/vo/AlarmAnalysisRespVO.java
- /anwei-fire-remote-monitor/module-ai/module-ai-biz/src/main/java/com/enjoyiot/module/ai/service/LlmClient.java
- /anwei-fire-remote-monitor/module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/dal/mysql/AlertRecordMapper.java
- /anwei-fire-remote-monitor/module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/firewatch/FireAlarmOrderServiceImpl.java
- /anwei-fire-remote-monitor-vue/src/api/ai/analysis/index.ts
- /anwei-fire-remote-monitor-vue/src/views/eiot/firewatch/alarm-order/index.vue

## Notes
- 复用层次的判断：研判与对话/月报共用 LlmClient（同一模型与配置），但没有复用 /ai/chat/completions —— 那条通道会自动注入工具提示词并最多跑 5 轮 [TOOL_CALL] 循环，适合"模型自己找数据"的自由问答；研判的数据集已由代码确定，需要一次调用稳定返回 JSON，混用会被工具协议污染并拖长等待。同理 AiChatApi.chatOnce 是给外部模块的门面（固定 temperature/maxTokens、无超时参数），调用方本身就在 module-ai-biz 内时不该再注入自己的 Api 绕一层。
- 面板只有几十 px 高、无法承载自由文本，因此另建结构化端点返回定形 VO，比复用对话接口再正则剥标记更稳。
- 设备在线判定依赖约 2 小时一次的报活心跳，窗口过短会把在线设备误判成离线。
- 模型输出的 verdictType 若不在 {fire,false_alarm,fault,verify} 闭集内一律回退规则结论，不接受未知标签。
- 后端 48080 进程启动时间早于本次编译且 server/pom.xml 无 devtools，新接口必须重启后端才生效，浏览器侧只能验前端热更新。
- 单模块编译 `mvn -o -pl module-ai/module-ai-biz compile` 会取本地仓库旧 eiot-biz jar 而报"找不到符号"，跨模块改动后须带 -am。

## Task overview
部分完成：后端研判服务/控制器/VO、LlmClient 超时重载、Mapper 计数、alertRecordId 回填、前端 API 与面板绑定全部落码，编译与 vue-tsc 零新增错误，模型返回格式与 SQL 均实测通过；结果暂存内存 10 分钟缓存，ai_analysis/ai_suggestion 两列仍未启用。端到端界面效果待用户重启后端后验证。改动均未提交 Git。
