---
title: "闸机出入数据修复选择仅改采集端不动prod后端"
usage_scenario:
    - "在无后端权限时定闸机数据修复方案"
    - "评估方向字段由哪一侧维护"
    - "决定人闸事件时间偏移处理方式"
keywords:
    - "只改采集端方案"
    - "dhsdk单侧修复"
    - "carInOrOut按设备配置"
    - "prod权限受限"
    - "人闸时差比对判据"
source: "auto"
---

Conclusion: 大华闸机"人闸无数据 + 车闸方向对不上"的修复只改采集端 fireworks-dhsdk-local 分支，不动平台后端 prod 分支——人闸去掉无条件 +8 小时偏移改为自适应写法（offset.before(now) ? offset : date），车闸方向由采集端按设备分别配置上报值（入口1/出口2）。
Rationale (trade-off): 用户明确无 prod 修改权限；且对照实验已证明只要上报事件时间非未来时间且 status 正确，后端全链路（含 em_gate_device_info 设备管理关联与 inOutState 取值）即正常工作，采集端单侧改动即可闭环，风险与协调成本最低。
Rejected alternatives: 后端统一以设备管理 device_type 决定车闸方向并删除 setInOutState(recordVo.getStatus()) 覆盖行——该方案与设备管理设计更一致、采集端无需重复维护方向，但需 prod 权限，且同一覆盖逻辑存在于 prod / fireworks-local / odlTest-fireworks 三个分支须同步修改，成本更高；后端把未来时间丢弃改为返回错误码仅提升可观测性，不解决根因。
Applicable / expiry conditions: 在"用户无 prod 权限、后端行为不可变"的前提下成立；一旦获得 prod 写权限或方向字段需在设备管理侧统一治理，应重新评估为后端方案（采集端 carInOrOut 配置届时可下线）。

## Notes
- 本条中记录的"自适应写法（offset.before(now) ? offset : date）"已被现场部署证伪：设备时钟比采集机快 30~48 秒时，加 8 后的时间被判为"未来"→ 不加 8 → 上报 UTC 值，云端显示早 8 小时（17 点测试显示 8 点）。
- 最新有效写法（已实测产出 Java 8 兼容包）：按"时差大小"判定 —— Math.abs(now - eventDate) > 4 小时视为 SDK 给出的 UTC 时间字段需 +8 还原北京时间，否则直接使用；最后再加一道防未来兜底（若结果仍晚于当前时间则取当前时间），保证不被后端当未来时间静默丢弃。
- 补充事实：大华设备本身时区配置为 UTC+8 且时钟正确，SDK 上报的时间字段仍是 UTC 化的，因此原始代码的无条件 +8 本来就是对的；问题只在设备/采集机存在几十秒级钟差时会触发后端"未来时间"校验。
- "车闸方向由采集端按设备分别配置"的实现载体已变更（2026-10-29，用户明确要求"这一次不做硬编码"）：先前落地为 EventHandler 内静态 CAR_IN_OUT_MAP 硬编码设备SN→方向，接第二家企业（3708324436）时暴露每家都要改代码重打包的维护缺陷；现改为纯配置驱动——DhConfig 增加 per-device `carInOrOut` 字段，代码遍历 dh.configs[] 匹配 deviceCode 取方向，未配置回退全局 dh.carInOrOut。取舍结论不变（仍只改采集端），但方向数据的唯一来源已是现场 yml，新增企业无需再动代码。
- 迁移代价（该方案的固定成本，必须注意）：删除硬编码映射后，所有已上线企业的现场 application-prod.yml 都要为每台车闸补 carInOrOut，否则该企业车闸方向退化为全局值；换企业/换车闸设备不再需要发版，但需要改现场配置并重启采集端。
