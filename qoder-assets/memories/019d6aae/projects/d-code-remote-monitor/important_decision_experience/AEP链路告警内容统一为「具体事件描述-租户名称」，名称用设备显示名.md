---
title: "AEP链路告警内容统一为「具体事件描述-租户名称」，名称用设备显示名"
usage_scenario:
    - "修改AEP告警的名称或详情文案"
    - "排查告警内容显示格式问题"
    - "评估内容口径推广到其他链路"
keywords:
    - "告警内容格式"
    - "租户名称拼接"
    - "烟感报警文案"
    - "设备显示名"
    - "details口径"
source: "user"
---

【结论】AEP_HTTP_PUSH 链路写入 eiot_alert_record 的口径（2026-10-10 拍板）：details 统一为「具体事件描述-租户名称」（如"烟感报警-广东安为安全科技有限公司"），不再拼接设备编号与"| 来源:"尾巴；name 为「类型标签 - 设备显示名」（如"火警 - 展厅烟感"）；烟感事件描述从"烟雾报警"改为"烟感报警"。
【落点】AlarmMessage.buildDetails(tenantName)（租户名缺失回退旧口径"描述 | 来源"）；AlarmRecordService.resolveTenantName + buildAlertRecord/pushToWebSocket 共用；AepHttpPushAlarmStrategy.buildAlarmMessage 描述不再拼设备；AepCallbackServiceImpl 的 deviceName 取设备显示名、缺省回退 dn。
【理由与权衡】多租户联合值守需按租户名定位单位；设备编号对人工识别无意义；被否替代：仅改前端展示（历史与多出口不一致）、内容拼设备名（与"烟感"描述重复）。
【适用边界】仅 AEP 链路；摄像头/规则引擎/主机配置等其他写入链路内容格式维持现状，统一需另行拍板。
