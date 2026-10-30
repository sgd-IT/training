---
title: "Nginx上传大小限制配置"
usage_scenario:
    - "排查线上Excel导入失败但本地正常的问题"
    - "新项目部署时配置Nginx反向代理参数"
    - "处理大文件上传功能的环境适配"
keywords:
    - "Nginx"
    - "client_max_body_size"
    - "文件上传限制"
source: "auto"
---

线上生产环境使用Nginx代理后端服务，其默认client_max_body_size为1MB，上传Excel文件超过该限制时会被截断，导致后端收到不完整InputStream，POI解析失败报'unsupported file type: UNKNOWN'。需在Nginx配置中显式设置client_max_body_size 100m。
