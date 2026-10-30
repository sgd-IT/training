---
title: "手写访问器domain新增字段漏getter致运行时静默丢值"
usage_scenario:
    - "给非Lombok的Java实体新增字段时"
    - "排查接口某字段恒为null或改动未生效"
    - "怀疑后端热重载失败前先查访问器"
keywords:
    - "手写getter setter"
    - "domain新增字段"
    - "字段静默null"
    - "devtools增量编译"
    - "platement domain"
source: "auto"
---

Bug class：给非 Lombok（手写 getter/setter）的 Java domain 实体新增字段后，接口返回该字段恒为 null / setXxx 调用静默不生效，易被误判为"后端没热重载/连错环境"。
Root cause：hc-standard 的 BaseWorkSpecial.java 等 platement domain 类是手写访问器风格，只加 private 字段声明而不补对应 getXxx/setXxx，编译器不会报错（字段本身合法），但序列化与 Service 里的 setViewOnly(...) 之类调用实际作用不到该字段；叠加 IDEA devtools Eclipse 编译器增量产物与命令行 mvn 不一致，症状表现为"改了没生效"，排查方向容易被带偏到重启/换库。
Fix pattern：凡给手写访问器的 domain 新增字段，必须同步补齐 getter+setter 三件套（字段+get+set），并在 mapper 显式列清单场景同步 resultMap/select/insert/update 四处；验证是否真生效要看接口返回体里该 key 有无值，而非只看进程是否重启。
Reusable lesson：不要因为"字段加了就以为能用"而漏写访问器——对非 Lombok 实体新增字段必须连带补 get/set，否则运行时静默丢值且极易误诊为环境问题；适用于本项目所有手写访问器的 domain 类（BaseWorkSpecial、JobSlipBean 等）改动，不适用于使用 @Data/Lombok 自动生成访问器的实体。
