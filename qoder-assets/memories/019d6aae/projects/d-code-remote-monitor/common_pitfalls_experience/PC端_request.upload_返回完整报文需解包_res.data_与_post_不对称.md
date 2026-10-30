---
title: "PC端 request.upload 返回完整报文需解包 res.data 与 post 不对称"
usage_scenario:
    - "排查导入结果条数显示为0或undefined"
    - "新增upload接口读取业务字段"
    - "统一Excel导入结果展示方案"
keywords:
    - "request.upload"
    - "axios封装层级"
    - "res.data解包"
    - "successCount undefined"
    - "showImportResult"
source: "auto"
---

Bug 类：PC 端前端 axios 封装 request.upload 与 request.post 返回层级不一致导致的静默错误——upload 返回完整报文 {code,msg,data}，post 返回解包后的业务数据。页面直接读 res.successCount 得到 undefined，界面显示"导入完成！成功 0 条"，而后端实际已成功入库（排班导入实测 3 条全成功仍显示 0）。
根因：src/config/axios/index.ts 里 post/delete/put 都有 `return res.data`，唯独 upload 是 `return res`（响应拦截器返回的是完整 CommonResult 报文）。
修复模式：upload 调用方读取业务字段前统一解包 `const data = res?.data ?? res`（兼容两层）；firewatch 排班/巡检/隐患三处导入已收敛到 @/utils/importResult.ts 的 showImportResult(res) 统一展示（全成功轻提示、有失败弹窗逐行列"第 N 行：原因"，失败明细字段是 failDetails/reason，不是 errors/message）。
可复用教训：新增 request.upload 调用点必须先解包再读字段，不能照搬 post 的直接取顶层字段经验。适用于本仓库前端所有走 request.upload 的调用点；不适用于 el-upload 以 :action 直传（绕过 axios，on-success 拿到的本就是原始报文，取 response.data 即可）的场景。
