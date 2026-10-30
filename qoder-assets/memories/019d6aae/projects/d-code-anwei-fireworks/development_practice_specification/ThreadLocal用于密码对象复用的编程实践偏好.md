---
title: "ThreadLocal用于密码对象复用的编程实践偏好"
usage_scenario:
    - "设计高频加密/解密工具类时选择对象复用策略"
    - "评审ThreadLocal使用方案时参考用户对GC安全性的关注点"
    - "编写类似AesGcm256Util的工具类时确定是否添加remove()方法"
keywords:
    - "ThreadLocal"
    - "密码对象复用"
    - "GC管理"
    - "编程实践"
---

在高频加解密场景中，用户认可使用ThreadLocal复用GCMBlockCipher等重量级密码对象作为优化方案，但强调必须配合GC管理意识，例如提供并调用remove()方法及时清理线程局部变量。
