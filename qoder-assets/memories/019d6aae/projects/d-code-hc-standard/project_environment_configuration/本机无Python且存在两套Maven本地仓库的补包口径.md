---
title: "本机无Python且存在两套Maven本地仓库的补包口径"
usage_scenario:
    - "需要在本机跑一次性文本或文件处理脚本时"
    - "用Java单文件源码模式加载第三方库时"
    - "dependency:get补包后定位jar真实路径时"
keywords:
    - "无Python环境"
    - "java单文件源码启动"
    - "maven本地仓库双路径"
    - "dependency:get落点"
    - "-Dartifact冒号加引号"
source: "auto"
---

本机（Windows + PowerShell）未安装可用的 Python：python / python3 / py / pdftotext 均不可用，python 命令会转到 Microsoft Store 提示；node_modules 里的 pdfkit 只能生成 PDF，不能抽取内容。可行的替代路线是 JDK 21 单文件源码启动模式：`java -cp <jars> Xxx.java <args>`，无需先编译。PDF 处理所需 jar 组合与缺依赖报错口径见"本机临时Java工具的可用JDK与jar classpath口径"。

注意本机存在两套 Maven 本地仓库：~/.m2/repository 与 D:\Tool\apache-maven-3.9.15\repo（settings.xml 指定的实际落点），且 commons-logging 版本不一致（前者 1.3.6、后者 1.2）。用 `D:\Tool\apache-maven-3.9.15\bin\mvn.cmd dependency:get "-Dartifact=g:a:v"` 补拉的包只落在后者目录，拼 -cp 时必须按各自真实路径引用，不能统一按 ~/.m2 推断路径。PowerShell 下 -Dartifact 参数含冒号必须整体加引号，否则坐标会被截断解析成错误的 groupId。
