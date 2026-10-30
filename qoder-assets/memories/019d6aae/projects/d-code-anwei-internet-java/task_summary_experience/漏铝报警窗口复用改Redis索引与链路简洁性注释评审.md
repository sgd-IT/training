---
title: "漏铝报警窗口复用改Redis索引与链路简洁性注释评审"
usage_scenario:
    - "再次调整漏铝msgId复用或报警时间语义时"
    - "继续执行漏铝链路分批重构清单"
    - "评审同类异步配对逻辑的注释质量"
keywords:
    - "alarm_time首次报警"
    - "窗口复用Redis索引"
    - "lyxl_well_msgid"
    - "PENDING单条收敛"
    - "漏铝链路简洁性评审"
    - "注释规范检查"
source: "auto"
---

## Task description
- Core requirement: 让漏铝报警记录的 alarm_time 回到首次报警时间，并对漏铝报警链路做代码简洁性与注释规范检查
- Task background: 设备一次物理漏铝期间约每 3~5 秒连推 N 次 status=0，但只上传 1 个 15 秒音频。此前"无音频不入库"改造把记录暂存到 Redis PENDING（不落库），而 Controller 的窗口复用判断用 getOne 查数据库，永远命中不到，导致每次 push 各生成新 msgId、各堆一条 PENDING，最终只有就近匹配到的末尾那条入库 —— alarm_time 因此偏晚最多约 59 秒，且消息推送重复 N 次

## Execution process
1. 逐段实读 JiagoVideoLeakageController.receiveDeviceStatus / videoFileUpload / retry 与 AluminumLeakageAlarmRecordServiceImpl.videoEventUp / setEventVideo / getVideoAlarmRecord，按时间线讲清当前修复逻辑：push 只 hset 池 + 写 PENDING，唯一落库点是音频到达后的 setEventVideo → saveOrUpdate
2. 定位 alarm_time 偏晚根因：alarm_time 在每次 push 里用 new Date() 赋值，而配对规则是"取离上传时间最近的 field"，必然命中事件末尾那次 push；截图两批数据偏差分别约 59 秒与 9 秒即为证据
3. 确认连带影响：videoOffAlarm 的 30 秒消警保护期以 alarm_time 起算会算错基准；配对用设备时钟 dto.getTime() 而 alarm_time 用服务器时钟，两个时钟源未对齐
4. Grep 核实 RedisUtils 方法签名，发现 set 走 Gson 序列化、get(key) 取 String 会带双引号，必须使用 get(key, String.class)
5. Grep 核实 DateUtil / StrUtil / AluminumLeakageAlarmRecord / QueryWrapper 在 Controller 内的其他引用点，确认删除窗口查询后 import 仍需保留
6. 修改 JiagoVideoLeakageController：新增常量 LYXL_WELL_MSGID = "lyxl_well_msgid:" 与 WELL_MSGID_WINDOW_SECONDS = 120（用户指定），把原 getOne 数据库窗口查询替换为 Redis 索引复用 —— 键为空则 RandomUtil.randomUUID() 并以固定 120 秒 TTL 写入（不续期），否则复用；日志改为"窗口内首个报警，新建 msgId"/"窗口内复用 msgId"
7. GetProblems 验证：仅剩两处原有的 ResponseDataVo raw type 警告，无新增编译问题；videoEventUp 与 setEventVideo 未改动，第二次 push 靠 getVideoAlarmRecord 命中 PENDING 后 return，天然不覆盖首次 alarm_time
8. 执行 code-conciseness-check + code-comment-style 评审：通读 4 个文件（JiagoVideoLeakageController 456 行、AluminumLeakageAlarmRecordServiceImpl 412 行、VideoLeakageAlController 147 行、LocalFileCacheSyncJob 147 行），Grep 确认 addNoNetworkCache 零调用、offAlarm 唯一调用处已注释、controlRangeService 仅被注释引用，输出 13 条简洁性问题 + 分文件注释问题表 + 分 4 批的动手顺序建议

## Related files
- /monkey-monitor-api/src/main/java/com/monkey/general/modules/mon/JiagoVideoLeakageController.java

## Notes
- 窗口复用改查 Redis 索引后同时解决三件事：alarm_time 取首次、PENDING 从 N 条变 1 条、消息推送从 N 次变 1 次；但 sendIpVoice 在 Controller 内独立调用不受 msgId 复用影响，音柱仍 N 次响，需另加播报开关
- TTL 取值依据实测最长事件时长（截图 59 秒），不可每次 push 续期，否则设备卡在 status=0 会无限复用同一条记录而不再产生新报警；事件超 120 秒会被切成两条记录
- 行为变化：同事件传多个音频时都指向同一 msgId，video_url 保留最后一个（改前是配到不同记录）
- 评审发现的既有隐患：monkeyLeakProperties.getIsStorePhoto() 与 subTitleW.getMode() 直接拆箱可能 NPE；videoFileUpload 在两个 Controller 各有一份约 40 行重复实现；syncNoNetworkCameraFile 的 setIsRecover(1) 在循环外与隔壁方法口径不一致
- 判空写法在同一文件混用四种（手写 ==null||isEmpty、ObjectUtils.isEmpty、StrUtil.isBlank），其中两处条件一字不差相隔 40 行

## Task overview
已完成：窗口复用由查库改为查 Redis 索引（TTL 120 秒），一次事件收敛为 1 个 msgId + 1 条 PENDING，alarm_time 恢复为首次 push 时间，消息推送不再重复；编译无新增问题。改动尚未提交。遗留：音柱重复播报未处理（待用户决定是否一并改）；简洁性与注释评审共 13+ 项问题按风险分 4 批，等待用户选择先做哪一批。
