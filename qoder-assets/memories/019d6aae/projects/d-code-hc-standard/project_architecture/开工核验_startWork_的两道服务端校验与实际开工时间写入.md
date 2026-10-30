---
title: "开工核验 startWork 的两道服务端校验与实际开工时间写入"
usage_scenario:
    - "改动作业票开工放行或签字校验条件"
    - "排查开工被拒提示安全措施未落实原因"
    - "核对实际开工时间的写入时机与展示口径"
keywords:
    - "startWork双重校验"
    - "selectWorkApproveFinish"
    - "wanGong区分会签完工"
    - "actualStartTime"
    - "workStatus平台枚举"
source: "auto"
---

特殊作业票开工放行由 BaseWorkSpecialServiceImpl.startWork() 的两道服务端硬校验构成，任一不过即抛 ServiceException 中断：
- 第一道（安全措施）：baseWorkStepMapper.selectWorkStepFinish(outerId) > 0 则报"安全措施未落实签字完成"。SQL 统计 base_work_step 中 workId 匹配、personFileId 为空、且 content NOT LIKE '其他安全措施%' 的条数——即以"其他安全措施"开头的补充项被豁免，不参与强制签字。
- 第二道（现场会签）：baseWorkApproveMapper.selectWorkApproveFinish(outerId, 0) > 0 则报"现场会签未完成"。SQL 统计 base_work_approve 中 workId 匹配、fileId IS NULL、wanGong=#{wanGong} 的条数；审批节点用 wanGong 区分两类：0=现场会签、1=完工验收。
关键约束：startWork 中另有一行查 wanGong=1（完工验收）的校验在源码里是被注释掉的，开工阶段并不检查完工验收签字；endWork 才用 selectWorkApproveFinish(outerId, 1) 校验完工验收全签。
校验通过后按序执行：绑定卢博祥设备取直播流写 base_work_video → work_status 由 0 置 2（进行中）→ 推园区平台 startTicket 并写 sync_status。
APP 端 ActivityHotWork 点"开始作业"时还有一道前置提示性校验（检查 approveList 前 n-1 个节点 fileIdUrl 非空），但真正的强制在服务端。

【作业状态口径】workStatus 为平台固定枚举：0=未开始、1=已验收、2=进行中、3=未验收、4=已删除；平台侧没有"中断/暂停"态，本地新增的中断语义只能留在本系统，推平台前须归一为"进行中"(2)（归一模式见"作业票新增本地状态值须推送前归一为平台枚举"）。
时间戳现状（已更新）：startWork 除切换 workStatus 外，现已把 actualStartTime 写入 base_work_special.actualStartTime（AFTER workStartTime 列），语义即"现场会签页点击开始作业按钮的那一刻"，用于与报备填写的计划 workStartTime 区分；PC 列表与 APP 现场会签页顶部均展示该值，未开工时 APP 整行隐藏。endWork 仍未落实际完工时间戳。
防绕过约束：startWork 只允许 workStatus=0 时执行（否则中断后再点开始会覆盖 actualStartTime 并向平台推错误状态）；endWork 拒绝在 workStatus=5（本地中断态）时结束。

## Notes
- 正文"endWork 仍未落实际完工时间戳"已过时：本轮（动火票 c0de0002 完工验收实测）确认 endWork 现已把 workStatus 直接置 1（已验收，不经 3 未验收态），并同时写入 workEndTime 与 checkTime（实测两者同值为 endWork 调用时刻），还写 systemFileIds（平台归档 fileId）。以本条最新实测为准。
