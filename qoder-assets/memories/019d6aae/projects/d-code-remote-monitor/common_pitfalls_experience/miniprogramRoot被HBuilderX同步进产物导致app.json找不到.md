---
title: "miniprogramRoot被HBuilderX同步进产物导致app.json找不到"
usage_scenario:
    - "微信开发者工具报「根据 miniprogramRoot 指定的小程序目录未找到 app.json」时定位修复"
    - "uni-app项目上传生产包前确认产物目录配置正确"
    - "手动修改根目录project.config.json后排查编译产物被污染问题"
keywords:
    - "miniprogramRoot"
    - "HBuilderX"
    - "微信开发者工具"
    - "编译产物"
    - "app.json"
source: "user"
---

在 anwei-fire-remote-monitor-mobile 项目中，HBuilderX 发行/运行时会把源码根目录 project.config.json 的内容同步到 unpackage/dist/{dev,build}/mp-weixin/project.config.json。若曾给根目录配置手动加过 miniprogramRoot 字段，该字段会被带进编译产物；微信开发者工具打开产物目录时会按 miniprogramRoot 再向下一层找 app.json（即 build/mp-weixin/unpackage/dist/build/mp-weixin/），导致「在该目录下未找到 app.json」错误。正确做法：根目录 project.config.json 不要加 miniprogramRoot（它是给 HBuilderX 的，开发者工具不该直接开源码根）；若产物里的 project.config.json 出现了 miniprogramRoot，直接删除该行即可。上传生产包的正确方式是用微信开发者工具直接打开 unpackage/dist/build/mp-weixin 目录作为项目（CLI: cli.bat open --project <build路径>，工具位于 D:\Tool\微信web开发者工具\）。上传前用 grep 检查产物 utils/request.js 的 BASE_URL：fire-remote-admin.anxunwang.cn 为生产、192.168.250.130 为开发包。
