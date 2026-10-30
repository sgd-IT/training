---
title: "多平台拉流接口按pullType分支决策"
usage_scenario: []
keywords:
    - "pullType"
    - "多平台拉流"
    - "接口分支"
    - "视频平台接入"
---

## 决策场景
多平台视频拉流接口统一设计时，确定是否按pullType参数进行逻辑分支

## 决策内容
当系统需同时支持海康与大华平台拉流时，所有涉及拉流的对外接口（如geturlStr、geturl、getHuifang）均应根据pullType参数值（0=海康，1=大华）进行条件分支，分别调用对应平台SDK方法

## 适用范围
含多厂商设备接入能力的视频管理平台后端接口设计
