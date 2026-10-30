---
title: "探测线上图片可达性禁用HEAD须用GET加字节数比对"
usage_scenario:
    - "验证上传文件URL是否真实可访问"
    - "排查前端图片显示不出来"
    - "核查远程文件内容是否损坏"
keywords:
    - "HEAD请求误导"
    - "curl GET探测"
    - "image/jpeg校验"
    - "profile静态资源"
    - "size_download比对"
source: "auto"
---

Bug class: 用 HEAD 请求探测"图片文件是否存在/可访问"得到错误结论，进而误判为文件损坏或权限问题。
Root cause: hc-standard 线上（220.203.23.144:18080）对 /profile/upload/**.jpg 的 HEAD 请求不走静态资源处理，而是落到 Java 应用并返回 Content-Type: application/json;charset=utf-8 + X-Content-Type-Options: nosniff（HTTP 仍为 200），于是"图片 URL 返回 JSON"被误读成图片丢失或访问权限异常；实际同一 URL 的 GET 返回 200 image/jpeg 且字节数与本地样本一致。RuoYi 侧 SecurityConfig 对 "/profile/**" permitAll、ResourcesConfig 按 Constants.RESOURCE_PREFIX 映射到 RuoYiConfig.getProfile()，本身无鉴权拦截。
Fix pattern: 验证远程文件健康度一律用 GET 并只取元信息丢弃正文——`curl.exe -s -o NUL -w "%{http_code} %{content_type} %{size_download}\n" --max-time 10 <url>`，再与本地留存副本的字节数逐一比对确认内容未损坏；不要用 `-I`/HEAD。
Reusable lesson: Don't 依据 HEAD 响应的 Content-Type 判定静态文件类型或可达性，因为该环境 HEAD 与 GET 走不同处理链路会返回误导性 JSON，会把健康的文件误判成"缺失/权限问题"并引出不必要的修复动作；instead 用 GET + -o NUL + -w 三字段做判定，且把 size_download 与已知样本字节数对齐。适用于本仓对外端口上 /profile/** 静态资源的可达性与完整性验证，以及任何 HEAD/GET 处理链可能分叉的 Java 网关前置场景；不适用于已确认由 nginx 等纯静态服务直接响应 HEAD 的环境（此时 HEAD 头可信）。
