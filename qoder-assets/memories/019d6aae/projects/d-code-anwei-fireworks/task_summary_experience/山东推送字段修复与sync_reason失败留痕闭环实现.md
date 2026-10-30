---
title: "山东推送字段修复与sync_reason失败留痕闭环实现"
usage_scenario:
    - "改造山东SD平台推送字段或失败处理逻辑"
    - "为推送链路增加结果与原因留痕字段"
    - "排查省平台数据推送后不再重推问题"
keywords:
    - "SD推送"
    - "sync_reason"
    - "access_status重置"
    - "deviceId映射"
    - "批次失败定位"
    - "在线DDL"
source: "auto"
---

## Task description
- Core requirement: 对照《烟花爆竹安全生产风险监测预警接口技术文档1.1》审查山东(SD)推送实现的缺口，并依次落地：字段级修复 → 每张表增加"同步原因"字段留痕 → 失败回滚值改为0 → 部分失败也精确重推
- Task background: SD推送实现在 monkey-monitor 的 platform/push/sd 包（PushingSDDataService / SetSDUploadInfo / RequestSDMethod / SDAuthBuilder），原有失败明细只落日志无法追溯；原 rollbackAccessStatus 把 access_status 置1，而所有查询条件是 eq(access_status, 0)，导致失败数据永久不再重推

## Execution process
1. 缺口审查产出三类问题：字段取值/格式错（2.7/2.8 deviceId 误用 deviceCode、2.5 storeroomId 拿 storeId 顶替、2.4 acreage/dosage 与 2.5 upper/lower 未做小数格式化、图片为空仍发 ""）；数据生命周期缺口（删除不同步、修改不重推、部分失败静默丢数据、业务id为null时用随机UUID兜底破坏幂等）；协议层无防护（413/403/425、时区依赖JVM默认）
2. 确认 update 时间字段：11张表都有，但命名两种 —— update_date（em_information/em_warehouse/em_storeroom/em_personnel/em_device_info/em_server_info）与 gmt_modified（em_alram_info/em_temperature/em_humidity/em_person_in_out_info/em_car_in_out_info）；MyMetaObjectHandler.updateFill 只在带实体的 update（updateById）时触发，纯 UpdateWrapper.set(...) 不填充，故推成功后改 access_status 不会误刷 update_date，"修改重推"可直接用 update_date 判断，无需加列
3. SetSDUploadInfo.java：deviceId 改用 buildDeviceCodeToIdMap 映射出的设备UUID，匹配不到即跳过并warn；新增 formatTwoDecimalsPlain（只补两位不做 +0.01，用于量程/实测值）与 formatSixDecimals（经纬度）；imageFile/alarmFile 为空时跳过该条，企业信息仅告警不跳过（跳过会让下游全部关联不上）；各组装方法新增 Map<Long,String> skipReasons 出参回传跳过原因（温湿度因温度/湿度是两张表，拆成 tempSkipReasons / humSkipReasons 两个map避免主键冲突）
4. RequestSDMethod.java：uploadData 返回 UploadResult(batchId, errorMsg) 而非裸 String；HTTP 状态码翻译成可落库文案（413请求体超15M、425时间戳过时需查时区、403限流等）并带上响应体前200字；403 也纳入限流重试（文档写403、实测有429）
5. 新建 deploy/init/sd-sync-reason.sql：11张表 ALTER ADD COLUMN sync_reason varchar(500) DEFAULT NULL，配 ALGORITHM=INPLACE, LOCK=NONE 在线DDL；初版曾设计 sync_result+sync_reason 双列，用户明确"结果沿用access_status，只加原因列"后撤销 sync_result
6. 11个 em 实体在 accessStatus 后新增 private String syncReason（Lombok @Data 自动生成getter/setter，必须先声明字段再在业务代码引用）
7. PushingSDDataService.java：新增 markFailed(service, list, reason, retry) 统一写原因入口（retry=true 时顺带 set access_status=0）、clearReason（批次全成功时 set null 防旧原因误导）、excludeSkipped（把本地跳过的记录从 updateAccessStatus 名单剔除，否则跳过记录被标已上传永不重试，同时保证批次"第N条"下标对得上真正发出的顺序）
8. 回滚语义修正：rollbackAccessStatus 改名 resetAccessStatus，set("access_status", 0) 而非1，并在注释中写明坑防止被改回
9. 部分失败精确重推：能解析 errorContent 中"第N条"时只把那几条置0+各自原因，其余保持2；解析不到条目时只写原因不置0（整批置0会让已成功记录重复入库），并 warn 提示人工介入
10. mvn -o -pl monkey-monitor -DskipTests compile 每轮均 BUILD SUCCESS

## Related files
- /deploy/init/sd-sync-reason.sql
- /monkey-monitor/src/main/java/com/monkey/general/platform/push/sd/PushingSDDataService.java
- /monkey-monitor/src/main/java/com/monkey/general/platform/push/sd/SetSDUploadInfo.java
- /monkey-monitor/src/main/java/com/monkey/general/platform/push/sd/RequestSDMethod.java
- /monkey-monitor/src/main/java/com/monkey/general/modules/em/entity/Information.java
- /monkey-monitor/src/main/java/com/monkey/general/modules/em/entity/Warehouse.java
- /monkey-monitor/src/main/java/com/monkey/general/modules/em/entity/Storeroom.java
- /monkey-monitor/src/main/java/com/monkey/general/modules/em/entity/DeviceYhInfo.java
- /monkey-monitor/src/main/java/com/monkey/general/modules/em/entity/AlramInfo.java
- （另含 Personnel / ServerInfo / Temperature / Humidity / PersonInOutInfo / CarInOutInfo 共11个实体同构改动）

## Notes
- 表名易错：设备表实际是 em_device_info（实体类名为 DeviceYhInfo），不是 em_device_yh_info
- 上线顺序必须"先执行DDL再发代码"：实体已映射 sync_reason，列不存在时 MyBatis-Plus 查询直接报 Unknown column 'sync_reason'
- JDK 选择：项目锁定 Java 1.8，须设 JAVA_HOME=D:\Tool\jdk\jdk1.8 后编译；用 JDK21 会因老版本 Lombok 报错（环境问题非代码问题）
- 数据库核验受阻：mcp.json 中 anwei_fire_remote_monitor_test 与 anwei-fire-remote-monitor-prod 均为 disabled:true，命令行直连测试库 175.178.237.103:26671 报 ERROR 2003(10060) 超时，故 update_date 空值率未能用真实数据核对，仅得代码级结论
- deviceType=8（温湿度一体）不在 saveDevice 的 1/2/3/4 推送范围内，其温湿度数据修好 deviceId 映射后会被跳过并warn——原先被"错发deviceCode"掩盖，属暴露而非新引入，需业务确认处理方式
- 遗留未处理：删除同步（deleteFlag=1 的数据永远进不了推送链路）、batchId 未持久化、business id 为 null 时随机UUID兜底破坏幂等、413 无按字节动态降 batchSize 防护

## Task overview
已完成并通过编译：SD推送字段级修复（deviceId用UUID、小数格式、图片必填校验+跳过原因回传）、11张表新增 sync_reason 留痕（DDL待用户执行）、失败回滚由1改为0使全批失败可重推、部分失败按"第N条"精确定位置0重推。结果为完成（DDL执行与线上验证待用户侧进行）；删除同步与修改重推仍是后续待办。
