---
title: "AEP烟感重复告警竞态诊断与告警内容租户化改造"
usage_scenario:
    - "排查AEP告警重复入库或去重失效"
    - "调整告警名称与内容展示口径"
    - "手敲训练模式下给方向性提示"
keywords:
    - "AEP告警去重"
    - "check-then-act竞态"
    - "dedupCache"
    - "告警内容租户名"
    - "javap产物核验"
source: "auto"
---

## Task description
- Core requirement：修复「展厅烟感」同一条一级火警在告警消息列表出现两条重复记录（未去重），并把「告警内容」列改为「具体事件描述-租户名称」口径。
- Task background：展示页为 anwei-fire-remote-monitor-vue/src/views/eiot/alarm/list.vue（列序＝租户名称/配置名称/产品名称/设备名称/级别/类型/告警时间/告警内容），数据源为 eiot_alert_record；AEP 实时链路经 AepCallbackServiceImpl → AlarmStrategyFactory → AlarmRecordService.saveAlarms 落库并推送，该路径原有 5 分钟内存去重窗口。

## Execution process
1. DB 取证（MySQL MCP anwei_fire_remote_monitor_test / mysql_query）：id 31573、31574 的 tenant_id=166、device_id=15、name/details 完全相同，alert_time 相差 3ms；另查到 31575（防拆报警）、31576（烟雾报警）为随后 15:25 的新测试数据，佐证竞态为偶发。
2. 设备核对：eiot_device_info id=15 → name="展厅烟感"、dn="99015381863555073280140"、model="烟感报警器"、tenant_id=166；确认 AepCallbackServiceImpl 构造 AlarmParseContext 时把 deviceName 传成了 dn，导致 name/details 里显示设备编号。
3. 根因定位：processPushMessage 标 @Async（注释即"确保 Controller 立即返回 HTTP 200"），两个推送＝两个线程；AlarmRecordService.saveAlarm 的去重是 check-then-act——isDuplicate 纯读、占位 dedupCache.put 排在 insert 之后，中间隔着整次落库构成竞态空窗。
4. 曾落地一版修复（isDuplicate 改为 ConcurrentHashMap.compute 原子判定+占位、insert 失败 remove 释放），编译通过并用 javap 验证产物含 lambda$isDuplicate$0；随后按用户"这个去重让我来写吧"整体还原为原版本，还原后再次 clean+compile 得 Compiling 544 source files / BUILD SUCCESS，当前代码＝竞态未修复状态。
5. 文案改造保留生效（仅 AEP 链路）：AlarmMessage.buildDetails(String tenantName) 输出「描述-租户名」，租户名缺失回退旧「描述 | 来源」；AlarmRecordService 新增 resolveTenantName(Long) 并在 buildAlertRecord 与 pushToWebSocket 两处共用；AepHttpPushAlarmStrategy 的事件描述不再拼设备且 smoke_state=0 改称「烟感报警」；AepCallbackServiceImpl 的 deviceName 取 StrUtil.blankToDefault(deviceInfo.getName(), deviceInfo.getDn())。
6. 环境坑排查：终端 PowerShell 原始读取源码得到密文样字节（头 88 7d 1c…）、工具新建脚本文件短时不可见，一度误判"编辑未落盘/编译的是旧源码"；改用 mvn clean+compile 看 Compiling N source files、javap -p 检查 target/classes 方法签名，证明编译对象就是新代码。
7. 用户手敲阶段的两次纠偏：① 提出"直接加一个 @Async 可以吗"→ 说明 saveAlarm 全仓仅声明处＋saveAlarms 内 this 自调用两处、无外部代理调用，注解是死代码，且 @Async 只改变执行线程不产生互斥，非 void 返回异步化会让调用方拿到 null；② 追问思路→ 给出 compute/merge 原子复合、putIfAbsent+比较替换、局部 synchronized 三方向及四个设计问题（占位语义、时间戳注释、失败释放、过期刷新），只给方向不给实现代码。

## Related files
- /anwei-fire-remote-monitor/module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/alarm/AlarmMessage.java — buildDetails 新增租户名参数与新口径
- /anwei-fire-remote-monitor/module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/alarm/AlarmRecordService.java — 新增 resolveTenantName、推送复用；去重修复写了又还原（现为用户手敲基线）
- /anwei-fire-remote-monitor/module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/alarm/AepHttpPushAlarmStrategy.java — 事件描述去设备编号、烟感改「烟感报警」
- /anwei-fire-remote-monitor/module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/aep/AepCallbackServiceImpl.java — deviceName 改用设备显示名并回退 dn

## Notes
- 「有去重」≠「去重在并发下有效」：判断重复类缺陷要看登记动作相对耗时操作（insert）的位置，而不是只看是否存在查重逻辑。
- 同类自调用会同时绕过 @Async 与 @Transactional 代理，注解在这类位置是静默失效的死代码；可用 Thread.currentThread().getName() 打印快速证伪。
- 本机文件视图差异要靠编译产物（javap）而非终端 Read/Grep 下结论，否则会误判改动丢失并触发无效返工。
- 教学式协作中用户偏好先自查再要提示：给方向＋坑点＋设计问句即可，贴出实现代码反而破坏训练目标。
- 历史重复记录 31573/31574 按用户选择保留不清理；改动未提交 git（需先展示清单与提交信息待确认）。

## Task overview
部分完成：文案口径改造已落地并通过 JDK21 clean+compile（544 文件 BUILD SUCCESS）与 javap 双重复核；去重竞态已完成根因确诊、修复方案与验收标准冻结，但实现按用户要求还原并由其手写，AI 侧转入四维评审待命；端到端实测受限于本地无运行实例（48081 未监听），需部署重启后再验证。
