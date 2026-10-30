---
title: "XSS过滤器包装HttpServletRequest导致multipart解析失败"
usage_scenario:
    - "文件上传后内容异常（文件头错误、大小不符、markSupported=false）"
    - "启用XSS过滤器后Excel等二进制文件解析失败"
    - "排查Filter对multipart/form-data请求的隐式影响"
    - "中间件引发的全局稳定性问题（如线程阻塞、连接超时）"
keywords:
    - "XSS过滤器"
    - "multipart解析"
    - "文件上传"
    - "HttpServletRequestWrapper"
source: "auto"
---

XSS过滤器使用XssHttpServletRequestWrapper包装HttpServletRequest后，即使未修改输入流内容，也会导致Tomcat multipart解析失败，使file.getInputStream()返回损坏数据（如文件头异常、markSupported=false）。需将文件上传接口路径加入xss.excludes排除列表。（来源：Filter机制与日志分析）
