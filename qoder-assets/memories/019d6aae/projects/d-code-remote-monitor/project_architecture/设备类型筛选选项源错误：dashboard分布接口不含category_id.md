---
title: "设备类型筛选选项源错误：dashboard分布接口不含category_id"
usage_scenario:
    - "为消防值守页面新增设备类型筛选"
    - "排查某筛选条件选了却没生效"
    - "区分分布统计接口与字典接口的用途"
keywords:
    - "device-category无category_id"
    - "设备类型筛选死参数"
    - "eiot_product.category_id"
    - "CategoryController权限"
    - "图表接口误当字典"
source: "auto"
---

消防值守前端的「设备类型」下拉在 alarm-record/index.vue（告警记录/报警统计页）是死参数：它调 FireDashboardServiceImpl 的 /eiot/firewatch/dashboard/device-category 取选项，但该接口返回的是 DashboardStatisticsRespVO.DeviceCategoryVO（只有 categoryName + count，供图表用），不含 category_id；前端映射写的是 item.categoryCode || item.productKey || item.id，三个字段在该响应里都不存在，于是 el-option 的 value 恒为 undefined，选中后查询参数等于没传。

后端 /eiot/firewatch/alarm-order/page 与告警记录分页的 deviceType 入参是按 eiot_product.category_id 过滤（需经 device → product → category 关联），因此合法的选项值必须来自分类字典 /eiot/category/list（CategoryController，返回含 id 的分类对象，id 即 category_id），而不是 dashboard 的分布统计接口。

约束与边界：分类字典接口带 @PreAuthorize("@ss.hasPermiss...iot:category:query")，值班员类角色可能无权导致下拉为空；给消防值守页面加设备类型筛选时必须先确认所选数据源真的带出 category_id，并核对该角色是否有字典查询权限。本条仅适用于 firewatch 系列页面的设备类型/分类筛选，不适用于纯展示用的类型分布图表（那种场景用 device-category 是正确的）。
