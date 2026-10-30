---
title: "合同保存报产品不存在的双表分立根因与 git 溯源方法"
usage_scenario:
    - "合同保存报产品不存在排查"
    - "产品主档归属与同步方案决策"
    - "用 git 溯源判断改造责任归属"
    - "ERP 与 CRM 跨模块数据一致性排障"
keywords:
    - "PRODUCT_NOT_EXISTS 产品不存在"
    - "erp_product crm_product 双表"
    - "save-with-plans 保存链路"
    - "git log -S 词频溯源"
    - "产品主档统一方案"
source: "auto"
---

## Bug 类
合同保存（走 /crm/sales-contract/save-with-plans）携带 productId 时报 PRODUCT_NOT_EXISTS「产品不存在」，但产品在 ERP 产品管理里明明已建档。

## 根因
anwei-inner 并存两套产品主档且零同步：erp_product（ERP 系统→产品管理→产品信息）与 crm_product（CRM 系统→系统配置→产品管理）。CRM 合同表单产品下拉（@/api/crm/product → /crm/product/simple-list）与后端校验（CrmContractServiceImpl.validateContractProducts → CrmProductServiceImpl.validProductList）都只认 crm_product，查不到即抛错；ERP 产品建档只写 erp_product，两表无任何同步代码或同步 SQL。git 溯源：erp 产品创建接口与 crm_product 校验同属初始化提交 4c67364（yanglu 2026-08-13，yudao 原生代码），git log -S "validateContractProducts" 全仓仅命中 4c67364，非后续改造引入；9555178/2408ec3（yujiale 2026-09-08）只做 ERP 侧产品改造（erp_product 加 code/module_id、建 erp_product_module、回填历史产品），未动 CRM；7c2a6a0（yujiale 2026-08-28）新增 save-with-plans 入口复用原生校验，是问题暴露点。

## 修复模式
短期在 CRM 产品管理菜单建档（写 crm_product）；中期二选一统一主档：改合同表单+校验走 ErpProductService（推荐，ERP 侧产品被采购/库存引用深不可弃）或做 erp→crm 单向同步。CollectionUtils.convertSet 只过滤 null 不过滤 0，productId=0 的历史明细同样触发报错，需一并过滤。

## 可复用教训
排查"该由哪个模块/哪次改造负责"时，先 git log -S <关键符号> --all 看词频变化的提交（一次命中初始化提交=原生设计，避免误判成某次改造失误），再查 sql 目录确认建表/数据脚本是否在 git 内（erp_product 建表与数据均不在仓库，属运行时/库外产物）。
