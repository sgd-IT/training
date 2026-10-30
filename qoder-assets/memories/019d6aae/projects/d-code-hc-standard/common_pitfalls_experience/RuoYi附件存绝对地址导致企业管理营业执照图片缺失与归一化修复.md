---
title: "RuoYi附件存绝对地址导致企业管理营业执照图片缺失与归一化修复"
usage_scenario:
    - "排查附件图片在部分模块打不开"
    - "修复库中存的上传绝对地址"
    - "评估上传返回地址收敛方案"
keywords:
    - "file_path 绝对地址"
    - "图片显示缺失"
    - "18080 静态资源"
    - "地址归一化修复"
    - "RuoYi 上传"
source: "auto"
---

Bug class: 企业管理（base_contractor / base_contractor_cert / base_contractor_people_cert / base_project）附件图片在页面显示缺失，而作业票、签字等模块图片正常。
Root cause: RuoYi 上传接口用 serverConfig.getUrl()（取当次请求的 host:port）拼绝对地址存进 file_path，因此库里的地址跟着上传入口变：同一系统实测并存 http://220.203.23.144:18080/...（18080 不提供 /profile 静态资源）、http://182.92.130.172/...（旧服务器）、http://localhost:28080/...、http://192.168.0.20x:28080/...（内网），而 /profile/** 静态映射只挂在后端 server.port=28080 上。
Fix pattern: 数据修复用地址归一化而非改端口——保留 /profile/ 之后的相对路径，前缀统一换成后端地址：SET file_path = CONCAT('http://220.203.23.144:28080/profile/', SUBSTRING_INDEX(file_path,'/profile/',-1)) WHERE file_path LIKE '%/profile/%' AND LOCATE('220.203.23.144:28080/profile', file_path)=0（幂等）。脚本在 ruoyi-admin/src/main/resources/sql/fix_contractor_file_path_port.sql。
Reusable lesson: 归一化只解决"地址写错"；若物理文件不在当前服务器 upload 目录（跨服务器迁移或目录被清），地址改对仍是碎图，必须重传原图。适用边界：所有把绝对 URL 存库的 RuoYi 附件字段。
