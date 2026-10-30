---
title: "小程序自动注册逻辑中安全添加remark字段"
usage_scenario:
    - "小程序/微信自动注册流程中新增或修正remark字段赋值逻辑"
    - "排查因条件判断过宽导致的重复INSERT主键冲突问题"
    - "同类自动注册功能（如H5、APP）需复用相同分支隔离原则时"
keywords:
    - "自动注册"
    - "remark字段"
    - "主键冲突"
    - "条件判断修复"
source: "auto"
---

## 任务描述
在小程序自动注册逻辑中新增 remark 字段值为"小程序自动注册"，并修复因错误判断 user.getRemark() == null 导致的重复 INSERT 主键冲突问题。

## 执行过程
```mermaid
graph TD
    A[需求:自动注册加remark字段] --> B[读取AdminAuthServiceImpl.java定位注册逻辑]
    B --> C[发现条件判断存在逻辑缺陷：user == null || user.getRemark() == null]
    C --> D[提出正确分支：仅user == null时创建新用户，老用户仅更新userType]
    D --> E[执行SearchReplace移除冗余判断条件]
```

## 任务总结
修正了自动注册逻辑：将创建分支严格限定为 user == null 场景，避免老用户因 remark 为空被误判为新用户而触发 createWxMaUser 导致主键冲突；同时确保新用户 builder 中正确设置 remark = "小程序自动注册"。
