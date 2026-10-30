---
title: "Jiago系统接口功能与协议规范"
usage_scenario:
    - "新增Jiago相关功能开发时参考接口定义"
    - "编写接口测试用例时构造合法请求体和断言响应"
    - "生成API文档或对接第三方系统时提取协议细节"
keywords:
    - "Jiago接口"
    - "设备状态上传"
    - "音频文件上传"
    - "REST API"
    - "报警记录分页"
    - "lyxl协议"
    - "接口日志排查"
source: "auto"
---

Jiago系统提供两个核心接口：
1. 设备状态上传接口：POST /lyxl/push，application/json格式，必填deviceSn/time/status/data（含id/type/status）；
2. 音频文件上传接口：POST /api/lyxl/file，multipart/form-data格式，必填deviceSn/time/id/file。
响应统一使用code/msg结构，code=0表示成功，非0表示失败。
