---
title: "Jiago设备状态上报接口（/lyxl/push）全链路实现"
usage_scenario:
    - "新增类似设备状态上报类JSON接口时的标准化开发流程"
    - "统一管理多路径（如/api/lyxl/**与/lyxl/**）安全放行的配置方式"
    - "为JSON接口补充MethodArgumentNotValidException全局异常处理"
keywords:
    - "/lyxl/push"
    - "JiagoDeviceStatusVo"
    - "JiagoDeviceDataVo"
    - "设备状态上报"
    - "GlobalExceptionHandler"
source: "auto"
---

## Task description
为 Jiago 漏铝监控系统新增设备状态上报接口 POST /lyxl/push，并与已有音频上传接口 POST /api/lyxl/file 统一安全放行与参数校验机制。

## Execution Process
```mermaid
graph TD
    A[Requirement: new /lyxl/push interface] --> B[Analyze docs and existing code, confirm gaps]
    B --> C[Create JiagoDeviceDataVo sub-device DTO]
    C --> D[Fix JiagoDeviceStatusVo: correct status field, add @Valid data list]
    D --> E[Add @PostMapping("/lyxl/push") method in Controller]
    E --> F[Add handleDeviceStatus processing logic in Service]
    F --> G[Extend SecurityConfig requestMatchers to allow /lyxl/**]
    G --> H[Add MethodArgumentNotValidException handler in GlobalExceptionHandler]
```

## Related Files
- /monkey-monitor-api/src/main/java/com/monkey/general/loulv/dto/JiagoDeviceDataVo.java
- /monkey-monitor-api/src/main/java/com/monkey/general/loulv/dto/JiagoDeviceStatusVo.java
- /monkey-monitor-api/src/main/java/com/monkey/general/loulv/JiagoController.java
- /monkey-monitor-api/src/main/java/com/monkey/general/loulv/service/JiagoDeviceService.java
- /monkey-monitor-api/src/main/java/com/monkey/general/config/JiagoSecurityConfig.java
- /monkey-monitor-api/src/main/java/com/monkey/general/config/GlobalExceptionHandler.java

## Task Summary
Successfully delivered complete device-status reporting capability: defined a two-level DTO structure (main status + sub-device list), implemented the Controller→Service call chain, unified allow-list paths and global validation exception responses, supporting strict JSON format validation (NotBlank/NotEmpty/Valid) and standardized error-code returns.

## 注意
- 本条"相关文件"中的 loulv 包路径与 JiagoController/JiagoDeviceService 类名与当前代码库实际不符：本轮（2026-10）读取代码确认 /lyxl/push 的实际实现位于 monkey-monitor-api 模块 com.monkey.general.modules.mon 包的 JiagoVideoLeakageController.receiveDeviceStatus，请求 DTO 为 monkey-monitor 模块 modules.mon.dto 包的 DeviceStatusPushDTO 与 DeviceStatusItemDTO；该类与文件可能已经重构更名或属于早期另一版本，使用前请以当前代码为准核实。
