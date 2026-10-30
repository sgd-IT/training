---
title: "uni-app微信小程序image标签SVG须用/static绝对路径并经编译产物验证"
usage_scenario:
    - "决定小程序页面内SVG图标引用路径写法"
    - "排查SVG不显示或assets.js未定义报错"
    - "有人主张改相对路径时的证伪依据"
keywords:
    - "uni-app"
    - "微信小程序"
    - "SVG绝对路径"
    - "common/assets.js"
    - "编译产物验证"
source: "auto"
---

结论（以实际编译产物验证为准）：在 uni-app 微信小程序项目 anwei-fire-remote-monitor-mobile 中，模板里 `<image src="/static/xx.svg">` 使用以 '/' 开头的绝对路径可行且为项目既有惯例（pages/profile/device-list.vue:25 即此写法）。验证依据：编译产物 unpackage/dist/dev/mp-weixin/common/assets.js 会把模板中的静态 src 编译为常量（如 "/static/icon-location.svg"），由页面 JS require("../../common/assets.js") 引入，运行时从包根解析，正常渲染。

历史误判纠正：曾据运行时报错 `module 'common/assets.js' is not defined` 得出「必须改用相对路径 ../../static/xxx.svg」的结论，属错误推断——该报错来自 dist 缓存未重建（清除 unpackage/dist 后重新编译即消失），并非绝对路径本身不可用；不得据此把全项目 SVG 引用改成相对路径。

可复用教训：静态资源路径写法不要仅凭记忆或通用文档假设，must 先查阅项目中已上线页面的既有写法，再以编译产物（common/assets.js）验证；不同构建工具对路径的处理逻辑不同。适用边界：模板内静态 src 引用；不适用于需要 JS 侧动态 import/require SVG 的场景。
