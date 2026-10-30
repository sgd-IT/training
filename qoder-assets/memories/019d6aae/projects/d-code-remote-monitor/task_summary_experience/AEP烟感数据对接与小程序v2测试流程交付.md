---
title: "AEP烟感数据对接与小程序v2测试流程交付"
usage_scenario:
    - "解读AEP烟感上报报文各字段含义"
    - "复现或参考AEP烟感数据对接测试方案"
    - "排查AEP告警未入库问题"
keywords:
    - "AEP烟感"
    - "物模型字段"
    - "eventType映射"
    - "小程序v2"
    - "测试流程"
    - "smoke_state"
source: "auto"
---

## Task description
- Core requirement: 优化AEP侧烟感报警器的数据与智慧消防平台的对接，可识别报警类型、烟温指标、设备状态；解决已知问题发布智慧消防小程序第二版（登录方式改为账号密码登录、无告警处置流程、无功能接口展示开发中）；提供完整的测试流程
- Task background: AEP烟感设备通过NB-IoT上报dataReport/eventReport；小程序v2需替换微信快捷登录为账号密码登录；告警中心需确认无处置入口；后端空壳接口需统一标识；测试流程需覆盖链路各环节并明确拦路项

## Execution process
1. 分析AEP烟感数据对接：确认AEP设备级回调URL被注释，仅产品级URL开放；梳理烟感物模型字段（smoke_value/temperature_value/battery_value）；验证eventType映射逻辑（eventType=1→fire）；确认deviceOnlineOfflineReport被忽略以避免PSM抖动
2. 设计小程序v2测试：账号密码登录链路（generate-login-nonce → mobile-login）、退出登录弹窗、tenantId存储清除；确认告警详情页无处置按钮；验证DevelopingTag在消息页/统计页/PC端的统一展示
3. 构建AEP测试用例表：9条dataReport/eventReport报文注入用例（烟雾报警/解除、温度过高、防拆、故障等），逐条验证alarm_type/alarm_level入库；核对去重窗口5分钟与租户隔离
4. 编写可执行测试流程：包含前置准备（查dn/product_key/keep_alive_time）、curl mock注入、TD/Redis/DB日志核对、真机联调确认、发布前检查单（版本/配置/白名单）
5. 输出测试流程文档：结构化为5个阶段（AEP对接、小程序登录、无处置流程、开发中标识、发布检查），含具体命令、SQL、检查点和拦路项

## Related files
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\service\alarm\AepHttpPushAlarmStrategy.java
- d:\code\remote-monitor\anwei-fire-remote-monitor\module-eiot\module-eiot-biz\src\main\java\com\enjoyiot\module\eiot\service\aep\AepCallbackServiceImpl.java
- d:\code\remote-monitor\anwei-fire-remote-monitor-mobile\pages\login\index.vue
- d:\code\remote-monitor\anwei-fire-remote-monitor-mobile\pages\alarm-detail\index.vue
- d:\code\remote-monitor\anwei-fire-remote-monitor-mobile\src\components\DevelopingTag\index.vue

## Notes
- None

## Task overview
成功交付AEP烟感数据对接与小程序v2测试流程：明确了AEP设备级回调URL注释这一关键拦路项；完成了小程序账号密码登录链路重构与UI精细化打磨；确认告警详情页为只读展示；统一实施DevelopingTag标识空壳接口；输出了一套可直接执行的、覆盖全链路的测试流程文档，包含前置准备、用例表、核对方法和发布检查单。
