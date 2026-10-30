---
title: "JDK 17中sun.misc.BASE64被移除需替换为java.util.Base64"
usage_scenario:
    - "JDK升级到17后编译报sun.misc包不存在"
    - "在Java 17+项目中处理Base64编码/解码"
keywords:
    - "JDK 17"
    - "BASE64"
    - "sun.misc"
    - "java.util.Base64"
---

JDK 17 移除了 `sun.misc.BASE64Decoder` 和 `sun.misc.BASE64Encoder`，必须替换为 `java.util.Base64.getDecoder().decode()` 和 `java.util.Base64.getEncoder().encodeToString()`。直接使用旧类会导致编译错误 `package sun.misc does not exist`。（来源：Bash）
