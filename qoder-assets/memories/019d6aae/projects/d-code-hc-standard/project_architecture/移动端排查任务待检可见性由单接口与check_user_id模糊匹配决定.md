---
title: "移动端排查任务待检可见性由单接口与check_user_id模糊匹配决定"
usage_scenario:
    - "判断某账号能否在APP看到指定排查任务"
    - "修改排查任务检查人员后确认生效范围"
    - "解释逾期任务为何手机上无法补检"
keywords:
    - "getTaskRecordList"
    - "check_user_id模糊匹配"
    - "应检人快照"
    - "ApiRiskController"
    - "逾期不可补做"
    - "APP待检可见性"
source: "auto"
---

移动端（APP）风险排查任务的"待检/已检/逾期"可见性与可操作性由以下已验证的结构事实共同决定：

1. 唯一入口：ApiRiskController.java 仅暴露 getTaskRecordList / getTaskRecordInfo / updTaskRecordStatus 三个接口；列表接口强制以 SecurityUtils.getUserId() 作为过滤条件传入 selectRiskTaskRecordList，无任何管理员绕过分支，也没有第二个能列出记录的移动端接口。详情与点检接口按记录 id 操作且不校验归属，但正常用户只能从自己的列表拿到 id，故不构成额外可见范围。

2. 反直觉匹配规则：BaseRiskTaskRecordMapper.xml 的该查询用 `check_user_id like concat('%', #{userId}, '%')` 做**包含匹配**而非按逗号切分精确匹配。后果是短 ID 会误命中长串中的子串——实测 check_user_id="830,832,720" 除三人外还会命中 userId=2（系统管理账号）。判断"某人能否看到某任务"时不能只看名单字面人员，须按此规则实算；若要彻底收口需改为分隔符精确匹配。

3. 应检人是生成时的快照：base_risk_task_record.check_user_id / check_user_name 在 TaskList.addRiskTaskRecord 生成记录时从 base_risk_task 复制，后续修改任务配置的检查人员只影响次日及以后新生成的记录，当日已派发记录不会换人；且管理后台"排查记录"页（riskTaskRecord/index.vue）未提供编辑应检人的表单入口，单日换人只能走数据库。

4. 逾期后手机端不可补救（结构性断点）：ActivityRiskManagementDetails.java 中"正常/异常"按钮容器 llBtn 仅在 bean.getCheckStatus()=="0" 时设为 VISIBLE，itemActualTime 也仅在待检时可点选；一旦被 job 105 置为 check_status=4（plan_time <= NOW() 且未检），详情页变成只读，用户既看不到按钮也无任何提示语，只会觉得"任务打不开/被撤了"。后端 updTaskRecordStatus 本身不校验状态，故限制纯在 APP 侧渲染逻辑；要支持逾期补检需改该显示条件并配套提示文案，或引导由管理员在后台修改记录。

5. 完成动作的实际约束：提交时携带 latitude/longitude（默认 "0"，代码未强制定位成功）、check_time 取用户在控件选择的实际时间（默认当前），后端仅校验 check_time 不得大于当前时间；点"异常"（status=2）会跳转 ActivityAddHiddenDanger 要求继续上报隐患，即异常必须成对留下隐患记录。

6. 零提醒特性：任务生成与到点可见过程不触发任何推送通知，用户必须主动打开 APP 查看，这是现场"以为没派发"的首要来源。

配套定位口径：菜单为"风险管理(2327)→排查任务(2490)/排查记录(2496)"，改检查人员需 base:riskTask:edit 权限；排查任务列表搜索区支持按"检查人员"文本过滤，可据此批量定位同一批任务。APP 列表卡片展示风险单元名、计划检查时间与状态文字（待检绿/正常蓝/异常橙/逾期红），"第N次"班次信息只在详情页出现，卡片上不显示。
