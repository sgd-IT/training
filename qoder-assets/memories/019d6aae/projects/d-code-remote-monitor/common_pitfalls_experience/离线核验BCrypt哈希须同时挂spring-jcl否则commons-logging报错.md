---
title: "离线核验BCrypt哈希须同时挂spring-jcl否则commons-logging报错"
usage_scenario:
    - "临时核验用户表bcrypt密码对应明文"
    - "批量造号前离线生成密码哈希"
    - "java单文件运行报commons-logging类缺失"
keywords:
    - "BCryptPasswordEncoder"
    - "spring-jcl缺失"
    - "NoClassDefFoundError"
    - "单文件源码模式"
    - "密码哈希核验"
source: "auto"
---

Bug class: 用 JDK 单文件源码模式离线生成/校验 BCrypt 密码哈希时运行期类缺失。
Root cause: spring-security-crypto 的 BCryptPasswordEncoder 在初始化时依赖 commons-logging API，而 Spring 6.x 由 spring-jcl 提供 org.apache.commons.logging.* 实现；只把 spring-security-crypto 加进 classpath 会在构造 encoder 时抛 NoClassDefFoundError: org/apache/commons/logging/LogFactory，与 BCrypt 算法本身无关，容易被误判为 jar 版本不匹配或 hash 串损坏。
Fix pattern: classpath 同时拼接三件套 —— spring-security-crypto + spring-core + spring-jcl（均在本地 Maven 仓库 ~/.m2/repository 下按版本取 jar），再用 `java -cp "<a>;<b>;<c>" Verify.java` 单文件源码模式运行；先对已知明文比对存量 hash，再逐账号生成新 hash 并 matches 自校验。
Reusable lesson: Don't 只挂 spring-security-crypto 就以为足够，因为日志门面缺失会让 encoder 构造直接失败并被误读成"哈希不可用"；instead 一并加入 spring-jcl。适用于本项目在无 IDE/无构建脚本环境下临时核验 system_users.password（如确认演示账号初始密码、批量造号前校验哈希）；不适用于走完整 Maven/Gradle 依赖解析的运行场景，那种场景由传递依赖自动带入 spring-jcl。
