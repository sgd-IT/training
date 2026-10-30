---
title: "Bash沙箱终端原始读文件会乱码，验证改动须用编译产物javap或工具侧读取"
usage_scenario:
    - "终端读取源码显示乱码或文件找不到"
    - "验证代码改动是否真实编译生效"
    - "用PowerShell统计文件行数或核对方法调用点结果异常时"
keywords:
    - "沙箱终端乱码"
    - "Get-Content行数不准"
    - "Select-String漏匹配"
    - "文件视图不一致"
    - "javap验证产物"
    - "mvn编译校验"
source: "auto"
---

本机 Bash 终端（Qoder IDE sandbox.exe 包装的 PowerShell/Node 进程）原始读取工作区文件存在视图差异：用 Get-Content、Select-String、System.IO.File::ReadAllBytes 直接读源码或工具新建的脚本文件，可能得到乱码/密文（字节头如 88 7d 1c...）；新建文件在终端侧短时（甚至十几分钟）不可见；jshell 读脚本文件同样得到乱码。但 JDK 工具进程读取同一文件是正常明文：mvn 编译成功、javac 可编译新源码、javap 可 dump 编译产物。因此核对代码改动是否真实生效的正确姿势：① mvn clean 后 compile，看日志出现 Recompiling/Compiling N source files；② javap -p（必要时加 -c）检查 target/classes 中方法签名与编译器生成 lambda（如 lambda$isDuplicate$0）佐证源码版本；③ 内容核对用工具侧 Read/Grep，不要用 PowerShell 原始读文件正文或 Get-ChildItem 判断存在性（会误判为未同步/乱码）。
