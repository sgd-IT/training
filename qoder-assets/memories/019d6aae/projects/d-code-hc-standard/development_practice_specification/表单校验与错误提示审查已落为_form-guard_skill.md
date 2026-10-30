---
title: "表单校验与错误提示审查已落为 form-guard skill"
usage_scenario:
    - "体检表单必填与报错提示缺陷"
    - "评审提交失败的反馈闭环"
    - "整改校验与副作用的执行顺序"
keywords:
    - "form-guard skill"
    - "表单必填校验"
    - "错误提示文案"
    - "反馈闭环"
    - "六层检查清单"
source: "user"
---

表单校验与错误提示审查已固化为 form-guard skill，位于 C:\Users\Administrator\.qoder\skills\form-guard（用户级，跨项目可用），含 SKILL.md、checklist.md、examples.md 三份文件。覆盖三件事：输入约束落点（UI 标识、前端 rules、后端拦截三层一致）、错误文案可读性（用业务语言，禁止字段英文名、SQL、堆栈、外部平台原始报文）、提交反馈闭环（外部同步失败不得提示成功）。配套四条红线：必填三层缺一即漏洞、外部依赖报文必须转译、校验必须先于副作用、失败不得报成功。RuoYi 落点：BindException 与 MethodArgumentNotValidException 取 getDefaultMessage，不写 message 会弹 Spring 英文；RuntimeException 走 e.getMessage() 原文外泄；App 端对非中文错误消息统一弹"数据异常"。本条与《用户友好型错误提示规范》互补：前者是文案口径，后者是可执行载体。
