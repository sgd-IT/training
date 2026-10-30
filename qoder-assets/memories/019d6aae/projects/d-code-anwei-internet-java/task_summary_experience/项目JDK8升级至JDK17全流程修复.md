---
title: "项目JDK8升级至JDK17全流程修复"
usage_scenario:
    - "JDK版本升级后编译报错修复"
    - "Lombok与高版本JDK兼容性问题"
    - "Maven多模块parent artifactId不匹配修复"
keywords:
    - "JDK 17"
    - "Lombok"
    - "maven-compiler-plugin"
    - "jvm.config"
    - "Base64"
---

## 项目 JDK 升级记录（JDK 8 → JDK 17）

### 背景
系统仅安装 JDK 17（openjdk 17.0.18），但项目配置为 JDK 1.8，导致 IntelliJ 报"内部 java 编译器错误"。

### 修复内容
1. **`.idea/misc.xml`**：`languageLevel` 和 `project-jdk-name` 从 `JDK_1_8`/`1.8` → `JDK_17`/`17`
2. **根 `pom.xml`**：`maven.compiler.source/target` 从 `8` → `17`；新增 `lombok.version=1.18.30` 并在 `dependencyManagement` 中覆盖（Spring Boot 2.1.5 BOM 锁定的 1.18.8 不兼容 JDK 17）
3. **`.mvn/jvm.config`（新建）**：添加 `--add-opens jdk.compiler/com.sun.tools.javac.*=ALL-UNNAMED` 共 9 条，供 Lombok 注解处理器访问 JDK 内部模块
4. **`monkey-common/pom.xml`** 和 **`monkey-monitor-api/pom.xml`**：parent artifactId 从 `anwei-internet-of-thing` 修正为 `anwei-internet-java`
5. **`Base64Utils.java`** 和 **`U.java`**：`sun.misc.BASE64Decoder/Encoder`（JDK 9+ 已移除）替换为 `java.util.Base64`

### 关键注意事项
- Lombok 版本必须通过 `dependencyManagement` 显式覆盖，仅在 `properties` 设置 `lombok.version` 无效（被 Spring Boot BOM 覆盖）
- `-J--add-opens` 参数放在 `maven-compiler-plugin` 的 `compilerArgs` 中无效，必须放在 `.mvn/jvm.config` 中
- 升级后所有 6 个模块均编译成功（BUILD SUCCESS）
