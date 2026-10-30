---
title: "嘉果报警记录与音频一对一匹配诊断及合并方案A设计"
usage_scenario:
    - "排查报警记录列表中音频缺失或匹配异常问题"
    - "设计重复上报事件的记录合并或去重方案"
    - "修改lyxl push与音频接口的关联逻辑前"
keywords:
    - "报警记录"
    - "音频匹配"
    - "msgId"
    - "Hash池"
    - "就近匹配"
    - "窗口复用Redis索引"
    - "lyxl_well_msgid"
    - "无音频不入库"
    - "alarm_time取首次"
source: "auto"
---

## Task description
- Core requirement: 排查嘉果（Jiago）漏铝监控中"最近报警记录只有一条有音频文件"的原因，并给出处理方案
- Task background: /lyxl/push 设备状态推送约3~5秒一次，每次status=0都创建独立报警记录；/api/lyxl/file 音频上传为独立接口；报警记录列表页大量记录显示"暂无音频"，仅每批最新一条有音频

## Execution process
1. 定位日志出处 JiagoVideoLeakageController.receiveDeviceStatus：日志中 List<DeviceStatusItemDTO>:3 打印的是 dto.getData().size()（子设备状态项个数），非列表内容
2. 通过 git log 定位历史修复 commit d986dd2「嘉果设备音频msgId改用Hash池就近匹配」：旧版 redisUtils.set 单key每次push覆盖msgId，改为 Hash池（field=设备time, value=msgId）+ 精确/就近匹配 + hdel消费
3. 结合截图规律（每批4条记录仅最新一条有音频）确认真因：一次物理报警设备连推N次status=0生成N条记录，但设备端一次事件只上传1个音频，就近匹配后仅1条记录拿到音频——是"1音频 vs N记录"的结构性结果，非覆盖bug
4. git status/git diff 发现工作区存在未提交的"无音频不入库"实现：videoEventUp 由 save() 改为暂存 Redis（lyxl_pending_alarm:，TTL 10h），setEventVideo 音频到达时才真正入库；配套 LocalFileCacheSyncJob/VideoLeakageAlController 改用 getVideoAlarmRecord()（查库+PENDING兜底）
5. 设计推荐方案A「同事件合并记录」：videoEventUp 创建记录前先按 casting_wells_id + alarm_type=3 + action_status=0 + 30~60秒窗口查询已有记录，命中则不新建，仅将本次push的池field的value指向已有记录msgId；澄清方案A是根本不创建冗余记录，而非入库后过滤
6. 评估msgId语义变化连带影响：核心链路（音频上传/断网补偿/记录查询均按msgId查记录）不受影响，需额外确认前端与统计模块是否假设msgId per-push唯一

## Related files
- /monkey-monitor-api/src/main/java/com/monkey/general/modules/mon/JiagoVideoLeakageController.java
- /monkey-monitor/src/main/java/com/monkey/general/modules/mon/service/impl/AluminumLeakageAlarmRecordServiceImpl.java
- /monkey-monitor-api/src/main/java/com/monkey/general/data/table/LocalFileCacheSyncJob.java

