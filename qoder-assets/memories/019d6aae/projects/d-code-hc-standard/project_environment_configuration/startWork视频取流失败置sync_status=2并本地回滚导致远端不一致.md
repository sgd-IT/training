---
title: "startWork视频取流失败置sync_status=2并本地回滚导致远端不一致"
usage_scenario:
    - "排查开工后视频不可播与远端设备不一致"
    - "分析作业票同步状态异常根因"
    - "在startWork或endWork邻近新增作业状态接口时评估推送与回滚边界"
keywords:
    - "startWork事务回滚"
    - "推送失败置sync_status=2"
    - "远端已成功本地回滚"
    - "外部接口调用失败"
    - "@Transactional边界"
source: "auto"
---

作业票表 base_work_special.sync_status 的线上实测取值与项目标准定义不一致（标准口径以《sync_status字段标准定义》为准：NULL/'1'/'2'，不存在 '0'，补推筛选须写 (sync_status IS NULL OR sync_status IN ('0','2'))）。

本条记录的独立增量为 startWork 的事务一致性缺陷：
- startWork 在外部视频接口（如卢博祥 getDeviceLive）调用失败时把该票证/视频的同步状态置 2，并触发本地事务回滚；
- 而远端设备注册（saveTicketAndDevice）可能已经成功，形成"本地已标记失败并回滚、远端已有数据"的不一致状态；
- 排查开工后视频不可播或平台侧有设备但本地无记录的问题时，应优先核对该回滚路径，而不是假定远端也未执行。
