---
title: "气体检测录入口仅在安卓App，PC后台无录入且双通道互不相通"
usage_scenario:
    - "排查气体检测数据从哪个端录入或有无入口"
    - "确认APP作业票详情页有哪些页签"
    - "评估PC端补录历史气体数据的可行性"
keywords:
    - "ActivityYasAnalysis"
    - "FragmentJobSlipGas"
    - "addWorkSensor"
    - "base_work_sensor"
    - "PC无录入口"
    - "APP票证详情入口"
    - "气体分析页签"
    - "checkGasDetection"
source: "auto"
---

特殊作业气体检测数据的录入入口只在移动端安卓 App，PC 后台无录入能力，此为易被误判的结构事实：

- App 录入链路：作业票详情底部"气体分析"页签 FragmentJobSlipGas → 点新增跳转 ActivityYasAnalysis（标题即"气体分析"）→ 调 POST /api/special/addWorkSensor（新建，无 outerId 时）或 updateWorkSensor（有 outerId 时编辑）、deleteWorkSensor（作废），接口定义在 other1 项目 HttpInterface.java，落库表 base_work_sensor。字段含气体类型（0有毒有害/1可燃/2氧气/3其他）、气体名称、CAS号、分析时间、分析部位、分析结果、结果单位、分析人。
- PC 端现状：ruoyi-ui 全仓库无任何 addWorkSensor 调用，base/workSpecial/index.vue 也无气体相关代码，PC 后台只能查看不能录入；要补录历史气体数据目前唯一入口仍是 App 那个页面。
- 两套通道互不相通：另有老纸质票体系 POST /api/ticket/addAnalysis → base_ticket_analysis（字段 uniqueKey/position/gasName/resultValue/signPath/createTime），对应 PC 页面 base/ticket/indexShow.vue 的"代表性气体"展示位。这套不参与开工校验——checkGasDetection 只读 base_work_sensor，因此往纸质票页面填的气体数据开工校验完全感知不到。