## Notes
- 用户对根因的记忆（"音频文件被覆盖"）只对了一半：旧版被覆盖的是Redis中的msgId引用而非音频文件本身，且该问题已由d986dd2修复；排查此类问题应先用git log -S与git show核对历史修复，再对照数据规律验证当前假设
- 验证"设备每批实际上传几次音频"的方法：grep服务端日志关键字"接收到音频文件"
- 方案A已实施（不再是待决策状态）：窗口复用判断放在 Controller.receiveDeviceStatus 生成msgId之前（而非原设计的 service 内），命中则复用已有msgId；Service.videoEventUp 在创建前用 getVideoAlarmRecord(msgId) 做二次存在性判断以防并发，已存在则直接return。原因是 controller 需先确定 msgId 才能 hset 池值，把查库放 controller 改动最小。效果：一次事件=一条记录+一条音频。
- PENDING（无音频不入库）改动未回退，与方案A共存；其代价（音频不到则记录丢失、弹窗延迟、统计少算）仍存在，待用户决定是否恢复 this.save()。
- 后续会话补充（消警机制查证）：漏铝报警表 mon_aluminum_leakage_alarm_record 的消警唯一实际入口是前端监控页轮询 todayAlarm 接口触发 videoOffAlarm，规则为每次轮询仅消1条最新未处理报警且创建超30秒保护期才消，action_status/processing_status 置1并写 eliminate_time；热成像（alarmType=0）分支的消警调用已被注释不会自动消警；offAlarm 批量消警所有调用处均已注释属死代码；无人开页面时报警持续积压。感知数据表 alarm_information 的消警为另一套体系（阈值恢复/ZzqjMethod/AlarmEliminationMethod.allClearAlarm），互不影响。
- 【后续会话纠正一】上文"方案A效果：一次事件=一条记录+一条音频"不成立。实测依据：videoEventUp 在无音频时只把记录写入 Redis PENDING（不再 save），而 Controller 的窗口复用判断用 getOne 查的是数据库，第二次 push 在库里查不到第一次记录，因此窗口复用实际失效，仍每次生成新 UUID 并在 Redis 堆 N 条 PENDING（音频不到即 10h 后静默过期、报警留痕丢失）。真正消除列表"暂无音频"行的只有"无音频不入库"这一层；副作用是 createRecordAndPushMsg 消息推送与 sendIpVoice 音柱播报仍按每次 push 重复触发。若要修好复用，需把窗口判断改为同时查 PENDING。
- 【后续会话纠正二】上文"PENDING 改动未提交、待用户决定"的状态已过期：git log 核实两个改动均已提交到 master 并推送 origin —— 757d3d8（嘉果状态上报窗口内复用msgId）与 8d153ae（漏铝报警记录统一查询并改为音频上传后落库），提交时间 2026-10-16 10:54，本地 jar 于 10-16 11:10 打包。
- 【后续会话补充三】现场配置事实（青海桥头铝电，端口 9090，customer id 377）：spring.redis.database 现场为 1、开发环境为 8，两套环境的 msgId 配对池与 PENDING 记录互不可见，跨环境调试须重新触发 push；现场 yml 只配 max-file-size: 10MB 且缺 max-request-size；monkey.leak.preSignalDelayEnable=false、leakAlarmNormal=1、leakAlarmDemo=1、isStorePhoto=false、resetEncoding=ax0033；dmpk.dmpks[0].castWellId=118 必须改成目标铸造井 ID 否则音柱不响（仅影响播报，不影响记录建档）。现场配置里 spring.datasource.hikari 与 spring.redis.jedis.pool 两段写法无效（正确为 spring.datasource.dynamic.hikari.min-idle/max-pool-size 与 spring.redis.lettuce.pool）。
- 【后续会话补充四】若线上列表在修复提交之后仍出现一批多条仅一条有音频，优先判定服务未部署新 jar（新代码不会产生无音频行）；可用日志关键字"窗口内已有未消警记录，复用 msgId"/"记录已存在，跳过创建"是否存在，或 SQL 按 msg_id GROUP BY HAVING COUNT(*)>1 来区分新旧版本。配置齐了却 0 条数据的最大嫌疑是 getCastingStatus 的 signal 恒为 0：需 iot 库 range 信号点位、em_device_realtime 实时值、以及 num in (ax0024, ax0048) 冷却水泵至少一条 d_value=1 同时满足，任一缺失整个报警分支静默跳过不打日志。
- 【后续会话纠正三·最新权威状态，以上文相关表述为准绳覆盖】"方案A已实施、一次事件=一条记录"及纠正一所述"复用失效"均已被新一轮改造取代：窗口复用已从 getOne 查数据库改为查 Redis 索引 lyxl_well_msgid:{castingWellId}，键不存在则生成新 UUID 并以固定 120 秒 TTL 写入（不随后续 push 续期），存在则复用同一 msgId。因此一次物理事件收敛为 1 个 msgId + 1 条 PENDING，alarm_time 取首次 push 的时间（后续 push 在 videoEventUp 内被 getVideoAlarmRecord 命中 PENDING 后直接 return，不覆盖已有字段），createRecordAndPushMsg 消息推送从 N 次降为 1 次。取值必须用 redisUtils.get(key, String.class)——RedisUtils 走 Gson 序列化，不带 class 会取出带引号的字符串。残留边界：同事件传多个音频时 video_url 保留最后一个；事件持续超 120 秒会被切成两条记录；sendIpVoice 仍按每次 push 各播一次（未去重）。
