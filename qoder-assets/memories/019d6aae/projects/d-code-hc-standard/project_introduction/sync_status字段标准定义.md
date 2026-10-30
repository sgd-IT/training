---
title: "sync_status字段标准定义"
usage_scenario:
    - "新增同步状态字段时确定类型与默认值"
    - "编写补推或待推送筛选SQL"
    - "判断记录是否已同步成功以决定能否安全推平台删除"
keywords:
    - "sync_status"
    - "CHAR(1) DEFAULT 0"
    - "NULL兼容筛选"
    - "补推条件"
    - "推送成功跳过"
    - "推送前置条件"
    - "删除同步判定"
source: "auto"
---

作业票表（base_work_special）、视频表（base_work_video）、承包商表（base_contractor）、隐患表（base_hidden_trouble）、风险任务表（platement_implement_task）的 sync_status 字段标准定义：
- 类型：CHAR(1)，可为空，注释：'同步状态：0未同步、1已同步、2同步失败'
- 含义：'0'=未同步（刚保存尚未推送）、'1'=已同步（推送成功）、'2'=同步失败（推送失败）
- 建列口径：显式 DEFAULT '0'（2026-08 经 information_schema.columns 核实 base_work_special.sync_status 的 COLUMN_DEFAULT 即为 '0'）。
- 【存量取值按环境不同，写筛选条件前必须先 SELECT 分布】不能把某个库的实测值当成全局口径：
  · 本地/测试库 tgr-gz-vue（192.168.250.160）实测全部行为 '0' 或 '2'，无 NULL、无 '1'（推送链路在此环境基本未跑通）；
  · 早前另一环境实测为 NULL/'1'/'2'（历史行留空、不存在 '0'）。
- 因此补推/待推送筛选一律写成最宽兼容式：(sync_status IS NULL OR sync_status NOT IN ('1'))，即"除明确成功外都要重推"，禁止只写 IN ('0','2') 或只写 = '0'。
- 项目另有操作证表 base_work_certificate 存储作业票操作证数据。
