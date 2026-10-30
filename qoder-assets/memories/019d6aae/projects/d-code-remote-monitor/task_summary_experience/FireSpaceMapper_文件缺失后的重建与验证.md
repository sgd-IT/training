---
title: "FireSpaceMapper 文件缺失后的重建与验证"
usage_scenario:
    - "When a Mapper interface is missing and needs to be reconstructed from service usage"
    - "When verifying that a newly added Mapper method compiles and integrates correctly"
keywords:
    - "FireSpaceMapper"
    - "MyBatis-Plus"
    - "default method"
    - "forced compile"
    - "build verification"
source: "auto"
---

## Task description
- Core requirement: restore the missing FireSpaceMapper.java file and verify its compilation and correctness
- Task background: FireSpaceMapper.java was absent from the codebase; the FireSpaceServiceImpl relies on fireSpaceMapper.createFireSpace(...) to insert and return the new ID; the project uses MyBatis-Plus, which supports automatic primary key backfilling and tenant field injection

## Execution process
1. Confirmed FireSpaceMapper.java did not exist on disk
2. Created FireSpaceMapper.java with a default createFireSpace(FireSpaceDTO) method that builds a FireSpaceDO, inserts it via MyBatis-Plus's insert(), and returns space.getId()
3. Added the required FireSpaceMapper import to FireSpaceServiceImpl.java
4. Performed a forced clean-and-compile to verify: 'Compiling 550 source files' confirmed full recompilation of module-eiot-biz, and 'BUILD SUCCESS' confirmed no compilation errors

## Related files
- /module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/dal/mysql/firewatch/FireSpaceMapper.java
- /module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/service/firewatch/FireSpaceServiceImpl.java

## Notes
- The 'Nothing to compile' incremental build detection in this repository is unreliable; a forced clean-and-compile is necessary to confirm changes are compiled.

## Task overview
Successfully restored the missing FireSpaceMapper.java file: implemented a default createFireSpace method that correctly uses MyBatis-Plus for auto-increment ID backfilling and tenant-aware insertion; added the required import to FireSpaceServiceImpl.java; verified full recompilation and BUILD SUCCESS with a forced clean-and-compile.

## Notes

- 本条正文记录的 default createFireSpace(FireSpaceDTO) 方法已不是当前状态：后续会话实测 FireSpaceMapper.java 被重写为仅 extends BaseMapperX&lt;FireSpaceDO&gt; 的裸接口，插入改由 ServiceImpl 直接调用 fireSpaceMapper.insert(space) 完成，因此引用"createFireSpace 依赖 Mapper 默认方法"的部分应视为过时。
- 另需注意：该裸接口版本曾被误删 com.enjoyiot.framework.mybatis.core.mapper.BaseMapperX 的 import 而导致编译报错，编辑期 IDE 语言服务报的 BaseMapperX/selectCount/log cannot be resolved 属缓存滞后，结论仍以 JDK 21 下 clean + 定向 compile 是否出现 Compiling N source files 且 BUILD SUCCESS 为准。此"强制全量编译才可信"的注意点仍然有效。
- 补充修正（编码恢复与注释评审会话实测）："必须 clean 才能确认改动被编译"这一条偏严。实测 `mvn -o -pl module-eiot/module-eiot-biz -am -DskipTests compile`（不带 clean）即可输出 `Compiling 584 source files` 并 BUILD SUCCESS，判定标准应是日志出现 Recompiling/Compiling N source files，而非是否执行了 clean。另一必要前提：单用 `-pl <biz模块>` 而不带 `-am` 会因 m2 里兄弟模块 jar 过期或缺失而报大量"找不到符号"（本次命中 AlertApiImpl / RemoteConsoleController / UdpEventNotifier），属环境假故障，须带 `-am` 从源码同批构建后再判成败；管道过滤 mvn 输出时 Select-Object 会让命令返回码变 1，不能据此判定编译失败，要看 BUILD SUCCESS/FAILURE 行。
