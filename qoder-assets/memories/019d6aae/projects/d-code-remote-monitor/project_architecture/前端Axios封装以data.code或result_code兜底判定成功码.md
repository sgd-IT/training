---
title: "前端Axios封装以data.code或result_code兜底判定成功码"
usage_scenario:
    - "排查前端请求成功但页面报错或反之"
    - "判断接口业务码约定与解包后返回值类型"
    - "设计页面catch是否需重复弹错误提示"
keywords:
    - "axios拦截器"
    - "result_code"
    - "业务成功码"
    - "响应解包"
    - "service.ts"
source: "auto"
---

前端 src/config/axios/service.ts 响应拦截器用 `const code = data.code || result_code` 取业务码，result_code 来自 src/config/axios/config.ts，值为 200。因 JS 中 0 为 falsy，后端(yudao CommonResult)成功返回 code:0 时会落入兜底的 200 而通过 `code !== 200` 的错误分支判断，等效于"成功码是 0 也能通过"。随后 index.ts 再解包一层 res.data，页面 await 拿到的是 CommonResult 的 data 字段内容（如误报接口为布尔 true）。失败路径：HTTP 层错误由拦截器自动 ElMessage.error 并 reject；业务非 200 码自动 ElNotification.error 并 reject——即封装层已弹错，页面 catch 内重复弹会产生双提示。
