---
title: "正式库证书权限与从业人员表列已存在无需新增SQL"
usage_scenario:
    - "判断权限或字段变更是否需登记SQL清单"
    - "核对正式库菜单权限落点与menu_id"
    - "确认从业人员上报字段是否需DDL"
keywords:
    - "sys_menu证书权限"
    - "contractorPeopleCert edit"
    - "platement_practitioners_person列"
    - "待上正式库清单判定"
    - "anwei1-0001"
source: "auto"
---

正式库 220.203.23.144/tgr-gz-vue（@@hostname = anwei1-0001，MCP serverName tgr-gz-vue，只读）实测存在的结构与权限，可作为"是否需要往《待上正式库变更清单》新增 SQL 登记"的判定依据：

- sys_menu 中证书类按钮权限齐全：base:contractorPeopleCert 的 list/query/add/edit/remove/export（菜单 2167 下，edit 为 menu_id 2170「人员证书修改」）、base:contractorCert 的 list/query/add/edit/remove/export/success（菜单 2383 下，success 为 menu_id 2389「证书通过」）。因此把人员证书修改权限由 admin 放宽为 base:contractorPeopleCert:edit、企业证书审核限管理员用 base:contractorCert:success，均不需要新增菜单权限 SQL。
- platement_practitioners_person 表已含平台上报必填列：personType varchar(2)、birthday varchar(10)、sex int、cardId varchar(50)、department varchar(100)、deleted varchar(1)、createDate/createBy/updateDate/updateBy，另有 certFiles、result_id；注意该表列名为 camelCase，补全上报字段的 Mapper 映射改动无需 DDL。
- 上述两项结论仅覆盖正式库；按既有口径仍需与 160 测试库比对后才能断言跨环境一致。
