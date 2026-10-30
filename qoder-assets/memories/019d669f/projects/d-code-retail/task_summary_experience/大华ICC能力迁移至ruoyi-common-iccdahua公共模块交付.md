---
title: "大华ICC能力迁移至ruoyi-common-iccdahua公共模块交付"
usage_scenario:
    - "定位ICC取流能力的最新代码位置"
    - "理解新公共模块的接入方式"
    - "为其他第三方平台（如海康）对接提供参考模板"
keywords:
    - "ruoyi-common-iccdahua"
    - "ICC迁移"
    - "模块解耦"
    - "IccServerConfig"
    - "Maven多模块"
source: "auto"
---

本次任务成功将大华ICC平台对接能力（IccOpenApiClient、StartVideoResponse、StartTalkResponse、DahuaRsaUtils、IccRestTemplateUtils）从fireworks模块抽离，封装为独立的ruoyi-common-iccdahua公共模块。具体步骤包括：1) 创建新模块ruoyi-common-iccdahua，定义其pom.xml依赖；2) 将相关类迁移至org.dromara.common.iccdahua包下；3) 更新ruoyi-common/pom.xml和ruoyi-common-bom以注册新模块；4) 修改ruoyi-fireworks/pom.xml，将httpclient5依赖替换为ruoyi-common-iccdahua；5) 更新所有引用文件（VideoCameraController等）的import语句；6) 删除fireworks中已迁移的5个原始文件；7) 通过mvn compile验证整个依赖链。业务侧通过IccServerConfig POJO实现与基础设施的解耦，fireworks模块仅负责实体转换。
