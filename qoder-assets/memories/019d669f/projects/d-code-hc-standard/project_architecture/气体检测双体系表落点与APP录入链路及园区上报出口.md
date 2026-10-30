---
title: "气体检测双体系表落点与APP录入链路及园区上报出口"
usage_scenario:
    - "定位气体分析数据属于哪张表哪个页面"
    - "排查气体分析保存是否误推园区平台"
    - "判断PC端有无气体分析入口"
keywords:
    - "base_work_sensor"
    - "base_ticket_analysis"
    - "saveOrUpdateSensor三出口"
    - "PC端零入口"
    - "b_ticket_analysis_gas字典"
    - "调用链定性主用表"
source: "auto"
---

"气体检测/气体分析"数据在本项目并存两套互不相通的体系，定位数据前必须先判定属于哪一套：

1. 在用体系 —— 特殊作业票的"气体分析"，表 base_work_sensor（列：workId 挂接作业票 outerId、name/type/cash 气体名称与类型与CAS号、place 分析部位、standard/result/unit 标准结果单位、person/time 分析人与时间、deleteMark）。链路全部在 APP 端：录入为 ActivityYasAnalysis（现场取样页）→ /api/special/addWorkSensor、updateWorkSensor（ApiSpecialController → IBaseWorkSensorService）；查看为特殊作业票详情 tab 的 FragmentJobSlipGas（读 JobSlipBean.sensorList）。气体类型下拉取自字典 b_ticket_analysis_gas_11（动火）/_13（临电）/_14（受限空间）。
   园区上报出口共 3 处（2026-09-22 实测修正，原表述"由 BaseWorkSpecialServiceImpl 组装成 sensors 字段"不完整，易误判为只有整票上报才推传感器）：① BaseWorkSensorServiceImpl.insertBaseWorkSensor —— 新增落库后无条件调 PlatementTicketNewApi.saveOrUpdateSensor；② BaseWorkSensorServiceImpl.updateBaseWorkSensor —— 更新成功后查库再调同一接口（①②为需求32未提交改动所加，APP"添加/保存"按钮即触发）；③ BaseWorkSpecialServiceImpl.rePushBatchToPlatform —— 整票批量补推内的 api.saveOrUpdateSensor(fullData.getSensorList())，仅由 PushDataController 手工触发。三者最终均为 POST http://111.4.80.39:8092/openapi/work/sensor/saveOrUpdateSensor（带 KS-APPKEY/KS-TIMESTAMP/KS-SIGN 签名头），删除不上报。因此排查"某个操作会不会误推平台"必须按 saveOrUpdateSensor 全仓检索所有调用点，只看单个 ServiceImpl 会漏。当前临时注释状态与恢复登记见「气体分析传感器园区外呼集中点与临时注释状态」；补推接口鉴权与地址见「园区对接页面级配置：待推队列缺失、同步状态回写与补推鉴权约定」。风险提醒：临时注释只关闭了出口①②，出口③所在的整票补推链路仍是活代码，测试期有人调 /push/workSpecial 依旧会把传感器数据推给园区平台。

2. 遗留体系 —— 旧作业票的"取样分析"，表 base_ticket_analysis（列命名完全不同：uniqueKey、deviceName 取样仪器、position、gasName、resultValue、timeoutStatus）。PC 端仅在 base/ticket/indexShow.vue 有"取样分析"卡片展示，而该页面所属 sys_menu 菜单名已标记为"特殊作业-废"（menu_id 2390）；其新增接口 /api/ticketBase/addAnalysis（ApiTicketController）经全仓 Grep 确认无任何前端或 APP 调用方，属纯遗留写入通道。

关键反直觉事实：ruoyi-ui 整个 PC 前端对 sensor/Sensor 的检索结果为 0 匹配，sys_menu 中也不存在任何 workSensor 相关菜单——即 PC 端目前既无气体分析的录入页也无查看页与管理入口，base_work_sensor 的数据只能在 APP 上看；若要在 PC 补展示，后端 BaseWorkSensorController 已具备带权限注解的 list/export/增删改接口，只缺前端页面与菜单。

两表数据量（开发库 192.168.250.160/tgr-gz-vue 实测）：base_work_sensor 44 条、base_ticket_analysis 79 条，故按记录数判断"哪个是主用表"会得出相反结论，须以调用链而非数据量定性。
