---
title: "infra代码生成器组件盘点与Mapper落位规则核查"
usage_scenario:
    - "新增业务表需要快速生成CRUD骨架时"
    - "确定Mapper接口与XML的放置位置时"
    - "排查生成的代码目录名与项目不符时"
keywords:
    - "代码生成器"
    - "codegen"
    - "Mapper存放目录"
    - "dal/mysql"
    - "mapper-locations"
    - "BaseMapperX"
    - "yudao框架"
source: "auto"
---

## Task description
- Core requirement: 确认项目是否存在快速生成 CRUD 代码的组件，并说明生成的 Mapper 应放在哪里。
- Task background: 项目基于 yudao/ruoyi-vue-pro 系框架（包名 com.enjoyiot，module-* 多模块布局），需核实 infra 侧是否保留完整 codegen 能力以及生成产物的目录约定与实际业务模块写法是否一致。

## Execution process
1. 检索 module-infra-biz 下 codegen 包，确认后端链路齐全：CodegenController（/infra/codegen 的 create/update/sync-from-db/preview/download）、CodegenServiceImpl、CodegenBuilder（字段类型与 HTML 控件、查询条件自动推断）、CodegenEngine（Velocity 渲染）、CodegenTemplateTypeEnum（单表 ONE / 树表 TREE / 主子表 MASTER_NORMAL、MASTER_ERP、MASTER_INNER）。
2. 定位模板资源目录 module-infra-biz/src/main/resources/codegen/，含 java/、vue3/、vue/、vue3_vben/、sql/ 共约 36 个 .vm 文件。
3. 从 application.yaml 的 enjoyiot.codegen 块读到 front-type: 20（Vue3 标准模板）、unit-test-enable: false；base-package 由 platform.info.base-package 提供（com.enjoyiot）。
4. 确认前端配套：src/api/infra/codegen/index.ts 与 src/views/infra/codegen/ 下 index.vue、ImportTable.vue、EditTable.vue、PreviewCode.vue。
5. 追踪 CodegenEngine 中 javaModuleImplMainFilePath 与 mapperXmlFilePath 的模板字符串，得出 Mapper 接口生成于 dal/mysql/${businessName}/${className}Mapper.java、XML 生成于 src/main/resources/mapper/${businessName}/，且 zip 内层目录名模板写死为 enjoy-module-${moduleName}。
6. 对比实际仓库结构（module-eiot/module-eiot-biz/.../eiot/dal/mysql/firewatch/ 下 FireAlarmOrderMapper、FireDeviceMapper），确认业务模块目录名为 module-xxx 而非 enjoy-module-xxx。
7. 全局搜索 resources/mapper/**/*.xml 与 mapper-locations 配置均为空，仅 framework/common 存在 1 个模板残留 XML，据此判定本项目不使用 Mapper XML。

## Related files
- （本任务为只读查证，未修改或新建任何文件）

## Notes
- 生成器 zip 内的 enjoy-module-xxx 两级目录名与本项目实际的 module-xxx 不一致，解压落位时必须改名对应，否则找不到目标模块。
- application.yaml 未配置 mybatis mapper-locations，因此生成出的 Mapper XML 放进 resources/mapper 也不会被扫描到；正确做法是丢弃 XML，简查询用 @Select 注解、复杂查询用 BaseMapperX + LambdaQueryWrapperX，与现有风格保持一致。
- 判断"某类文件是否真的在用"不能只看生成器模板声明，必须回到仓库做 glob 与配置项双重验证，否则会给出错误的放置建议。

## Task overview
已完成（结论型交付，无代码改动）：确认项目自带可用的 infra codegen 代码生成器，前后端与模板齐备，支持单表/树表/主子表三类模板并产出 Controller+VO+DO+Mapper+Service+SQL+Vue 页面；Mapper 的实际落位约定为 module-{name}/module-{name}-biz/src/main/java/com/enjoyiot/module/{name}/dal/mysql/{业务名小写}/{Xxx}Mapper.java，Mapper XML 在本项目废弃不用。
