---
title: "Element UI 远程下拉点开即选需 visible-change 手动加载并做回显保护"
usage_scenario:
    - "开发可搜索的人员/数据下拉选择框"
    - "排查远程下拉点开无选项问题"
    - "修复下拉框回显显示为id"
keywords:
    - "el-select remote"
    - "visible-change"
    - "remoteMethod 初始守卫"
    - "下拉回显保护"
    - "Element UI 源码"
source: "auto"
---

## 使用场景
用 el-select（filterable + remote）实现"点开下拉即可从列表选择"的人员/数据选择框时（Element UI 2.x，如 RuoYi 前端），默认交互是展开为空、必须输入关键字才触发远程搜索。

## 使用方法
1. 展开时手动加载：绑定 @visible-change，展开（true）时调用加载方法并传空关键字，使后端不加过滤条件返回默认列表（如 searchPerson('')）。
2. 保留输入远程过滤：remoteMethod 正常绑定；空关键字也要能请求（后端 name 条件 null 时不过滤）。
3. 回显保护：v-model 已有值但该值不在 options 列表中时，el-select 显示为空或原始 id；需在每次加载结果中把"当前已选值"合并（unshift）进 options。
4. 修改场景：打开对话框时先把当前已选对象放入 options，再允许用户重新展开选择。
5. 数据量小的主数据可退化为本地过滤型：不启用 remote，只在 @visible-change 时一次性加载全量（大 pageSize），label 拼成"业务编码（名称）"这类带上下文的展示串、value 存真正要落库的字段，靠 filterable 走本地过滤；此时必填校验规则的 trigger 必须由 blur 改为 change，否则选中后红字提示不消失。
6. 跨模块加载选项要留意数据源接口的权限标识：新页面复用别的模块列表接口做下拉时，该接口的权限需一并授予使用账号，否则下拉静默加载失败。

## 注意事项
展开时不会自动调用 remoteMethod 的根因：element-ui select.vue 的 watch visible 会以空串调用 handleQueryChange，而其中 previousQuery===null 的初始守卫直接 return 吞掉调用（已在本地 node_modules/element-ui/packages/select/src/select.vue 源码核实）。非 remote 的 filterable 才走本地 filterMethod，行为不同。
