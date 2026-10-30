---
title: "推送数据状态码决策-access_status=0→2"
usage_scenario:
    - "贵州平台数据推送的增量过滤逻辑"
    - "新增推送数据类型时的状态管理参考"
keywords:
    - "access_status"
    - "增量推送"
    - "贵州平台"
    - "状态码"
    - "0→2"
    - "零DDL"
---

## 决策场景
贵州平台数据推送中筛选待推送记录的增量过滤策略

## 决策内容
统一复用已有 `access_status` 字段做增量标记，不加新列（零DDL变更）：
- 查询条件：`.eq(accessStatus, 0)` — 只查未推送记录
- 推送成功后：`UpdateWrapper.set("access_status", 2)` — 标记已推送
- 推送失败时：不更新状态，下次自动重试

## 状态语义
- 0 = 未推送（默认值，待推送）
- 1 = 未推送（手动测试标记用）
- 2 = 已推送到贵州平台

## 涉及数据类型
人员出入(RYCRJL)、车辆出入(CLCRJL)、监测指标报警(ZBBJ)、视频报警(SXTFX)

## 注意事项
报警持续更新（如消警设置clearDate）需业务层重置 access_status=0 才会再次被查出推送
