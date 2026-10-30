---
title: "多端账号分离复用 user_type 固化语义而非新增列的取舍"
usage_scenario:
    - "判断多端登录限制用现成列还是新增列"
    - "为账号增加准入维度时评估迁移成本"
    - "复审 user_type 语义变更是否仍成立"
keywords:
    - "user_type复用决策"
    - "零DDL方案"
    - "平台位掩码"
    - "登录渠道翻写"
    - "UserTypeEnum不挪用"
source: "auto"
---

Conclusion: 移动端/PC 端账号分离不新增任何列，直接复用 system_users.user_type 并把它从"最后登录渠道标记"固化为"账号平台位掩码"（1=PC、2=移动端、3=双端），同时删除三处登录后的翻写。

Rationale (trade-off): 全列盘点确认该表除 user_type 外无任何可用于端区分的字段（login_ip/login_date 无端信息，push_enabled/area_code 属业务开关与地区），而 user_type 的列注释本就是"用户类型"这一固定属性语义；渠道追溯已由登录日志（logType）承担，删掉翻写不损失可观测性；零 DDL、零迁移脚本、发版风险最小。校验一律放在密码校验之后，避免向猜密码者泄露账号平台归属。

Rejected alternatives:
- 新增专用列（如 login_scope）保留 user_type 的渠道翻写痕迹 —— 被否：要多加一列并再走一遍 DO/VO/Service/前端全链路，成本明显高于收益。
- 改用框架 UserTypeEnum 的 ADMIN/MEMBER 双体系做彻底独立账号池 —— 被否：取值区间与业务字段撞名但语义无关，且会牵动数据权限规则与前缀判定，改动面失控。
- 按 user_type 现值直接当门禁而不删翻写 —— 被否：登录一次就把值改回渠道含义，门禁会随用户行为漂移。

Applicable / expiry conditions: 适用于"单列 tinyint + 端数量有限（≤3~4）"的平台准入场景；若未来出现第三类端（如 APK、第三方开放接口）需要独立配额或审计维度，或需保留每次登录的渠道明细用于统计，则应重新评估加列方案。存量语义迁移（老值 3 由"Wx 渠道"变为"双端"）必须与新代码同批发布，旧实例仍在运行时登录会把值翻写回去。
