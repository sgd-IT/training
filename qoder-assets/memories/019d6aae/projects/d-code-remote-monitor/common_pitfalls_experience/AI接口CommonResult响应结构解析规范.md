---
title: "AI接口CommonResult响应结构解析规范"
usage_scenario:
    - "前端调用AI接口返回undefined content导致功能失效"
    - "多个Vue页面出现相同AI响应解析失败问题"
    - "排查API返回数据为空但后端日志显示成功的情况"
keywords:
    - "CommonResult"
    - "res.data.content"
    - "API响应结构"
    - "axios拦截器"
source: "auto"
---

后端所有API统一使用CommonResult<T>包装响应，实际业务数据位于res.data中。前端调用AI接口时，错误地直接访问res.content，应改为res.data.content。该结构在ChatController.java返回CommonResult<ChatRespVO>及service.ts拦截器逻辑（第189行return data）中明确体现。（来源：Read + Grep）
