---
title: "transformOperatorInfo分隔符兼容解析与operatorId职责边界修复"
usage_scenario:
    - "重构作业人字符串解析方法的兼容分支"
    - "区分 operatorInfo 与 operatorId 取值口径"
    - "改动作业票推送转换层前确认存量格式"
keywords:
    - "transformOperatorInfo"
    - "分隔符兼容解析"
    - "operatorId与operatorInfo职责"
    - "作业票推送转换层"
source: "auto"
---

## 任务描述
修复 transformOperatorInfo 对作业人员信息字符串的分隔符兼容解析，并明确 operatorInfo 与 operatorId 的职责边界。

## 执行过程
1. 定位 transformOperatorInfo 方法，发现仅支持逗号分隔、无法兼容分号分隔入参
2. 重构为优先按 ';' 分割、回退到 ',' 分割，提取每人「姓名:证件信息」段后统一拼接输出
3. 同步校验器、实体、DTO 注释与前端 placeholder 示例

## 相关文件
- transformOperatorInfo 所在作业票推送转换层
- 相关校验器/实体/DTO 注释与前端 placeholder

## 概览
方法实现 ';'/',' 双分隔符兼容解析。注意：本条的价值在于分隔符兼容解析实现与 operatorInfo/operatorId 职责区分，而非最终存储格式——线上 base_work_special 存量实际为英文逗号分隔且每人含证书明细（格式规范见「作业人员信息字符串格式规范」），早期"多人统一分号、每人仅姓名:身份证号"的标准描述已被生产库实测推翻。

## Notes
- operatorInfo（作业人展示字符串）与 operatorId（推送平台的人员编码，取 base_contractor_people.sync_id 原值 UUID）是两个不同字段；排查平台"作业人员编码错误"或改动 operatorId 取值口径时不得套用本条结论。
- 改造前须先核对目标环境存量取值，不能凭记忆假定分隔符。
