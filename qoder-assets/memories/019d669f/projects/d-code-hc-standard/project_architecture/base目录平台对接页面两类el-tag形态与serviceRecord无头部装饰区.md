---
title: "base目录平台对接页面两类el-tag形态与serviceRecord无头部装饰区"
usage_scenario:
    - "修改base目录页面头部样式或装饰标签"
    - "按class串定位待删el-tag元素范围"
    - "调整四个平台对接页面上报状态列展示"
keywords:
    - "module-heading"
    - "el-tag形态区分"
    - "serviceRecord页面结构"
    - "平台对接页面"
    - "装饰性标签"
source: "auto"
---

ruoyi-ui 的 views/base 下平台对接类页面存在两种 el-tag 形态，删除或改造时必须区分：

1. 头部装饰标签：位于 header class="module-heading" 内标题 h2 旁，写法为 el-tag effect="plain" type="info|warning"（文案"基础档案"/"运行管理"；未写 size，Element UI 默认 medium），DevTools 渲染 class 为 el-tag el-tag--info/warning el-tag--medium el-tag--plain。第三方单位(thirdPartyUnit)、装置开停车(deviceCtrl)、大检修(overhaul)原有各 1 处，这 3 处已在"四页面标题装饰标签删除"任务中全部移除；服务记录(serviceRecord)采用旧版页面结构、本无 module-heading 装饰区。因此当前这 4 个页面的 header 内已无任何 el-tag。

2. 表格功能性状态标签：写在 el-table-column 的 slot 内，带 size="small" 且无 plain，渲染 class 含 el-tag--small，承载同步状态/上报状态/类型等信息（如 :type 绑定 syncStatus、status(scope.row).type）。共 6 处仍保留。

推论：用户以 DevTools 复制的 class 串提需求时，可用 size/plain 是否存在判定所指是头部装饰款还是表格功能款，避免误删信息性标签。公共组件 src/components/PlatementQuery 内不含任何 el-tag。
