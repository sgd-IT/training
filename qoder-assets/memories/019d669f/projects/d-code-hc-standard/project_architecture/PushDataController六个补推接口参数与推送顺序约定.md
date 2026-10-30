---
title: "PushDataController六个补推接口参数与推送顺序约定"
usage_scenario:
    - "手工补推某实体历史数据到园区平台"
    - "查找各对接实体的补推接口与参数"
    - "确认riskTask补推包含哪些步骤"
keywords:
    - "/push/workSpecial"
    - "rePushBatchToPlatform推送顺序"
    - "onlyUnsynced参数"
    - "riskTask八步顺序"
    - "失败明细返回格式"
source: "auto"
---

ruoyi-admin/src/main/java/com/ruoyi/web/controller/push/PushDataController.java（全文 155 行实测，2026-09-22）是一组"数据补推"运维接口，共 6 个 POST 接口，挂载在测试服务器 http://192.168.250.130:28080 下。共同调用约定（Bearer Admin-Token 鉴权、@RequestParam 传参、无前端入口、绕过同步状态的真实外呼风险）见《园区对接页面级配置：待推队列缺失、同步状态回写与补推鉴权约定》。

各接口参数与行为：
- /push/workSpecial —— startDate+endDate 必填（yyyy-MM-dd），onlyUnsynced 默认 true（只推 sync_status 未同步的），false 则范围内全量重推；逐条走 rePushBatchToPlatform，按序推 主数据→传感器(气体分析)→安全措施→审批流转→操作证→视频；主数据推送失败则跳过该条子数据；每条处理完更新 sync_status；返回 data 文本形如"总计N条, 成功N条, 失败N条"，有失败时追加"| 失败明细: 票号: 平台错误;"
- /push/workVideo —— startDate, endDate, onlyUnsynced；批量补推视频
- /push/riskObj —— 日期可不传（全量）；补推双控·分析对象
- /push/riskTask —— startDate, endDate；一键补推全部双控数据（四色图→分析对象→…→排查记录）。其中排查记录是第【7/8】步 PushDataServiceImpl.rePushRiskTaskRecord(startDate, endDate)，走 getRecordListByDateRange（仅按日期范围、不做 sync_status 过滤，与定时任务的 getSyncRecordList 不同源），最终逐条 POST /openapi/check_unit_data/troubleshooting；第【8/8】步为已验收隐患推送
- /push/hiddenTrouble —— 日期可不传（全量）；补推已验收隐患
- /push/contractor —— onlyUnsynced；补推承包商

此外，`ruoyi-admin/src/main/java/com/ruoyi/web/controller/base/ApiRiskController.java` 中的 `/api/risk/getTaskRecordList` 接口用于移动端查询排查任务列表，其核心参数 `getType` 是必填项，用于区分不同类型的排查任务。
