---
title: "误以为删除本地设备可触发AEP自动重录产品与设备"
usage_scenario:
    - "规划AEP设备接入或迁移的执行顺序"
    - "排查删除设备后AEP未重新注册"
    - "评估自动注册后的必补配置项"
keywords:
    - "addDeleteDevice触发源"
    - "AEP自动注册"
    - "本地删除无回调"
    - "keep_alive_time与物模型补齐"
    - "dn唯一索引"
source: "auto"
---

Bug class: 第三方平台回调驱动的自动注册被误认为可由本地侧动作触发。

Root cause: AepCallbackServiceImpl 的 handleAddDeleteDevice 是被动消费方，addDeleteDevice 消息只在 AEP 平台侧发生设备新增/删除动作时才推送；本地 eiot_device_info 删除记录（deleteDeviceInfo → deviceInfoMapper.deleteById 逻辑删除）不产生任何出向通知，因此"删掉本地设备就会让 AEP 重新录入产品和设备"不成立。同时 delete 分支写作 deviceInfoService.deleteDeviceInfo(Long.valueOf(aepDeviceId))，把平台 deviceId 当本地主键 id 使用：长数字 ID 抛 NumberFormatException 被外层 catch 吞掉仅打日志，短数字 ID 会误删本地同 id 设备。

Fix pattern: 需要自动注册时，触发点必须在平台控制台（优先"新增设备"方式，避免"删除后重加"以绕开 delete 分支的类型误用）；注册产出物需人工补齐三项才可用——产品 keep_alive_time 由默认 0 改为实际心跳周期、产品物模型导入平台侧字段定义文件（否则属性数据被 DevicePropertyHandler 过滤丢弃）、设备 tenant_id 由 0 改为目标租户（否则告警不可见）。本地旧设备与自动注册的新设备 dn 天然不同，可共存，应待新链路验证通过后再清理旧设备。

Reusable lesson: Don't 删除本地设备来期待上游平台重新推送注册，因为推送只由平台侧增删动作触发，本地删除静默无副作用会导致空窗期且新数据不会进来；instead 在平台侧触发新增并先注册后清理。适用于由外部平台回调驱动的自动注册链路（AEP/第三方 IoT 平台）；不适用于本地主动调用平台 API 的正向注册流程。另注意 deleteDeviceInfo 为逻辑删除，软删记录仍占用唯一索引，复用同一 dn 录新设备会被索引挡住。
