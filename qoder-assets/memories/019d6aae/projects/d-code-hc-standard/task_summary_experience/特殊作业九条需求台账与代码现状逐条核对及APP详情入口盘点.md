---
title: "特殊作业九条需求台账与代码现状逐条核对及APP详情入口盘点"
usage_scenario:
    - "核对甲方需求台账标注状态与代码真实差异"
    - "回答APP作业票详情页有哪些页签与入口"
    - "区分正式库与测试库外施队人员可用性"
keywords:
    - "需求台账逐条核对"
    - "APP票证详情五页签"
    - "双人监护校验缺口"
    - "气体检测入口现状"
    - "外施队账号体系断点"
    - "jhr姓名串不可改"
source: "auto"
---

## Task description
- Core requirement：用户贴出 9 条需求台账（全部标注"未完成"），要求核对除外施队定位这块之外的其他需求真实状态；随后追问需求清单第 3 条（44 双人监护）和第 4 条（32 气体检测）在 APP 是否有查看票证详情的入口，有就要加上。
- Task background：需求横跨特殊作业、人员证书、装置管理三块；此前一轮评估记忆对多条需求给出过"未实现/需新建"结论，但后续迭代已陆续落地，台账状态与实际代码存在偏差；APP 端作业票页面结构（ActivityHotWork + 多个 Fragment）此前未系统盘清。

## Execution process
1. 逐条代码取证（Grep + Read）判定 9 条台账真实状态，结论：**5 条代码层面已完成**——需求34 中断/恢复（BaseWorkSpecialServiceImpl.interruptWork/resumeWork/interruptList + ApiSpecialController 三接口 + APP ActivityHotWork「中断作业/继续作业」按钮，workStatus=5）、需求35 开工前气体检测（startWork 已调 checkGasDetection，30 分钟窗口、按 sensor.type 0有毒/1可燃/2氧气分别判定）、需求46 打印预览+PDF（PC workSpecial/index.vue 已挂 handlePreview/handleExportPdf）、需求32 气体多次录入修改（addWorkSensor/updateWorkSensor/deleteWorkSensor 齐全，APP FragmentJobSlipGas + isEditable 控制编辑权）、外施队证书（contractorPeopleCert/index.vue 已有 queryParams.contractor / form.contractor 企业类型与所属企业筛选）。
2. 判定 **4 条仍待开发**：需求43 外施队独立办票（卡点在账号体系——APP 登录走 sys_user + userType=01，承包商人员无账号；PC workSpecial 新增按钮仍注释）、需求44 双人监护内外各一人（仅 BaseWorkSpecialValidator 的 jhr 非空校验，无人数下限与 company_type 内外校验，选人数据源仍限本作业单位）、APP 端 actualStartTime 展示（PC 列表已有"作业开始时间""实际开工时间"两列，APP 各 Fragment 布局只有录入项无展示点）、需求51 装置自动开停车（deviceCtrl 是备案登记制非控制执行，需甲方澄清"自动"语义）。
3. 盘清 APP 票证详情入口结构：从作业票列表点击进入 ActivityHotWork，共 5 个页签 —— tv1 基本信息（mBaseFragment，监护人 tvWorkGuardian 多选弹窗在此页）、tv2 取样分析（FragmentJobSlipGas）、tv3 安全措施（FragmentJobSlipMeasure）、tv4 现场会签（FragmentJobSlipSign）、tv5 监控（FragmentJobSlipMonitoring）。
4. 针对需求44 列出三块改动清单：① 选人范围放开为"公司内部 + 本票外施队单位"（DialogWorkPeopleMultiple + getWorkPeopleList）② 保存前校验人数≥2 且 company_type 同时含 1 和 2（APP 建票页 + 后端 saveWorkSpecial 兜底）③ 详情页可选加"张三（内部）、李四（外施队）"标识。
5. 识别并向用户声明风险：jhr 是平台必填的纯姓名逗号串，不能改结构否则影响园区推送；第③项标识展示只能靠姓名反查 company_type，重名有误差，建议只做校验不做标识。
6. 读取 file/人员定位及相关需求清单.docx 失败（PowerShell ZipFile 报"找不到中央目录结尾记录"，python 不可用），判定文件为加密密文，已向用户索取明文。

## Related files
- /ruoyi-system/src/main/java/com/ruoyi/base/service/impl/BaseWorkSpecialServiceImpl.java
- /ruoyi-admin/src/main/java/com/ruoyi/web/controller/base/ApiSpecialController.java
- /other1/app/src/main/java/com/sckj/other1/activity/ActivityHotWork.java
- /other1/app/src/main/java/com/sckj/other1/fragment/FragmentJobSlipGas.java
- /ruoyi-ui/src/views/base/workSpecial/index.vue
- /ruoyi-ui/src/views/base/contractorPeopleCert/index.vue
- /ruoyi-common/src/main/java/com/ruoyi/common/platement/validator/BaseWorkSpecialValidator.java

## Notes
- 需求台账中标注的"未完成"不能直接采信：本次 9 条里有 5 条代码层已完成，若照台账排期会重复开发；判断完成度必须 grep 后端方法名 + APP 按钮文案 + PC 事件处理函数三类调用点。
- 上一轮需求评估记忆的结论已过时（曾判"APP 检索中断零命中""气体 time 被强制覆盖""PC 无打印入口"），引用历史评估结论前须重新取证。
- 向监管平台推送的字段（如 jhr 姓名串）结构不可动，任何"增强展示"改造都应做成只读反查而非改存储格式。
- file/ 下的 docx 需求文档存在透明加密，Expand-Archive / .NET ZipFile / python-docx 三条路都读不出，应直接向用户索取明文或截图。
- 【2026-10-27 追加更正】本条第 2 步判定的"待开发"四项中，需求44 双人监护与 APP 端 actualStartTime 展示**其后续迭代均已落地并真机验证通过**，不得再按本条结论排期。另需注意人员数据的库别口径：正式库（anwei1-0001 / tgr-gz-vue）三家承包商 1039/1040/1041 名下确实各 0 人，而测试库（192.168.250.160）预先造了 status=3、sync_id 非空的"王外施"(id=960, contractor_id=1039)；据此在正式库跑需求43/44 前必须先录入外施队人员，拿测试库数据下正式库结论则会误判"已有人可用"。

## Task overview
部分完成（核验类任务，无代码改动）：9 条需求台账逐条对照代码得出"5 条已完成待验收、4 条待开发"结论；盘清 APP 票证详情页为 5 页签结构且需求32 已有入口无需新增；需求44 输出三块改动清单并标明 jhr 字段不可改结构的约束；需求51 与 docx 需求清单两项阻塞待用户/甲方回应。尚未确认用户所说"3、还有4"指需求清单条目还是建议顺序序号，已停手等确认，未产生任何代码改动。
