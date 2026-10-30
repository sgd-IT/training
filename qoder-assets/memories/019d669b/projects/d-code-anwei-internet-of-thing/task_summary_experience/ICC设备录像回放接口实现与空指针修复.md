---
title: "ICC设备录像回放接口实现与空指针修复"
usage_scenario: []
keywords:
    - "ICC"
    - "录像回放"
    - "getPlaybackStream"
    - "空指针修复"
---

## 任务描述
为大华ICC设备服务新增录像回放接口 getPlaybackStream，并修复 CamerasController 中 quality 字段的空指针异常。

## 执行过程
```mermaid
graph TD
    A[需求:ICC录像回放+空指针修复] --> B[读取IccDeviceService.java确认接口已声明]
    B --> C[读取IccDeviceServiceImpl.java定位实现类]
    C --> D[读取CamerasController.java定位调用点]
    D --> E[在IccDeviceServiceImpl中新增getPlaybackStream方法]
    E --> F[完成参数校验、时间戳转换、token获取、VOD URL拼接]
    F --> G[在CamerasController中修复quality空指针：if-else分支替代顺序赋值]
    G --> H[验证两文件修改后内容与行号范围正确]
```

## 任务总结
成功交付ICC录像回放能力：在 IccDeviceServiceImpl 新增 getPlaybackStream 方法（支持设备编码/通道/时间范围生成 m3u8 回放地址），并修复 CamerasController 中 camera 为空时 quality 赋值导致的 NPE；URL 使用 ICC VOD 专用端口 7086，token 从 iccAuthService 动态获取。
