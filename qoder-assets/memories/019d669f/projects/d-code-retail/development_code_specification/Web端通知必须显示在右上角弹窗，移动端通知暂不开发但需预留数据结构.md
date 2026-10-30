---
title: "Web端通知必须显示在右上角弹窗，移动端通知暂不开发但需预留数据结构"
usage_scenario:
    - "当设计或修改 Web 站内通知功能时，确保弹窗位置符合用户要求"
    - "当为移动端通知做前期准备时，定义后端推送 payload 的通用数据结构"
keywords:
    - "web notification"
    - "right-top popup"
    - "mobile notification"
    - "payload structure"
    - "MessageService"
source: "auto"
---

Web端的通知必须显示在右上角弹窗（#icon-message 铃铛），前端已通过 push.ts 和 notice/index.vue 实现该链路；移动端通知尚未开发，但后端推送 payload 的 data 字段需预留结构（如 notificationType、alarmNo、retailId 等），以便未来 App 直接解析同一份消息体。
