---
title: "OpenApiController中调用未定义方法getSignal3导致编译失败"
usage_scenario:
    - "当在多模块 Java 项目中诊断 'cannot find symbol' 编译错误时"
    - "当怀疑某次提交遗漏了接口或实现文件时"
keywords:
    - "compilation error"
    - "cannot find symbol"
    - "incomplete commit"
    - "git log -S"
    - "cross-module dependency"
source: "auto"
---

Bug class: 编译期符号缺失（incomplete commit）
Root cause: 在 OpenApiController.java 中调用了 deviceRealtimeService.getSignal3，但该方法在 DeviceRealtimeService.java 接口及 DeviceRealtimeServiceImpl.java 实现类中均未定义；提交 da9e25b6 仅包含调用方代码，遗漏了接口和实现文件的推送。
Fix pattern: 执行 git log --all -S "getSignal3" 全分支搜索，若只返回调用记录而无定义记录，则确认为 incomplete commit；需补全接口定义、实现、以及所有依赖模块的同步更新。
Reusable lesson: 不要在未定义方法的情况下直接调用它，因为会导致编译失败；必须确保接口定义、实现类、以及所有引用模块的代码同步提交。适用于在多模块 Maven 项目中新增跨模块服务调用；不适用于单模块内已存在且已验证的方法调用。
