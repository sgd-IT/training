---
title: "RuoYi代码生成器对多张表批量生成CRUD代码"
usage_scenario:
    - "当需要为多张数据库表快速生成标准CRUD代码时"
    - "当项目引入新业务模块并需同步创建后端接口与前端页面时"
keywords:
    - "RuoYi"
    - "代码生成器"
    - "CRUD"
    - "批量生成"
    - "菜单SQL"
---

## Task description
- Core requirement: 使用RuoYi-Vue-Plus内置代码生成器，为9张已存在的数据库表批量生成基础CRUD代码
- Task background: 项目已集成ruoyi-gen模块，其generator.yml定义了默认包路径和作者；代码生成器提供Web界面和后端API，支持单表/批量生成、模板预览及菜单SQL生成；生成的代码需按约定结构放入对应Maven模块并执行SQL初始化菜单

## Execution process
1. 进入系统工具 → 代码生成页面，点击"导入"按钮，选择数据源并勾选9张目标表
2. 对每张表点击"编辑"，配置生成模板、包路径、模块名、业务名、功能名、作者及前端模板，并在字段配置表中设置查询条件、编辑字段和列表展示
3. 点击"预览"查看将生成的Java实体类、Mapper、Service、Controller、XML及前端Vue/TSX文件
4. 勾选全部9张表，点击"批量生成"下载包含所有代码的zip包
5. 解压zip，将Java文件按包路径放入ruoyi-modules/ruoyi-fireworks模块，XML放入对应resources/mapper目录
6. 执行生成的menu.sql，在数据库中创建菜单和权限

## Related files
- d:/code/anwei-fireworks-retail/ruoyi-modules/ruoyi-gen/src/main/resources/generator.yml
- d:/code/anwei-fireworks-retail/ruoyi-modules/ruoyi-gen/src/main/java/org/dromara/gen/controller/GenController.java

## Notes
- 生成的是基础骨架，后续需手动补充@RepeatSubmit、@Log、数据权限等增强注解
- 如有业务唯一性约束，需补充validEntityBeforeSave校验
- 表前缀（如fw_）可在导入后编辑时设置"去除前缀"以优化类名

## Task overview
成功为9张表批量生成了完整的CRUD代码：完成了从Web界面导入、逐表配置、批量下载、项目结构整合到菜单SQL执行的全流程；生成的代码符合RuoYi-Vue-Plus框架规范，可直接编译运行。
