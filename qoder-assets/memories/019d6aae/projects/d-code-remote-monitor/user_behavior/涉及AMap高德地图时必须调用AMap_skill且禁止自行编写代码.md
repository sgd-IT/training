---
title: "涉及AMap高德地图时必须调用AMap skill且禁止自行编写代码"
usage_scenario:
    - "代码中实际调用AMap JS API或新增高德点位与路线规划"
    - "需求文本明确出现高德或AMap字样时判定走skill还是自行实现"
    - "纯更名筛选删除死代码等无地图需求的任务本约束不生效"
keywords:
    - "AMap"
    - "高德地图"
    - "地图JS API"
    - "路线规划"
    - "百度地图BMapGL"
    - "AMap skill missing"
    - "precondition check"
    - "不触发边界"
source: "auto"
---

用户 sgd 的硬性约束：凡任务**确实涉及高德地图（AMap）能力**——例如地图初始化、点位标记、图层、路线规划、AMap JS API 2.0 用法等——必须先确认 AMap skill 是否已安装并加载（检查 C:\Users\Administrator\.qoder\skills\ 和 C:\Users\Administrator\.agents\skills\ 目录），仅当存在时才调用该 skill 获取指导并按其文档/示例实现；若未安装，则须向用户明确说明缺失，并协商是否迁移 skill 或改用其他方案（如百度地图 BMapGL）。严禁凭自身知识自行编写地图代码。执行前输出遵循【skills名称】标注规范。

不适用边界（重要，避免误召回）：任务全文未出现高德/AMap/地图相关需求时本约束完全不生效，不应据此盘点 skill 或向用户提问——例如页面更名、新增下拉筛选、删除 UI 区块与死代码清理等纯前端改动均与本条无关。本项目既有大屏地图使用的是百度地图 BMapGL，仅在用户明确要求 AMap 时才适用本条。
