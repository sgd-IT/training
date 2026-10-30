---
title: "前端仓库保存触发 Prettier 整体重排导致 diff 放大"
usage_scenario:
    - "查看前端改动 diff 判断实际修改范围"
    - "前端改动分批提交前筛除格式变更"
    - "怀疑大段改动是人为改写时核实"
keywords:
    - "Prettier保存格式化"
    - "diff格式噪声"
    - "anwei-fire-remote-monitor-vue"
    - "功能行与格式行分离"
source: "auto"
---

前端仓库 anwei-fire-remote-monitor-vue 的编辑器在保存时会对被改动文件做 Prettier 风格整体重排（模板属性逐行换行、去多余分号与尾逗号、import 花括号内补空格、清理行尾空格），实测一次小改动会在 diff 中放大成数十行格式变更。该重排不改变任何逻辑，功能零影响，且项目本身带有 prettier 配置，属合法行为。查看改动或分批提交时应把"功能行"与"格式行"分开识别，避免被格式噪声误导为大规模改写；公共组件（yt-crud.vue、yt-table-fun.vue）尤其明显，因为整份模板都会被重排。
