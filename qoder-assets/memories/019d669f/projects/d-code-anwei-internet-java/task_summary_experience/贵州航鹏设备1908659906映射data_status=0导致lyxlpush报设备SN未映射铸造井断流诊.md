---
title: "贵州航鹏设备1908659906映射data_status=0导致\"/lyxl/push\"报\"设备SN未映射铸造井\"断流诊断"
usage_scenario:
    - "排查询问设备SN未映射铸造井报错"
    - "贵州航鹏漏铝报警断流排查"
    - "em_aluminum_device_info映射失效诊断"
keywords:
    - "贵州航鹏"
    - "1908659906"
    - "data_status"
    - "设备SN未映射铸造井"
    - "lyxl push 500"
source: "user"
---

## 任务描述
诊断 2026-09-20 19:07 起 JiagoVideoLeakageController[164] 持续报"设备SN未映射铸造井 deviceSn:1908659906"（每5秒一条）。

## 根因（云端 anwei_mon_aluminum 库核实）
- 设备 1908659906（漏铝设备，贵州航鹏 customer_id=373，井 116「1#铸造单元」）在 em_aluminum_device_info 有映射记录 id=67，但 data_status=0（禁用）。
- resolveCastingWell 第一级 tb_casting_well_info.node_id 无匹配（井116 node_id=5febc2d9...）；第二级明文要求 data_status=1 → 被过滤返回 null → 接口返回 code=500。
- 记录轨迹：gmt_create 09-20 18:31:23、gmt_modified 18:46:52、update_by=hpxcl；设备16:58前报警正常（mon_aluminum_leakage_alarm_record 累计5045条，最后16:58:44），之后断流。

## 修复
UPDATE em_aluminum_device_info SET data_status=1 WHERE id=67（云端主库；贵州航鹏本地部署库依赖同步下发，须确认同步或两边同改）。

## 备注
- 同表 id=65/66（云南创新 0909452694/0909452581）同为 data_status=0 隐患未消除；id=64（网关 262900000029→井116）正常启用。
- 代码日志无法区分"无映射记录"与"记录被禁用"两种失败，排查时需同时查 data_status。

## 订正（2026-09-20 二次核实）
- 用户提供的同一行副本显示 data_status=1、gmt_modified=18:31:40（疑似来自另一库，如本地 anwei_mon_aluminum_local），与云端（0 / 18:46:52）不一致 → 存在双库数据撕裂，结论须以"报错服务实际连接的库"为准。
- UploadServiceImpl（本地→云端上行）白名单不含 em_aluminum_device_info，即设备映射只有云端→本地下发，本地对该表的修改不会回流云端。
- 云端 18:46:52 的设备变更非 Web 后台操作：sys_log 该时段 hpxcl 仅有首页打卡轮询（每4秒一次，说明贵州航鹏大屏连的是云端服务）。

## 订正（2026-09-20 架构变更后）
resolveCastingWell 第二级已改为匹配 tb_casting_well_info.equip_code（不再读取 em_aluminum_device_info 映射）。本例修复方式（将 id=67 data_status 置 1，验证后恢复正常）及"云南创新 id=65/66 隐患"结论仅适用于变更前；变更后同类断流的排查与修复入口改为：目标铸造井（贵州航鹏井116）的 equip_code 是否录入了设备编码 1908659906。
