---
title: "AesGcm256Util 高频加密内存溢出修复"
usage_scenario:
    - "密码学工具类（如AES/Cipher/Digest）高频调用导致内存增长时的复用优化"
    - "排查GC频繁或老年代占用上升时定位轻量对象重复创建问题"
    - "静态工具类中 ThreadLocal 使用与 cleanup 取舍决策参考"
keywords:
    - "ThreadLocal复用"
    - "堆内存溢出"
    - "密码学工具类"
---

## 任务描述
修复 AesGcm256Util 因高频调用 new GCMBlockCipher(new AESFastEngine()) 导致的堆内存溢出问题。

## 执行过程
```mermaid
graph TD
    A[发现批量推送时OOM] --> B[定位AesGcm256Util.encrypt/decrpyt]
    B --> C[识别每次new cipher为瓶颈]
    C --> D[引入static ThreadLocal<GCMBlockCipher>复用]
    D --> E[修改encrypt/decrypt使用CIPHER_LOCAL.get()]
    E --> F[新增remove方法→Grep确认未调用→SearchReplace删除]
    F --> G[验证无编译错误且内存分配显著下降]
```

## 任务总结
通过 ThreadLocal 复用 GCMBlockCipher 实例，将 cipher 创建频次从 O(n) 降为 O(线程数)，解决堆内存溢出；同时修正 decrypt 中 doFinal 返回长度的使用，避免脏数据；确认 static ThreadLocal 在固定线程池+小对象场景下内存可控，无需 remove 调用。
