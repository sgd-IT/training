---
title: "摄像头报警alarm_type分类在alarm-order链路不透出，按level归类为火警"
usage_scenario:
    - "排查热成像等摄像头报警在告警工单里显示为火警"
    - "给alarm-order接口补充报警类型分类字段"
    - "判断某类报警走哪个前端入口展示"
keywords:
    - "reportCameraAlert"
    - "CameraEventTypeEnum"
    - "alarmCategory未赋值"
    - "mapAlarmTypeToLevel"
    - "level与alarm_type正交"
source: "user"
---

摄像头类报警（热成像/烟火/高温/离岗/通道占用）统一由 DeviceAlertConfigServiceImpl.reportCameraAlert 写入 eiot_alert_record，落库时同时写 alarm_type（0=主机 1=高温 2=热成像 3=烟火 4=离岗 5=通道占用）和 level（CameraEventTypeEnum 第二参数；W3 改造后该枚举 level 字段已无消费者）。

反直觉点：alarm-order（火警处置中心）链路只消费 level，不消费 alarm_type。
- FireAlarmOrderServiceImpl.convertToRespVO 用同一列 level 同时算两个出口：vo.setAlarmType(mapLevelToAlarmType(level))（"1"→fire）与 vo.setAlarmLevel(mapLevel(level))（"1"→critical）；第 4 步已补 vo.setAlarmCategory(record.getAlarmType())（来源 0-5 出口归位，第 3/4 步随批完成）。
- AlarmOrderRespVO.alarmCategory 已按"来源 0-5"语义被三处消费（FireDashboardServiceImpl:598、移动端 alarm-detail、monitoring-center:905/983）。
- 前端双形态兼容映射（'1'⇒critical 等）第 4 步已收敛：duty-screen/remote-console 删英文分支为纯数字（数据源核实）、monitoring-center 保留双形态（firewatch 数字/alarm-push 英文双通道必需）、alarmSpeech.ts 类型名改 alarmKind 优先、card.js 重构 typeOf/levelOf 双读。
- 部件类型识别链路缺失：报文含部件类型码（40/30/23），但 AlarmMessage.ComponentInfo 无构建点、AlertRecordDO 无部件类型字段、无子设备台账表；部件类型仅以文本散落 details（15 条"部件类型：10"、均为烟感类，生成代码不在本仓库）。
- 存量链路混流实测（2026-10-08）：同一事件存在两条写入链路两种编码——热成像 type=2/level="1" 29,347 条 + type=0/level="2" 922 条；烟火 118+115；离岗 43+111（"离岗报警"）；另有三合一 247、摄像头事件报警 69。level=2 的 1,664 条中 1,597 条实为视频类，真正故障仅 67 条。存量迁移必须按事件名称逐项映射，不能按 alarm_type 分类。
- 部件事件实测：压力开关（99）/消火栓按钮（24）/水流指示器（96）/温感/手报均 0 条，从未收到过。

