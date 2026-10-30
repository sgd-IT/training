---
title: "修复AI分析页面因响应结构不匹配导致的调用失败"
usage_scenario:
    - "前端调用后端API返回undefined但后端日志显示成功时的诊断流程"
    - "Vue项目中处理CommonResult<T>统一响应体的字段访问规范"
    - "同类AI聊天/分析页面出现'服务不可用'但实际后端正常时的快速排查"
keywords:
    - "响应结构"
    - "CommonResult"
    - "字段层级"
    - "AI调用失败"
source: "auto"
---

## 任务描述
定位并修复智能分析页面（ai-analysis/index.vue 和 ai/chat/index.vue）无法调用AI服务的问题。

## 执行过程
```mermaid
graph TD
    A[问题:AI分析页面显示'服务暂时无法响应'] --> B[全局搜索AI相关文件]
    B --> C[定位前端页面与后端ChatController]
    C --> D[读取index.vue和ChatController.java确认调用链]
    D --> E[检查axios配置与CommonResult封装结构]
    E --> F[发现后端返回CommonResult<ChatRespVO>，content在data内层]
    F --> G[确认前端错误访问res.content而非res.data.content]
    G --> H[SearchReplace修复两处res.content → res.data.content]
    H --> I[Read验证修改后代码正确性]
```

## 任务总结
根因是前端未适配后端CommonResult统一响应包装，错误访问顶层res.content（实际为undefined），导致始终进入失败分支。通过精准修改两处判断逻辑为res.data && res.data.content，使AI响应内容可被正确提取、过滤并渲染，问题彻底解决。

## Notes
- 【字段访问层级需先确认请求通道（2016-10 会话复核）】"必须写成 res.data.content"不是普适结论：本仓库前端有两条通道——走 src/config/axios 的 request 封装时拦截器已剥掉 CommonResult 外层，返回值就是 data 本体，此时应直接访问 res.content / res.xxx（本轮新增的 AI 告警研判接口即按此写法并通过 vue-tsc 校验）；只有绕过封装、用原生 axios 或手写 fetch 拿到原始响应体时才需要 res.data.content。改这类页面前先读该文件实际的 import 来源与封装实现再定层级，勿照搬本条结论批量替换，否则会把已正常的调用改坏。
