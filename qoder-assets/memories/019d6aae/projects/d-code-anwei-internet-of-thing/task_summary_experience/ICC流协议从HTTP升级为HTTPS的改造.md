---
title: "ICC流协议从HTTP升级为HTTPS的改造"
usage_scenario: []
keywords:
    - "Mixed Content"
    - "hlss"
    - "HTTPS升级"
---

## 任务描述
解决前端HTTPS页面加载HTTP流导致的Mixed Content错误，将ICC实时流与回放流统一升级为HTTPS协议（hlss/7096）。

## 执行过程
```mermaid
graph TD
    A[需求:修复Mixed Content] --> B[定位实时流协议配置点]
    B --> C[定位VideoStreamRequest.java#L60]
    C --> D[SearchReplace:注释hls行，新增hlss行并添加协议说明]
    D --> E[定位回放URL拼接点]
    E --> F[定位IccDeviceServiceImpl.java#L185]
    F --> G[SearchReplace:注释http行，新增https行并添加端口说明]
```

## 任务总结
成功完成流协议HTTPS化改造：在VideoStreamRequest中将data.setType("hls")替换为"hlss"并保留原注释；在IccDeviceServiceImpl中将host拼接从"http://...:7086"改为"https://...:7096"，同步添加清晰注释说明协议与端口变更，确保可追溯。
