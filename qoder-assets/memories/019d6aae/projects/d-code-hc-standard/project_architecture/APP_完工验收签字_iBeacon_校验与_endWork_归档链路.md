---
title: "APP 完工验收签字 iBeacon 校验与 endWork 归档链路"
usage_scenario:
    - "改造或排查APP作业票完工签字流程时"
    - "分析endWork后状态字段与归档产物去向时"
    - "在无蓝牙环境设计作业票测试绕过方案时"
keywords:
    - "完工验收签字链路"
    - "iBeacon现场校验"
    - "SignatureAdapter按钮可见性"
    - "endWork状态流转"
    - "平台侧PDF归档"
source: "auto"
---

hc-standard 移动端作业票"完工验收→endWork"链路经任务执行实测确认的结构性事实：① SignatureAdapter 在 workStatus=2（进行中）时只给最后一个会签节点（wanGong=1 的完工验收）显示签字按钮，前 5 个意见节点按钮 GONE——进行中的票在 APP UI 上无法逐级补签历史节点。② 完工验收签字前置校验顺序：先选火种残留情况（动火票必填），再校验处理意见非空，缺一即 toast 拦截。③ 签字点"是"后强制进入 ActivityBluetooth 做 iBeacon 现场校验（匹配厂商 ID bytes[5]==0x4c/bytes[6]==0x00，且 major/minor 拼成的数字须包含于区域 areaBeacon），通过后才打开签名板；该校验无绕过入口，属防远程代签的设计约束，无蓝牙硬件的环境（MuMu 模拟器 dumpsys bluetooth_manager 恒为 enabled=false）真机路径必然卡死。④ 最后节点签字成功回调自动触发 /api/special/endWork；endWork 仅校验 selectWorkApproveFinish(outerId,1)（即完工验收已签），不校验其余 5 节点，随后直接置 workStatus=1（已验收，中间不经 3 未验收态）并写 workEndTime/checkTime/systemFileIds。⑤ endWork 归档走平台侧：printWorkSpecial 本地生成 docx 上传平台，平台转 PDF 返回 pdfPath，本地 print 目录只留 docx 属正常现象，不是转换失败。⑥ luboxiangCall 视频服务调用失败仅 log.warn 返回 null，不阻塞 endWork。⑦ updApprove 接口写 person 取 SecurityUtils.getNickName()，fileIdUrl 原样存字符串——若传相对路径（/profile/xxx），导出 PDF 时 Pictures.ofUrl 抛 MalformedURLException: no protocol 而跳过该图；真实 APP 上传返回完整 http URL 故生产链路无恙。
