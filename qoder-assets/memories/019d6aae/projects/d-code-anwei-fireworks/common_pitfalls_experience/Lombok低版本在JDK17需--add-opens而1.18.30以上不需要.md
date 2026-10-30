---
title: "Lombok低版本在JDK17需--add-opens而1.18.30以上不需要"
usage_scenario:
    - "使用JDK17编译含老版本Lombok的项目时报IllegalAccessError"
    - "排查IDEA内部编译器错误是否因多余--add-opens引起"
keywords:
    - "JDK17"
    - "Lombok"
    - "--add-opens"
    - "IllegalAccessError"
    - "1.18.30以下版本"
source: "auto"
---

JDK 17+ 默认禁止反射访问 jdk.compiler 模块内部类，Lombok 1.18.30 以下版本编译器会因此抛出 IllegalAccessError。解决方法是在 Maven 启动时通过 MAVEN_OPTS 设置 --add-opens 参数开放所需模块包。注意：本项目使用 Lombok 1.18.30+，已原生支持 JDK 17，无需此参数；若错误添加反而会导致 IDEA 内部编译器报错。
