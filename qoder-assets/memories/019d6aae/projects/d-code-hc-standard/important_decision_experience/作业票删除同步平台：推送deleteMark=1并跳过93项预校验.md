---
title: "作业票删除同步平台：推送deleteMark=1并跳过93项预校验"
usage_scenario:
    - "修改作业票删除或平台删除同步逻辑前"
    - "排查删除后平台仍显示活跃票问题"
    - "扩展补推逻辑覆盖已删除票场景"
keywords:
    - "删除推送"
    - "deleteMark=1"
    - "跳过预校验"
    - "updateTime"
    - "startTicket重载"
source: "user"
---

结论：作业票删除时须同步平台删除标记。本地先逻辑删除（deleteMark=1 且 updateTime=now()，见 BaseWorkSpecialMapper.xml 两个 delete 语句），再将 deleteMark=1 的完整实体经 PlatementTicketNewApi.startTicket(entity,false) 推送平台，sync_status 记录结果；deleteBaseWorkSpecialByOuterIds 已加 @Transactional，推送放在删除操作证/视频子表之前（子表数据要随删除上报一起推送）。
理由权衡：平台弃用物理删除接口，要求"如需删除，标志记为1"且 updateTime 必须变为删除时间；跳过93项本地预校验，因为实测测试库20条活跃票中13条 workStatus=1 缺 checkTime、2条缺 updateTime，严格校验会让这些票永远删不掉，改由平台返回结果判定。
被拒替代：① 走 validate=true 会卡死历史数据；② catch 掉推送异常继续本地删除会造成平台静默残留且无自动重试。
适用与失效条件：适用于 base_work_special 删除链路（Web 端 base:workSpecial:remove）；网络异常时整体回滚（与 save/start/end 推送语义一致）；删除推送失败的票可在 POST /push/deletedWorkSpecial 重推（按删除时间 updateTime 过滤，onlyUnsynced 默认 true）。
