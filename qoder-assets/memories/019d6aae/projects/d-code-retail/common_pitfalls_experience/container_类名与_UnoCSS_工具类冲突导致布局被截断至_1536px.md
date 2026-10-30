---
title: ".container 类名与 UnoCSS 工具类冲突导致布局被截断至 1536px"
usage_scenario:
    - "新页面布局未铺满或宽度异常"
    - "UnoCSS 项目自定义类名排查"
    - "用 computedStyle 定位布局问题时"
keywords:
    - "UnoCSS class name conflict"
    - "container max-width 1536"
    - "layout width truncated"
    - "tailwind utility clash"
source: "auto"
---

## Usage Scenario / 使用场景
需要采集某个线上页面的 DOM 结构、计算样式或切图素材，但浏览器会话未登录（路由守卫把请求重定向到 #/login）时。

## Usage Method / 使用方法
不要尝试登录或猜凭据：改用"分析前端静态资源"的路径——先读取 index.html 拿到 script/link 分片清单，再用页面内 fetch 逐个抓取 JS/CSS 分片文本，按已知 DOM 特征串（class 名、带 hash 的图片文件名、data-v-xxx 作用域 id）反查定位源组件模块、其 scoped CSS 注入模块与图片模块，最后用正则全量提取"模块 id → static 资源路径"映射并逐个探活 URL。

## Notes
- 打包产物里若样式由 style-loader 运行时注入，在 app.css 等 CSS 文件中检索目标类名会返回 0 命中，误判成"样式不存在"；必须回到 JS 分片里找样式模块。
- 样式存在包装模块（仅百来字符，内部再 require 真实载荷模块），直接导出包装模块会得到空壳，需顺着继续取载荷。
- 部分小图会被内联成 base64 data URI，在 static/img 目录下根本找不到对应文件，别在其上反复试错；这类图只能从 JS 文本解码还原。
- 动态拼接的资源前缀可能写在配置脚本（如 SITE_CONFIG.cdnUrl）里，也可能在不同分片里用不同变量名（一处 e.p、另一处 n.p），只按一种写法搜索会漏掉资源。
- 抓到的 hash 文件名可反推源码原名（img.f2a6ac7.png → assets/dashboard/img.png），落地素材时按原名保存更便于对接工程。
