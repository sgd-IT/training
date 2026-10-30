---
title: "特殊作业操作权限由状态掩码改为viewOnly加canOperate模型"
usage_scenario:
    - "修改作业票操作按钮显隐或权限判定"
    - "新增状态变更接口时确定校验顺序"
    - "排查作业票列表与详情状态不一致"
keywords:
    - "viewOnly标记"
    - "canOperate判定"
    - "applyUserId"
    - "guardianUserIds"
    - "checkOperatePermission前置"
    - "canOperate无登录态401兜底"
    - "mapper显式列清单漏insert"
source: "auto"
---

hc-standard 特殊作业 BaseWorkSpecialServiceImpl 的操作权限模型已由「详情接口掩码 workStatus」重构为「viewOnly 标记 + canOperate 判定」（用户拍板可操作口径为「监护人 + 开票的人」）。新不变量：

1. selectBaseWorkSpecialByOuterId 不再对非授权者强改 workStatus="1"（旧掩码污染业务字段、致列表与详情两接口同票两状态），改为 setViewOnly(!canOperate)，workStatus 恒返回真实值；APP ActivityHotWork.setRight() 据 viewOnly 隐藏操作按钮并给提示。

2. canOperate = isAdminUser() ∪ (当前 user_id == applyUserId) ∪ (guardianUserIds 含当前 user_id)。applyUserId(建票人账号ID)、guardianUserIds(监护人账号ID逗号串,建票时按 jhr 姓名反查 sys_user 落库) 为 base_work_special 新增两列。

3. interruptWork/resumeWork/startWork/endWork 四个状态变更接口的 checkOperatePermission 必须置于各方法最前、早于业务校验（会签完成/安全措施签字/气体检测等），否则业务条件凑巧满足时非授权人可绕过权限拦截，返回的是业务报错而非权限拒绝。

4. canOperate 内部经 isAdminUser() → SecurityUtils.getLoginUser()，在无认证上下文时会抛 ServiceException(401)；而 selectBaseWorkSpecialByOuterId 被 saveWorkSpecial、批量补推 rePushBatchToPlatform 等内部路径复用，故 canOperate 必须以 try-catch 兜底为"不可操作"而非让查询链路被打断。

5. insertBaseWorkSpecial 写 applyUserId/guardianUserIds 时，BaseWorkSpecialMapper.xml 的 insert 片段必须同步补齐这两列的列名与值；该 mapper 用显式列清单，只加 resultMap/select/update 而漏 insert 会导致字段静默不落库，进而使开票人自己不能操作本人开的票。

## Notes

- 原"遗留缺口"两项（updateBaseWorkSpecial 编辑票改 jhr 不重算 guardianUserIds、PC 端 index.vue 详情抽屉未处理 viewOnly）经本轮影响面核实后由用户明确决定不补（不影响使用），视为终局结论而非待办项；判断该模块能力时不应再将其列为待修复缺陷。
- 存量票回填命中数须区分库别：测试库 22 票中 19 成功；正式库 88 票实测开票人 88/88、监护人 86/88（差的 2 张是外施队监护人无 sys_user 账号）。
