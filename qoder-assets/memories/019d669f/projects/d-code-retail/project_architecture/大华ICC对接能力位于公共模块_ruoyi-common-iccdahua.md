---
title: "大华ICC对接能力位于公共模块 ruoyi-common-iccdahua"
usage_scenario:
    - "ICC取流相关代码定位"
    - "模块依赖与包路径确认"
    - "旧视频功能清理决策"
keywords:
    - "ruoyi-common-iccdahua module"
    - "IccServerConfig decoupling"
    - "common module registration"
    - "legacy iccdahua pending removal"
    - "org.dromara.common.iccdahua"
source: "user"
---

大华ICC对接能力已抽为独立公共模块 ruoyi-common-iccdahua（包 org.dromara.common.iccdahua，子包 service/model/utils）：IccOpenApiClient、StartVideoResponse、StartTalkResponse、DahuaRsaUtils、IccRestTemplateUtils 均在此模块；模块已在 ruoyi-common/pom.xml 与 ruoyi-common-bom 注册，httpclient5 依赖随之迁移，启动类 scanBasePackages=org.dromara 覆盖该包、无需自动装配文件。客户端与业务已解耦：方法参数由实体 IccMediaServer 改为 IccServerConfig POJO，fireworks 侧 IccMediaServerServiceImpl.getCurrentConfig() 负责实体→POJO 转换后传入。fireworks/iccdahua 目录仅剩旧 yml 版 ICC 代码（IccProperties/IccAuthService/IccDeviceService/旧响应模型），无 icc.* 配置实际不可用，待旧视频页切换后整体删除。