## Notes
- alarm_type 与 level 是正交两维（2026-09-28/10-08 MCP 查库确认）。
- 【目标态口径已定】用户拍板：`level` 固定为报警等级，闭集 {1,2,3}，**1 级为最大优先级**（有序维度；"只升不降"提级=取数值更小者 min，禁止取 max）；原 1-6 的报警类型新开字段承载（alarm_kind，不复用 alarmCategory）。
- 【三级语义定稿】一级 = 火灾迹象探测类（火警/烟感/温感/手报/热成像/烟火/高温）；二级 = 其余类型兜底（隐患、离岗、通道占用）；三级 = 故障类为主（故障、离线）。存量迁移（按名称逐项映射，实测修正）：一级 31,150 / 二级 295 / 三级 118（此前按 alarm_type 估算的 29,797/92/1,674 已作废）。
- 【档位定稿】一级内部四档按"确认依据的可靠程度"：Ⅰ=人工直接触发（手报/消火栓，收到即确认）；Ⅱ=两个独立信号"与"逻辑（第二信号到达即确认）；Ⅲ=视频远程复核（60s 判画面）；Ⅳ=无手段只能到场（3min）。运行时判定（触发源+视频覆盖+多信号），台账不存档位（存原料），告警记录存快照；多信号取最高档；自动确认仅Ⅰ/Ⅱ档。
- 【联动范围收缩】"目前联动先不考虑"：联动执行记录表/公式表/自动下发/兜底联动整体移出本期；Ⅰ/Ⅱ档先落"收到即自动确认"。
- 【老逻辑保留拍板 2026-10-08】读侧回退分支（旧语义解释通道）默认保留、不清理，六步切换的第 6 步降级为可选。理由：它是 31,563 条存量数据的唯一解释通道；新旧数据区分靠行级判据（alarm_kind IS NULL=旧、非 NULL=新）；将来全量回填完成且回退分支长期零命中后，再单独决定是否物理删除。重要实证：旧回退出口 mapLevel 对 '4'/'5' 产 'info'——所以 alarm-order:1069 info→C、duty-screen:909 alarmLevelText info 键、monitoring-center:822 levelText info 键均**必须保留**（定稿曾写"info 分支删除"，执行时修正为保留）。
- 【执行进展 2026-10-08】第 1 步（加列留痕）完成：DDL 落库（alarm_kind/tier tinyint NULL，31,563 条全 NULL 已校验）+ 脚本 V20261008__alert_record_add_alarm_kind_tier.sql + DO/VO + 编译通过。第 2 步（读侧双读）完成并真编译（Compiling 541 文件 BUILD SUCCESS）：AlertRecordMapper 8 处 SQL 改数字域 `COALESCE(alarm_kind, CAST(level AS UNSIGNED))` 双读（含补录 R2b 月报 PDF 导出、R6b mapKeyAlarms）；FireAlarmOrder/FireDashboard 双读分支（mapAlarmKindToAlarmType/mapLevelNew + setTier）；级别筛选与 selectPage:56 保持不动；SQL 等价性已在真实库逐格验证。第 3 步（写侧三写 W1-W5+R8）完成并真编译（Compiling 542 文件，含新增 AlarmSemantics）：W1 AlarmRecordService（归一→kind、定级、tier manualⅠ/compositeⅡ/其余一级Ⅳ兜底、saveAlarm 返回 DO、WS 双 payload 增 alarmKind/tier）；W2/W3 DeviceAlertConfigServiceImpl（配置 1火警/2故障/3其他→信息；摄像头 1-3→火警Ⅲ档、4/5→隐患）；W4 createAlarmOrder 人工触发一级Ⅰ档；W5 补录 AlertConfigServiceImpl.addAlertRecord+listener。第 4 步（前端收敛 F1-F9+M1-M2）完成：PC 7 文件——duty-screen（getPopupSeverity 纯数字+队列补 alarmKind）、monitoring-center（保留双通道形态+两处队列补 alarmKind）、alarm-record（类型双读、fireRatio 删 level='1' 判据）、remote-console（getAlertLevel 纯数字）、AlarmLogPanel（getAlarmTypeText 加 alarmKind 双读）、alarmSpeech.ts（LEVEL_TEXT_MAP→KIND_TEXT_MAP、levelToChinese 收对象 alarmKind 优先）、deviceRemote.api.ts（AlertRecordVO 补 alarmKind）；移动端 card.js 重构（typeOf：alarmKind→alarmType 英文→level 旧编码回退；levelOf：level→alarmLevel 换算；ALARM_LEVEL_TO_LEVEL 删 info 键；修正 '3'→监管/'4''5'→屏蔽错误表）；后端补 setAlarmCategory。F3/F6/F7/F9 经逐处数据源核实**维持**（alarm-popup 多入口共享组件属展示归一；alarm-screen 统计由第 2 步出口驱动、secondary 统计为既有死逻辑；deviceinfo 数值域档位新旧一致）。验证：vue-tsc 本次改动文件 0 错（仓库既有历史错误不计）、后端 clean 编译 542 文件 BUILD SUCCESS。【部署验证 2026-10-08 首批】W1 真机实测通过：用户触发烟感（拆装防拆+烟雾测试），AEP 侧 4 条消息本地落库 2 条——防拆（tamper_alarm=1）走事件通道记"警告事件"（kind=3/level"2"/tier NULL）、烟雾（smoke_state=0）走数据通道记"烟雾报警"（kind=1/level"1"/tier 4）；恢复态（smoke=1/tamper=0）不生成；duty-screen 弹窗（隐患+较重、火警+紧急）与落库出口一致；同设备旧记录（kind=NULL）双读回退正常。【事件通道具体化增强 2026-10-08 已实施】parseEventReport 原不解析 eventContent（防拆细节丢失、记泛化"警告事件"，用户要求具体展示）——现优先用 eventContent 走与 dataReport 共用的属性判定（新抽 alarmsFromAttributes）：防拆→"防拆报警"、烟雾→"烟雾报警"、故障→具体故障名（迷宫污染故障等）；恢复态不生成（与 dataReport 口径一致）；无法识别回退泛化"警告事件"/"故障事件"（行为不变）；烟雾类 eventReport 从 risk/warning（隐患/二级）修正为 fire/critical（火警/一级，与数据通道归一一致）；两通道同事件 dedupKey 相同（device:alarmType:source）天然去重不重复入库；eventContent 兼容对象与 JSON 字符串两种形态。编译 542 文件 BUILD SUCCESS，待重启后重触发防拆验证（预期弹窗详情显示"防拆报警"）。待验证：W2/W3/W5 链路造数。【告警消息列表页收敛 2026-10-08 验证期补录】eiot/alarm/list.vue（告警中心→告警消息，数据源 /alert/selectAlertRecordPage 裸 AlertRecord、无出口投影）："告警类型"列原为空壳（仅 label 无绑定）；"告警等级"列原挂旧编码选项翻译（1火警…5其他，新数据 level=2/3 会错显"故障/离线"）；"是否已读"列绑定 key=read（字段实为 readFlg，已读也显未读）。现改为：类型列接双读加工字段 typeText（新=alarmKind 六值表、旧=level 旧编码五值表）；等级列改等级显示（新=一级/二级/三级、旧=—）；已读列修正 readFlg；搜索选项改一级/二级/三级（混存期按 level 精确匹配，旧数据按旧编码命中）。vue-tsc 全量该文件 0 错。定稿 4.4 已补录 F10。【两处待收敛点清零 2026-10-08】①主机配置页 alarm/config.vue：列名"告警等级"→"告警类型"、选项按库内 18 条配置名称逐条核实校正为 1火警/2故障/3电源故障/4离线/5其他（与 alertConfigLevelToKind 归一一致）；②设备详情 detail.vue 告警记录筛选改新语义等级（一级/二级/三级，与告警消息列表口径一致），同行表格"X级"tag 列经核维持（新数据 1级/2级/3级+颜色档位一致）。定稿 4.4 补录 F11/F12。【list.vue 跟敲评审 2026-10-08】用户按教程手敲重写 list.vue（保留其命名 KIND_TEST_MAP/OLD_KIND_TEST_MAP 与排版），评审修出 5 处：①Record<String,String> 大写 String×3（TS2344 编译错）；②row.alarmKind.id 多打后缀（编译抓不到，旧数据 null 行运行时抛异常）；③旧回退分支误用 row.alarmKind（应为 row.level，双读回退取错字段）；④"告警类型"列 key 敲成空串（加工了字段但列没接）；⑤"告警等级"展示列整列漏敲（levelText 无列消费）＋占位符 ' ' 应为 '—'。修复后 vue-tsc 三文件零新增、总行数回基线 481。用户为学习型：会要求"给代码跟着敲"，交付后宜给评审复盘（编译器能抓的 vs 只能靠运行时/自查的）。
- 【定稿文档】《消防告警字段语义定稿》v0.2（d:\code\remote-monitor\消防告警字段语义定稿.md）：第一章语义定义、名称映射表、迁移 SQL、消费点打勾清单（W1-W5 全 ☑、R1-R8 已打勾、R9 无需改）、六步切换（第 1-4 步标已执行、第 6 步已降级可选）、7 项待拍板。
- 用户强调施工重心"语义统一"，对策：单一判据（新字段 IS NULL=旧语义）、读侧先于写侧分层原子切换、消费点清单+每步对账；前端侧同样贯彻"双读"（alarmKind 优先、旧编码回退），删除分支前必须逐处核实数据源形态。
