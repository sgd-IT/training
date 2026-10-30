---
title: "前端提交异步请求的空catch与组装语句误放顶层两类缺陷"
usage_scenario:
    - "排查提交失败却提示成功的告警处置类缺陷"
    - "修改含async提交的Vue页面避免崩页"
    - "评审firewatch页面的try/catch结构是否恰当"
keywords:
    - "空catch吞异常"
    - "失败仍提示成功"
    - "script setup顶层TDZ"
    - "submitFalseAlarm"
    - "成功分支位置"
source: "auto"
---

Bug class: async 提交方法内"成功动作在失败后仍执行" + 半成品编辑导致的语法/时序损坏（Vue <script setup> 页面）
Root cause: 两点机制。其一，提交方法用空 catch（catch{} 或仅 console.warn 而成功分支留在 try 外）吞掉请求异常，await 失败后代码继续往下跑，照常改本地状态、弹成功提示、关窗、写假时间线，形成"失败仍提示成功"。其二，手动/半自动编辑时把依赖运行时表单值的组装语句放到组件顶层：<script setup> 顶层在组件初始化即执行，此时同文件后面声明的 const 箭头函数尚未定义，触发 TDZ ReferenceError 直接崩页；且组装值只在初始化算一次，后续用户输入不生效；删掉旧 finally/收尾大括号又会让函数不闭合，后续代码被吞进函数体。
Fix pattern: 组装（映射编码→中文、trim 拼接成品串）必须放在方法体内、参数校验之后；成功动作全部移入 await 之后的 try 成功路径；外层 catch 只 console.warn，不在页面重复弹错（axios 封装层已统一弹 ElMessage/ElNotification，二次弹会双提示）；finally 只收尾 loading；保持函数正常闭合。
Reusable lesson: Don't 把读取表单响应式字段的组装语句写在组件顶层或用空 catch 包住主请求，因为会分别造成初始化即崩/取值失效与失败仍提示成功；instead 组装下沉到方法内、成功分支收进 await 之后、catch 只记日志。适用于本仓库 firewatch 下含 submitXxx 异步提交的 Vue 单文件页面（如处置页与监控中心的标记误报）；不适用于次要动作容错的内层嵌套 try/catch（如 confirmAlertRecord 这类失败不影响主流程的旁路调用），那类空捕获是合理的。
