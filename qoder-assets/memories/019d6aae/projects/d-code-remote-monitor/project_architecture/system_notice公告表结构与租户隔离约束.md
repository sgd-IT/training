---
title: "system_notice公告表结构与租户隔离约束"
usage_scenario:
    - "复用公告表承载新业务数据时"
    - "开发或修改公告模块前后端功能"
    - "判断表是否受多租户拦截器过滤"
keywords:
    - "system_notice表结构"
    - "NoticeController与NoticeMapper"
    - "公告菜单权限1036-1039"
    - "富文本content限制"
    - "公告表未进租户忽略清单"
source: "auto"
---

module-system 的公告能力（system_notice）现状与约束：

- 表结构：id / title varchar(50) NOT NULL / content text NOT NULL / type tinyint（1通知 2公告）/ status tinyint（0正常 1关闭）/ creator / create_time / updater / update_time / deleted / tenant_id bigint NOT NULL DEFAULT 0。生产库该表存在但 0 行数据。
- 后端全套已就绪：NoticeDO、NoticeMapper（likeIfPresent(title) + eqIfPresent(status) + orderByDesc）、NoticeService / NoticeServiceImpl、NoticeController，位于 module-system/module-system-biz 的 notice 包下。
- 前端也已就绪：src/api/system/notice/index.ts 与 src/views/system/notice 页面；NoticeForm.vue 的 content 走富文本编辑器录入，无法承载结构化条目数组。
- 菜单与权限在库中已配置：system_menu id=107「通知公告」，按钮权限 id=1036 system:notice:query、1037 system:notice:create、1038 system:notice:update、1039 system:notice:delete。
- 关键反直觉点：system_notice 不在 server/src/main/resources/application.yaml 的 yudao.tenant.ignore-tables 清单内，因此多租户拦截器会给其 SQL 追加 tenant_id 条件，各租户只能看到自己的公告。若要承载"全系统共享"性质的数据（如更新日志），必须先把表名加入忽略清单，并另开一个不带 system:notice:query 权限校验的只读接口。
