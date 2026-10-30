---
title: "采集端HttpUtils上报恒返回成功掩盖云端丢数据故障"
usage_scenario:
    - "排查上报无报错但云端无数据"
    - "改造采集端HTTP上报结果判定"
    - "设计推送链路成对对照实验"
keywords:
    - "HttpUtils恒返回true"
    - "上报成功假象"
    - "未来时间静默丢弃"
    - "入库核对验证"
    - "闸机事件上报排障"
source: "auto"
---

Bug class: 上报链路"看似成功实则丢数据"的静默故障
Root cause: fireworks-dhsdk-local 分支 src/main/java/com/netsdk/util/HttpUtils.java 的 post 方法在调用底层 HTTP 请求后无条件 return true，不校验响应状态码与业务返回体；同时 prod 后端 DaHuaService.savePersonInoutRecord 对 eventDate.after(new Date()) 的记录直接 return 丢弃但控制器仍返回 code 0 / "OK"。两层叠加使采集端日志显示上报成功、云端却无任何数据。
Fix pattern: 上报封装必须以响应体业务码（或 HTTP 状态）作为成功判据并返回可区分的结果，失败要打印完整响应；排查此类问题时不能以"接口返回 OK"判定入库，须直连数据库核对最新记录，或用"未来时间探测→正常时间对照→立即清理"的成对实验隔离后端校验逻辑。
Reusable lesson: 不要凭采集端"上报无报错"断定数据已入库，因为 HttpUtils.post 恒返回 true 会吞掉一切失败；应改为按响应业务码判定并落库核对。适用于 netsdk 采集端各 handler 的事件上报及任何"上报-入库"两段式链路排障；不适用于已有响应校验的上报实现。
