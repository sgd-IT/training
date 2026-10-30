---
title: "detail.vue中el-tag type映射函数返回类型缩窄修法"
usage_scenario:
    - "修复设备详情页el-tag类型校验报错"
    - "编写返回标签类型的映射函数时收窄返回类型"
keywords:
    - "getLevelType返回类型"
    - "TS2322"
    - "联合类型缩窄"
    - "detail.vue模板"
    - "disabled无值写法"
source: "auto"
---

设备详情页 src/views/eiot/deviceinfo/detail.vue 中 el-tag `:type` 绑定的具体修复写法：返回宽泛 string 的映射函数（如 getLevelType）会触发 vue-tsc TS2322，应把函数返回类型显式缩窄为 'success' | 'info' | 'warning' | 'danger' 联合并去掉 '' 映射项（undefined 与原 '' 的运行期回退默认行为一致）。同理布尔属性用无值写法 `disabled` 而非字符串 `disabled="true"`，数值型 prop 必须绑定 `:rows="4"` 而非 `rows="4"`。
