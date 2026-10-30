---
title: "丹唛派克（DMPK）功能注释禁用全流程"
usage_scenario:
    - "需要临时或永久禁用某第三方集成模块（如支付、语音、短信）时参考此注释策略"
    - "多通道并行架构中下线某一通道（如从双音柱退化为单音柱）的代码清理标准流程"
    - "排查因配置残留导致的意外调用时，快速定位并注释整套模块代码"
keywords:
    - "丹唛派克"
    - "DMPK"
    - "注释禁用"
    - "多通道下线"
source: "auto"
---

## 任务描述
在分支切换后，彻底注释禁用丹唛派克（DMPK）语音播报功能，保留华皓音柱通道，实现双路播报退化为单路；或在新分支上启用并修复DMPK播报链路。

## 执行过程
```mermaid
graph TD
    A[需求:启用/禁用DMPK播报] --> B[全局grep搜索DMPK相关文件]
    B --> C[识别9个目标文件：4个Java类+1个接口+1个Controller+3个YAML配置]
    C --> D[逐文件分析DMPK耦合点：import/字段/方法/配置项]
    D --> E[生成标准化方案：Java用//注释块，YAML用#注释段]
    E --> F[分批执行SearchReplace：先AlramInfoServiceImpl.java核心逻辑]
    F --> G[再处理AlramInfoService/TestDanMaiController/DMPK系列类]
    G --> H[最后注释/启用3处application-prod.yml/application-dev.yml/deploy配置]
    H --> I[全量验证：GetProblems确认无语法问题]
    I --> J[关键验证：检查prod配置dmpk.isopen=true且deviceCode正确]
```

## 任务总结
成功完成DMPK功能双向操作：
- 禁用场景：9个文件全部按规范注释，包括服务层调用链、配置实体、API测试入口及三处环境配置；注释后系统仅保留华皓音柱播报。
- 启用场景：生产环境 dmpk.isopen=true，dmpk.deviceCode=9C0CA428，alarm-voice.repeat-count=5；AlramInfoServiceImpl.java和DanMaiPaiKe.java已修复并添加全链路日志。
