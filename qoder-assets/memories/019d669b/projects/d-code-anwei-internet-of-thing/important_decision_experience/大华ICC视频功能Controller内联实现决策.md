---
title: "大华ICC视频功能Controller内联实现决策"
usage_scenario: []
keywords:
    - "大华ICC"
    - "Controller内联"
    - "视频回放"
    - "服务层抽象"
---

## 决策场景
大华ICC视频相关功能（如回放、实时流）的代码组织方式

## 决策内容
不将ICC视频逻辑抽象到`IccDeviceService`等服务层，而是直接在Controller中内联实现（含URL拼接、token获取等）

## 适用范围
所有大华ICC设备的视频流功能开发与修改，包括但不限于回放、实时预览、录像下载等
