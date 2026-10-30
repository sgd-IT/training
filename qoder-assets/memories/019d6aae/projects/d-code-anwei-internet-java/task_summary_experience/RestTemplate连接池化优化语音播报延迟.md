---
title: "RestTemplate连接池化优化语音播报延迟"
usage_scenario:
    - "语音播报类服务中HTTP客户端性能瓶颈分析与优化"
    - "类似丹唛派克等第三方API调用频繁场景的RestTemplate复用改造"
    - "排查HTTP调用延迟时关注连接建立开销"
keywords:
    - "RestTemplate"
    - "连接复用"
    - "语音播报延迟"
---

## 任务描述
优化语音播报链路中因频繁新建RestTemplate导致的TCP握手延迟问题，通过Spring Bean化RestTemplate并复用连接池降低现场播报延迟。

## 执行过程
```mermaid
graph TD
    A[需求:降低语音播报现场延迟] --> B[添加耗时日志定位瓶颈]
    B --> C[识别RestTemplate每次new导致重复TCP握手]
    C --> D[新建RestTemplateConfig.java注册Bean]
    D --> E[修改DanMaiPaiKe.java注入RestTemplate]
    E --> F[验证连接复用生效]
```

## 任务总结
成功将RestTemplate改造为Spring管理的单例Bean：新增RestTemplateConfig配置类（含5s超时与SimpleClientHttpRequestFactory），修改DanMaiPaiKe构造函数注入，消除每次播报重建HTTP连接的开销，预计节省200-400ms延迟。
