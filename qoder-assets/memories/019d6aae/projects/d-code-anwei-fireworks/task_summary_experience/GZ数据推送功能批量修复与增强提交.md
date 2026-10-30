---
title: "GZ数据推送功能批量修复与增强提交"
usage_scenario:
    - "GZ数据推送模块问题修复或功能增强时的标准化提交流程"
    - "排查access_status逻辑不一致、经纬度存储异常、企业重复插入等问题"
    - "同类多文件批量diff审查与分步提交操作参考"
keywords:
    - "GZ推送"
    - "access_status"
    - "经纬度标准化"
    - "企业去重"
source: "auto"
---

## 任务描述
批量提交GZ数据推送功能相关代码修复与增强，包括非法入侵报警判断注释、access_status查询条件统一修正、经纬度毫度标准化、social_credit_scope兜底优化、企业名称去重保护等。

## 执行过程
```mermaid
graph TD
    A[需求:提交AlramInfoServiceImpl] --> B[查看diff确认仅新增1行注释]
    B --> C[提交feat: 添加非法入侵报警判断注释]
    C --> D[需求:提交剩余GZ业务文件]
    D --> E[批量查看PushingGZDataService/SetGZUploadInfo/RequestGZMethod diff]
    E --> F[用户否决RequestGZMethod中zip保留改动并要求还原]
    F --> G[SearchReplace还原deleteTempFile逻辑]
    G --> H[确认RequestGZMethod仅剩企业去重保护改动]
    H --> I[逐个add+commit三文件：access_status修正、经纬度标准化、企业去重]
```

## 任务总结
成功完成4次精准提交：1）AlramInfoServiceImpl添加非法入侵判断注释；2）PushingGZDataService统一13处access_status=1→0并清理临时注释；3）SetGZUploadInfo修复经纬度毫度存储（新增normalizeCoord方法）、优化social_credit_scope兜底、固定equip_type="IPC"；4）RequestGZMethod增加企业按名称去重保护（provincecode=52）。

## Notes
- 该批量修复与增强已在贵州数据推送流程优化与自动化方案中实现并验证成功；具体包括：贵州推送access_status环境配置化、贵州推送access_status查询参数化等核心改进。
