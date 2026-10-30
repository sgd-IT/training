---
title: "VS Code Java语言服务JDK未加载导致基础类型解析失败"
usage_scenario:
    - "VS Code中Java文件大量报Object/String/Set等基础类型无法解析"
    - "pom.xml指定JDK版本但VS Code未识别对应JRE系统库"
    - "Java语言服务索引异常导致全项目红色波浪线"
keywords:
    - "VS Code"
    - "Java语言服务"
    - "JDK配置"
    - "Clean Java Language Server Workspace"
source: "auto"
---

VS Code Java语言服务未正确加载JDK系统库时，会报`java.lang.Object cannot be resolved to a type`等基础类型解析错误（而非代码语法错误）。根本原因是pom.xml中配置了`<java.version>21</java.version>`，但VS Code未识别或缓存损坏。解决方案：执行命令`Java: Clean Java Language Server Workspace` → 选择`Restart & Delete`强制重建工作区索引；同时确保`java.configuration.runtimes`中已声明JDK 21路径，且`java.jdt.ls.java.home`指向同一JDK 21安装目录（来源：VS Code Java扩展）
