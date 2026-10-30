---
title: "设备物业查询的租户双层过滤与Mapper直连真实库单测装配"
usage_scenario:
    - "调试超管与普通租户可见范围不一致"
    - "跨租户切换静默失效需核对双层过滤"
    - "为Mapper编写直连真实库的单测"
keywords:
    - "executeIgnoreWithResult"
    - "eqIfPresent双层过滤"
    - "TenantBaseDO"
    - "Mapper直连MySQL单测"
    - "application-unit-test.yaml"
source: "auto"
---

设备与物业查询的租户双层过滤模式（DeviceInfoService.getDeviceInfoPage 与 FirePropertyServiceImpl.listProperties 同构）：当租户上下文为 1（系统管理员）时，用 TenantUtils.executeIgnoreWithResult 关闭 MyBatis-Plus 租户拦截器，但 mapper 仍保留 eqIfPresent(EiotDeviceInfoDO::getTenantId, reqVO.getTenantId())，reqVO.tenantId 非空时依旧追加 WHERE tenant_id = ?；普通租户则拦截器与 mapper 两层同时过滤。排查「超管看到全部 / 企业只看自己」或跨租户切换静默失效时，须同时核对拦截器口径与该 eqIfPresent 显式条件是否叠加。

Mapper 直连 MySQL 单测的装配边界：业务模块 Mapper 若要绕过 Service 直接验证真实 SQL，不能用框架 H2 基座（不支持 FROM_UNIXTIME / DATE_FORMAT 等 MySQL 专有函数），需自建 application-unit-test.yaml 按 dynamic-datasource 格式配 spring.datasource.dynamic.primary=master 直连开发库，测试类用 @SpringBootTest(webEnvironment=NONE) + @ActiveProfiles("unit-test") + @Import(EnjoyDataSourceAutoConfiguration / DruidDataSourceAutoConfigure / EnjoyMybatisAutoConfiguration / MybatisPlusAutoConfiguration / MybatisPlusJoinAutoConfiguration / EnjoyTenantAutoConfiguration) 让租户拦截器生效，并加 @Transactional 默认回滚防污染开发库；造数用 JdbcTemplate 裸 INSERT 并注意各表 NOT NULL 列。运行命令 mvn -pl module-eiot/module-eiot-biz test -Dtest=XxxTest -DfailIfNoTests=false 须单独前台执行，结果以 surefire-reports/*.txt 的 "Tests run: N, Failures: 0" 为准（管道过滤后 ExitCode 常假报 1）。
