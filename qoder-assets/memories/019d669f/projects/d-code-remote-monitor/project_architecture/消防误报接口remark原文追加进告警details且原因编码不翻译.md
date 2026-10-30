---
title: "消防误报接口remark原文追加进告警details且原因编码不翻译"
usage_scenario:
    - "改造告警工单误报或处置提交逻辑"
    - "排查details中出现原因编码而非中文"
    - "新增告警处置类接口时确定参数与留档落点"
keywords:
    - "falseAlarm接口"
    - "appendRemarkToDetails"
    - "details追加留档"
    - "原因编码不翻译"
    - "AlertOrderStatusResolver"
source: "auto"
---

经任务执行走查验证的告警工单处置链路结构事实（anwei-fire-remote-monitor）：

1. 后端接口 FireAlarmOrderController#falseAlarm（PUT /eiot/firewatch/alarm-order/false-alarm）用 @RequestParam 接收 id/handler/remark，前端必须用 params 发送而非 data；成功返回业务数据 true。相邻的确认火警等接口会自动补登录人昵称，而 falseAlarm 不会——handler 不传就没有可追溯处理人。

2. 处置备注并非独立表或独立列：FireAlarmOrderServiceImpl.appendRemarkToDetails 把"标记误报"+remark 当作纯文本直接追加拼接到 eiot_alert_record.details 末尾（格式：原details + [时间] 标记误报：remark）并置已读。它不做原因编码到中文的翻译，也不校验内容结构——前端若把原因编码（如 smoke_dust）直接作为 remark 提交，库里留存的就是编码而非可读中文；改造留档格式应由前端在提交前组装成品字符串（中文原因+详细说明），或后端补齐登录身份，而非依赖后端翻译。因此 details 既是原始告警内容又是处置留档载体，同时也是列表/详情 VO 的 alarmDescription 字段来源（vo.setAlarmDescription(record.getDetails())）。

3. handler 为可选参数：StrUtil.isNotBlank 判空后才拼接处理人前缀，前端传 undefined 时该参数被 qs 整个跳过、请求中不出现 handler，库中留档即无处理人，无法追溯操作者。

4. 工单状态是推导出来的：AlertOrderStatusResolver 依据 details 里追加的处置文本片段（contains 匹配）解析 orderStatus，所以前端本地改写 status 与后端返回值不保证一致，"已处理"流程状态与"确认为误报"结论不能靠本地赋值表达。

5. 前端存在两个误报提交入口，均需同步修改：views/eiot/firewatch/alarm-order/index.vue（falseAlarmForm + submitFalseAlarm）与 views/eiot/firewatch/monitoring-center/index.vue（falseForm）；api/eiot/firewatch/firewatch.api.ts 已有 markFalseAlarm(id, handler?, remark?) 封装可直接复用，但页面内同名的 markFalseAlarm 方法负责打开弹窗，导入时需 as 别名避免冲突。原因编码转中文已有 getFalseAlarmReasonText。

6. 项目 axios 封装（config/axios/service.ts）统一处理业务错误码（成功码 code=0），页面层不宜重复弹提示。
