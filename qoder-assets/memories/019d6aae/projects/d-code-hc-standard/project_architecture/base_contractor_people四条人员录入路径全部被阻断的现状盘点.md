---
title: "base_contractor_people四条人员录入路径全部被阻断的现状盘点"
usage_scenario:
    - "改造或恢复外施队人员PC端录入功能时"
    - "评估人员录入方案时盘点各路径可行性"
    - "排查承包商人员录入入口缺失成因"
keywords:
    - "base_contractor_people"
    - "录入路径阻断"
    - "新增按钮注释"
    - "activation_code注册"
    - "status=3审核"
    - "syncContractorPeople覆盖contractor_id"
source: "auto"
---

base_contractor_people（相关方人员）共 4 条数据录入路径，经代码盘点全部存在阻断（2026-10 实测）：① PC 手动新增：后端 BaseContractorPeopleController.add() 接口完整，但前端 contractorPeople/index.vue 新增按钮被注释，且修改表单中所属企业/部门/岗位/姓名/手机号字段全部 disabled——该表单设计初衷是"同步数据的补充编辑"而非从零录入；② 人员定位系统同步：syncContractorPeople 从定位接口拉取时 setContractorId(1001L) 硬编码，外施队人员不在定位系统中，此路不通；③ APP 企业码自助注册：后端 registerUser() 通过 base_contractor.activation_code 查企业并挂载 contractorId，接口存在，但 other1 移动端仓库中搜不到任何 registerUser/RegisterPeople 调用，APP 端注册入口不存在，且 activation_code 列在 PC 企业页已被注释导致管理员拿不到码；④ 平台同步：PlatementContractorApi，取决于上游不可控。关键约束：选人接口 selectContractorPeopleList 的 SQL 条件要求 status=3（已通过），管理员手动录入需直接置 status=3 跳过审核流才能被选出。页面已有 contractorList（created 时 getContractorList() 填充）可直接复用做企业下拉。

## Notes
- 路径②的前提已失效（2026-10-23 用户出示定位系统组织树实证）：湖北源洹树下存在独立"承包商"分组并已绑定 7 张卡（湖北盛廷×2、陆晟机械×4 等），即外施队人员已进入定位系统，此路可通。真正的缺陷变为 updateBaseContractorPeopleByPhone 会把命中人员的 contractor_id 覆盖成 1001、status 改成 3、id_card_no 写成 worktype，且 where 仅按 phone 无 del_flag 条件；未命中则 insert 到 1001 名下，导致外施队归属从第一步就被抹掉。
- 路径①也已部分松动（同轮核对 contractorPeople/index.vue 表单区）：新增/修改对话框的"所属企业"已是可选 el-select（contractorList + company_name），不再 disabled；仍缺的只是列表页新增按钮入口，以及表单无 sync_id / 定位卡号录入项（positionCardNo 处于注释状态）。
- 本条其余结论（activation_code 注册入口缺失、status=3 强制约束、contractorList 可复用）保持不变。
- 路径①现状再次更新（2026-10-23 执行改造时实读同一文件）：列表页"新增"按钮已存在于工具栏（v-hasPermi='base:contractorPeople:add'），未被注释，因此"仍缺新增按钮入口"这一句已过时；现在真正缺的是表单没有 sync_id / 定位卡号录入项。同轮 submitForm 已改为：新增分支 this.form.status = 3（否则录进去因 status≠3 选不出人）、修改分支删除了原先强制 this.form.status = 1 的重置（编辑一次即从选人列表消失）。
- 路径②的破坏点已在代码层修掉（2026-10-23）：BaseContractorPeopleMapper.xml 的 updateBaseContractorPeopleByPhone 现只回填 position_card_no（dept_name 仅在本地为空时补）并给 where 补 del_flag='0'；BaseContractorPeopleServiceImpl.syncContractorPeople() 已注释 clearPositionCard() 全表清卡、去掉 contractorId/status/id_card_no(worktype) 覆盖，手机号未建档时只打日志跳过不再 insert 到 1001。
- 环境口径提醒：以上人员/ID 形态结论须区分库别。线上正式库（220.203.23.144，@@hostname=anwei1-0001）实测 1001 名下 136 人 sync_id 与 outid 完全相等、三家承包商企业（1039/1040/1041）人员数为 0，practitioners.sync 与 locationTask.getLocation 均为每 10 秒启用；测试库（192.168.250.160）则是雪花 sync_id 且 sync_id≠outid，不能把测试库取值形态当线上事实下结论。
