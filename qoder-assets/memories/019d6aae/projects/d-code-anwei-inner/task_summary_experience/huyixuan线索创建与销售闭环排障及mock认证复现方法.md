---
title: "huyixuan线索创建与销售闭环排障及mock认证复现方法"
usage_scenario:
    - "为CRM销售闭环各节点造演示数据并验证链路"
    - "使用yudao mock认证调试后端接口权限问题"
    - "排查合同表单保存时产品校验失败"
keywords:
    - "huyixuan线索创建"
    - "mock认证Bearer test"
    - "crm_project_quote验证"
    - "产品不存在crm_product空表"
    - "销售闭环排障"
source: "auto"
---

## Task description
- Core requirement: 1) 用 huyixuan（id=159）账号新建一条线索；2) 排查合同表单中「冻结报价」选不到、「产品不存在」、「无法选择项目」三个连续报错
- Task background: 前一轮已造好报价数据（项目8/报价22/明细50），用户现在要走通「线索→客户→项目→报价→合同」完整销售闭环；新线索插入后用户在合同表单页面遇到多个阻断

## Execution process
1. MCP 查表结构确认 crm_clue 列名（level 非 level_id）、现有数据格式（tenant_id=1, customer_type=1, owner_user_id 为 bigint）、自增值 AUTO_INCREMENT=12
2. 通过 MCP 插入线索 id=12（name='广州金桥养老院智慧消防远程值守线索', owner_user_id=159, creator='159', industry_id=4, level=1, source=2, transform_status=b'0', tenant_id=1）
3. JOIN system_users 验证 owner_name='胡艺璇' ✓
4. 用户反馈「冻结报价选不到」→ 排查报价表数据：status_hex=E5B7B2E586BBE7BB93（已冻结正确）、valid_until=2026-10-29、project_id=8、deleted=0 ✓
5. 排查后端 DO 字段映射 CrmProjectQuoteDO.java → 与表结构一致 ✓
6. 发现本地 mock-enable=true，确认 mock-secret 默认值 'test'，构造请求头 Authorization: Bearer test159 + tenant-id: 1
7. 以 huyixuan 身份直调 /crm/project-quote/list?projectId=8 → HTTP 200 返回报价 22（status=已冻结, validUntil=2026-10-29）✓
8. 以 admin 身份同样调用也成功 ✓ → 排除权限和数据问题
9. 排查 .env.local → VITE_BASE_URL=http://localhost:48080，确认前端连的就是本地后端
10. 确认全库仅一个金桥项目(id=8)/客户(id=103)，无重复数据；线索12未转化
11. 排查角色权限：role 160 拥有 crm:project:query/crm:project:create-quote/crm:contract:create 全部所需权限 ✓
12. 排查前端 chooseProject → handleProjectChange 链路：内部有 try-catch 保护客户/调研调用，getProjectQuoteList 无 catch 但接口实测正常
13. 上一轮遗留问题「产品不存在」：crm_project_quote_item.product_id=NULL，合同保存时 validateContractProducts 走 crm_product 表（空表），convertSet 只过滤 null 不过滤 0 → 任何携带 productId 的明细都会失败
14. 用户最新反馈「无法选择项目」尚未定位根因（Playwright 不可用，排障中断）

## Related files
- 本任务无代码文件改动；数据库变更：crm_clue 新增 id=12

## Notes
- yudao 框架 mock 认证用法：Authorization: Bearer {mockSecret}{userId}，mockSecret 默认为 'test'（SecurityProperties.java），需带 tenant-id header；此方式不加载角色权限集合，仅设置 userId 和 tenantId，因此 @PreAuthorize 注解的接口可能表现不同
- 当前运行的后端进程是 IDEA 启动的 PID 36520（12:45），日志只进 IDEA 控制台不落文件，导致无法直接读取运行时异常
- run-app.log/run-app2.log 是之前命令行启动留下的旧日志，不代表当前运行实例
- crm_customer_level 字典：1=A重点客户 2=B普通客户 3=C非优先客户；crm_customer_source：1=运营获客 2=销售拓客 3=应急名单 4=其他

## Task overview
部分完成：线索 id=12 已成功创建并验证；合同表单链路的后端数据和接口均验证正常（以 huyixuan 身份 mock 调用 project-quote/list/customer/get/research/page 全部 200）；「产品不存在」根因已确认（crm_product 空表 + product_id NULL）；「无法选择项目」的具体前端故障点尚未定位（怀疑浏览器缓存或 Playwright 复现未完成），需要进一步跟进。
