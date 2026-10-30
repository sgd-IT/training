---
title: "前端API前缀化机制与定位页硬编码外部地址例外"
usage_scenario:
    - "排查前端请求实际打到哪台后端"
    - "改造定位监控页地址的环境可配置性"
    - "新增前端外部服务调用时避免硬编码"
keywords:
    - "VUE_APP_BASE_API"
    - "dev-api代理配置"
    - "RTLS定位页iframe"
    - "220.203.23.199"
    - "nginx转发依赖"
source: "auto"
---

PC 前端（ruoyi-ui）的环境切换机制存在一个例外：全部业务请求都走 `process.env.VUE_APP_BASE_API` 相对前缀（axios 封装 src/utils/request.js、文件上传、图片/头像回显、附件下载、Excel 导入导出均如此），构建模式决定前缀——dev 为 /dev-api 且 vue.config.js devServer proxy 转发到 http://localhost:28080；build:prod 为 /prod-api；build:stage 为 /stage-api。仓库内不含 nginx 配置，因此打包产物实际连哪台后端完全由部署机 nginx 的反向代理规则决定，无法从代码推断。

反直觉例外：src/views/monitor/dingWei/index.vue 的 RTLS 人员定位监控页用 i-frame 直接硬编码 src="http://220.203.23.199:60080/rtls/"，该地址不随任何构建模式或环境变量变化，开发态与生产态打开该页面都是直连这台独立定位服务器（与测试服务器 220.203.23.144 同网段但不同主机）。这是前端业务代码中唯一的外部服务硬编码，其余 http 引用均为 RuoYi 文档、element 官网等无关链接；前端亦无 WebSocket 地址硬编码。若需按环境切换定位页地址，必须改造该行使其改读 VUE_APP_ 变量。
