---
title: "改动 module-eiot-biz 后单编 module-ai-biz 会撞本地仓旧包"
usage_scenario:
    - "多模块项目跨模块改动后编译"
    - "代码明明存在却报找不到符号"
    - "AI模块引用eiot新方法排查"
keywords:
    - "maven -am"
    - "找不到符号"
    - "本地仓库旧包"
    - "跨模块编译"
source: "auto"
---

Bug 类型：跨模块编译用了本地仓库的旧 jar。
根因：module-ai-biz 依赖 module-eiot-biz，执行 `mvn -o -pl module-ai/module-ai-biz compile` 时只从本地仓库取已安装的 eiot-biz，其中没有本次新增的 Mapper/方法，编译报"找不到符号"，语言服务同时报方法不存在，容易被误判为代码错误。
修复模式：只要改了下游模块被上游引用的类，就加 `-am` 让 reactor 从源码一起构建（`mvn -o -pl module-ai/module-ai-biz -am compile -DskipTests`），或先把下游模块 install 到本地仓库。
可复用教训：出现"方法明明写了却找不到符号"时，先确认编译命令是否带 -am、依赖模块是否重新编译，再怀疑代码本身；IDE 语言服务对未重新构建的跨模块引用同样会报红。
