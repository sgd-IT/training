---
title: "视频拉流后端交付：摄像头表、ICC配置表与WSPlayer接口"
usage_scenario:
    - "视频拉流功能后续开发"
    - "摄像头 CRUD 与权限排查"
    - "ICC 取流接口调试"
keywords:
    - "fireworks_video_camera"
    - "icc_media_server"
    - "WSPlayer"
    - "IccOpenApiClient"
    - "fireworks:camera"
source: "user"
---

## Task description
- Core requirement: implement the video streaming backend based on the '视频拉流对接复用指南.md' document, covering database tables, controller, and ICC platform integration
- Task background: the project already had an old ICC integration (yml-configured via IccProperties) and a legacy VideoController, but lacked database-stored ICC configuration and a retail_id-isolated video camera table; the new design uses fireworks_video_camera (retail_id-scoped) and fireworks_icc_media_server (global config, exempt from tenant isolation) to support WSPlayer streaming and voice talk

## Execution process
1. Analyzed the guide, project structure, existing ICC code (iccdahua package), controller conventions, SQL patterns, and tenant isolation architecture (PlusTenantLineHandler + RetailDataScopeHelper)
2. Confirmed the user's requirement for three-role video viewing scope (superadmin/regulator/normal user) and adapted the design accordingly
3. Created two SQL tables: fireworks_video_camera (VARCHAR(36) id, retail_id, device_id/channel_id/quality/status) and fireworks_icc_media_server (global config); updated PlusTenantLineHandler.IGNORE_TABLES to exempt fireworks_icc_media_server from retail_id injection
4. Implemented the full backend stack: entities (VideoCamera, IccMediaServer), mappers, services (IVideoCameraService, IIccMediaServerService, VideoCameraServiceImpl, IccMediaServerServiceImpl), IccOpenApiClient (token management with RSA encryption, double-check locking, SSL-ignore RestTemplate, StartVideo/StartTalk/HLS/FLV/ICC-device-listing), and VideoCameraController (/fireworks/video/camera/** with permissions fireworks:camera:list/query/add/edit/remove/stream/talk)
5. Compiled the module successfully using Maven with JDK 21

## Related files
- d:\code\retail\anwei-fireworks-retail\script\sql\fireworks_incremental.sql
- d:\code\retail\anwei-fireworks-retail\ruoyi-common\ruoyi-common-tenant\src\main\java\org\dromara\common\tenant\core\PlusTenantLineHandler.java
- d:\code\retail\anwei-fireworks-retail\ruoyi-modules\ruoyi-fireworks\src\main\java\org\dromara\fireworks\domain\VideoCamera.java
- d:\code\retail\anwei-fireworks-retail\ruoyi-modules\ruoyi-fireworks\src\main\java\org\dromara\fireworks\domain\IccMediaServer.java
- d:\code\retail\anwei-fireworks-retail\ruoyi-modules\ruoyi-fireworks\src\main\java\org\dromara\fireworks\mapper\VideoCameraMapper.java
- d:\code\retail\anwei-fireworks-retail\ruoyi-modules\ruoyi-fireworks\src\main\java\org\dromara\fireworks\mapper\IccMediaServerMapper.java
- d:\code\retail\anwei-fireworks-retail\ruoyi-modules\ruoyi-fireworks\src\main\java\org\dromara\fireworks\service\IVideoCameraService.java
- d:\code\retail\anwei-fireworks-retail\ruoyi-modules\ruoyi-fireworks\src\main\java\org\dromara\fireworks\service\IIccMediaServerService.java
- d:\code\retail\anwei-fireworks-retail\ruoyi-modules\ruoyi-fireworks\src\main\java\org\dromara\fireworks\service\impl\VideoCameraServiceImpl.java
- d:\code\retail\anwei-fireworks-retail\ruoyi-modules\ruoyi-fireworks\src\main\java\org\dromara\fireworks\service\impl\IccMediaServerServiceImpl.java
- d:\code\retail\anwei-fireworks-retail\ruoyi-modules\ruoyi-fireworks\src\main\java\org\dromara\fireworks\iccdahua\service\IccOpenApiClient.java
- d:\code\retail\anwei-fireworks-retail\ruoyi-modules\ruoyi-fireworks\src\main\java\org\dromara\fireworks\controller\VideoCameraController.java

## Notes
- None

## Task overview
Successfully delivered the complete video streaming backend: two database tables created and integrated into the project's tenant isolation architecture; full controller and service layer implemented with strict three-role scope control (superadmin/regulator/normal user); IccOpenApiClient encapsulates all ICC platform interactions (token, RSA encryption, stream URLs, voice talk); compilation verified with JDK 21.

## 交付后订正（ICC能力迁移至公共模块，2026-09-20）
以下为交付后经用户决策执行的模块迁移，原正文为历史快照保留不变：
- IccOpenApiClient 及 StartVideoResponse/StartTalkResponse/DahuaRsaUtils/IccRestTemplateUtils 已迁至新公共模块 ruoyi-common-iccdahua（包 org.dromara.common.iccdahua，子包 service/model/utils）；上文 Related files 中 'fireworks/iccdahua/service/IccOpenApiClient.java' 路径已失效，fireworks/iccdahua 目录现仅存旧 yml 版 ICC 代码（待删）。
- IccOpenApiClient 方法参数由实体 IccMediaServer 解耦为 IccServerConfig POJO（org.dromara.common.iccdahua.model）；fireworks 侧 IccMediaServerServiceImpl.getCurrentConfig() 现返回 IccServerConfig（BeanUtil 实体→POJO 转换后传入客户端）。
- httpclient5 依赖随迁至 ruoyi-common-iccdahua，ruoyi-fireworks pom 改为依赖新模块；迁移后 Maven（JDK21）编译验证通过。
