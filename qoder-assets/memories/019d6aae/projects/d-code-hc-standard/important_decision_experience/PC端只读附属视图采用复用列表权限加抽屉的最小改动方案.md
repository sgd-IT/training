---
title: "PC端只读附属视图采用复用列表权限加抽屉的最小改动方案"
usage_scenario:
    - "为PC后台已有列表补只读详情或留痕查看界面"
    - "决定新接口是复用权限还是新建按钮权限"
    - "后端命令行编译报错时判断改动是否已生效"
keywords:
    - "复用列表权限不新建perms"
    - "el-drawer替代独立路由页"
    - "行数据兜详情字段"
    - "sys_menu授权成本"
    - "mvn缺-am误判编译失败"
source: "auto"
---

Conclusion: 给 hc-standard PC 端补"作业票详情/中断记录"查看能力时，后端接口 GET /base/workSpecial/interruptList/{outerId} 复用已有的列表权限 base:workSpecial:list（不新建按钮权限、不改 sys_menu），前端以"操作列加详情按钮 + el-drawer 抽屉"承载视图（不新建路由页面），票面字段直接取表格行数据（不再调 getWorkSpecial 二次查票）。

Rationale (trade-off): 该数据的可见范围本就等同于"能看特殊作业列表的人"，没有更细的权限诉求；复用可省掉新增 perms 字符串、sys_menu 按钮行以及"新菜单需授权给所有启用角色"这一整套动作（该项目有此历史坑），改动面从 5 处降到 3 个文件。抽屉而非独立路由页，是因为 RuoYi 侧边菜单由 sys_menu 动态生成，新增路由页要配套菜单与授权，而详情只是列表行的附属视图。行数据兜字段是因为列表 SQL 已返回 workNo/workStatus/ticketsType/jhr/workUnitName 等全部展示项，再查一次纯属多余往返。

Rejected alternatives: 1) 新建 base:workSpecial:interrupt 按钮权限并插入 sys_menu——权限粒度更清晰、可按角色单独收放，但在无差异化诉求时额外引入授权维护成本，属合理但收益不足；2) 独立详情页 + 路由跳转——适合字段极多需全屏编辑的场景，本例只读展示十余个字段，代价是与菜单体系耦合；3) 进抽屉时重新按 outerId 查票——能保证数据最新，但多一次接口往返且列表本身已是刷新结果。

Applicable / expiry conditions: 适用于"只读附属视图 + 可见范围与既有列表权限一致 + 字段已在列表 SQL 中齐备"的 PC 后台增量功能；一旦出现以下任一条件须重新评估：① 需要把中断记录的查看权收给比列表更小的角色集（则必须拆独立 perms）；② 详情要支持编辑或字段数超出列表返回范围（则改为独立页面 + 单票查询）；③ 列表 SQL 精简掉某些字段导致抽屉缺数据（则回退为按 outerId 二次查询）。

补充一条实施期教训：在该机器上验证后端新接口时，命令行 mvn -pl ruoyi-admin compile 会因缺少 -am 报 ruoyi-generator:jar:3.8.6 not found，这并不代表代码有问题——IDEA 已在自动编译并使 devtools 热重启加载了新接口（实测 code=200）。若据此判定"编译失败=改动未生效"而反复回滚重做，会浪费时间；正确做法是直接调用新接口探测是否已生效，打包才用 mvn -pl ruoyi-admin -am clean package。
