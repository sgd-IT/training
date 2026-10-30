---
title: "丹唛派克播报请求JSON格式与设备编码校验规范"
usage_scenario:
    - "排查音柱未实际播报时核对设备编码来源"
    - "改造第三方硬件SDK入参为配置驱动fallback"
    - "评审代码时检查是否误传deviceSn充当音响编码"
keywords:
    - "丹唛派克"
    - "SendInfo1 JSON入参"
    - "音柱设备编码"
    - "响应code校验"
    - "配置fallback"
source: "auto"
---

## 任务描述
修复 Jiago 设备音频上报后丹唛派克 IP 音柱未实际播报的问题（日志显示"播报完成"但音柱无声）。

## 执行过程
```mermaid
graph TD
    A[现象:日志播报完成但音柱无声] --> B[定位JiagoDeviceService第51/63行]
    B --> C[问题1:SendInfo1传入纯文本而非标准JSON对象]
    C --> D[问题2:误将deviceSn'1908659908'当音柱编码传入]
    D --> E[核查application-dev.yml确认音柱真实设备码为'9C0CA428']
    E --> F[改用DanMaiPaiKe.SendInfo(text, deviceCodes)封装方法]
    F --> G[SendInfo支持null/空列表时自动读取配置deviceCode作fallback]
    G --> H[SendInfo1升级为boolean返回，严格以响应code==0判成功]
    H --> I[统一替换System.out为SLF4J日志]
```

## 相关文件
- /monkey-monitor-api/src/main/java/com/monkey/general/loulv/service/JiagoDeviceService.java
- DanMaiPaiKe.java（SendInfo / SendInfo1）
- /monkey-monitor-api/src/main/resources/application-dev.yml（loulv 音柱 deviceCode）

## 注意事项
- 音柱真实设备码是 '9C0CA428'（来自 application-dev.yml 配置项），与终端 deviceSn '1908659908' 是两个不同的标识，绝不可互相替代传入，否则接口静默失败。
- 首次 SearchReplace 修改失败导致文件损坏，必须先重读文件再精准修复。
- 播报方法必须返回 boolean 并校验响应 code，异常不得中断主流程。
- 该记忆不含"重复播报次数配置化"相关内容，后者见「语音播报重复次数配置修复与实现」。
