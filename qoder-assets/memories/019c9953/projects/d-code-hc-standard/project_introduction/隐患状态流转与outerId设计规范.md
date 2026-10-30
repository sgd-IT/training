---
title: "隐患状态流转与outerId设计规范"
usage_scenario:
    - "修复隐患推送重复数据问题时参考outerId设置逻辑"
    - "新增类似状态流转同步功能时复用统一outerId策略"
    - "排查平台侧隐患记录重复问题时验证outerId一致性"
keywords:
    - "隐患状态流转"
    - "outerId"
    - "saveOrUpdateBatch"
    - "平台同步"
---

隐患状态流转需通过固定outerId（使用隐患自身ID）实现平台侧同一条记录的更新，避免每次推送生成新UUID导致平台重复插入多条记录；平台接口/saveOrUpdateBatch依赖outerId识别新增或更新。
