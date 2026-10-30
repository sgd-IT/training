---
title: "CRM 菜单表驱动路由与 erp/crm 双产品主档结构约束"
usage_scenario:
    - "判断当前生效的是哪个 CRM 页面或菜单 component"
    - "排查 ERP 产品在 CRM 合同中选不到或保存报产品不存在"
    - "修改 CRM 列表页或详情页路由与菜单高亮"
keywords:
    - "system_menu component"
    - "routerHelper 动态路由"
    - "erp_product crm_product 双表"
    - "PRODUCT_NOT_EXISTS"
    - "crm_project_quote 状态驱动按钮"
source: "auto"
---

anwei-inner-office-vue 的 CRM 列表页没有静态路由注册：src\utils\routerHelper.ts 用 import.meta.glob 扫描 views 下的 vue/tsx，配合数据库 system_menu 表的 path / component / component_name 字段动态生成路由，因此在源码里 grep 某个列表页文件名（如 SalesClueListPage）会返回 0 引用，判断"当前生效的是哪个页面"必须查 system_menu.component。详情页相反，注册在 src\router\modules\remaining.ts 的静态路由中，并用 meta.activeMenu 指定所属列表菜单路径以驱动左侧菜单高亮；activeMenu 的值必须等于菜单链拼出的完整路径（顶级菜单 path + 子菜单 path），否则进详情后高亮丢失。同一页面的多版本共存靠升级脚本改写 component 完成切换，旧页面文件仍留在仓库中。

CRM 销售闭环的另一类结构约束是「前端按钮可见性由数据库状态字段直接驱动」：报价列表 src\views\crm\quote\QuotePage.vue 的操作列按钮用 v-if="frozen(s.row)&&!expired(s.row)" 控制「新建合同」，frozen 判定 status ∈ ['已冻结','已完成']、expired 判定 validUntil 非空且早于当天 —— crm_project_quote.status 是 varchar 直接存中文状态文本（草稿/已冻结/已被替代/已作废），不是数字枚举，因此要让某按钮出现只需改这两列。业务编号同样是库侧规则：报价编号 BJ + yyyyMMdd + 6 位补零 id、项目编号 PJ + 同格式，由后端 Controller 插入后回写，故按 AUTO_INCREMENT 值可反推应有编号。

产品主档存在两套零同步的表：erp_product（ERP 系统→产品管理→产品信息，component=erp/product/product/index）与 crm_product（CRM 系统→系统配置→产品管理，component=crm/product/index）。CRM 的报价/合同表单产品下拉与合同保存校验其实都走 CRM 产品（前端 import 自 @/api/crm/product → /crm/product/simple-list），保存链路为 CrmContractServiceImpl.validateContractProducts → CrmProductServiceImpl.validProductList → 查 crm_product，查不到抛 PRODUCT_NOT_EXISTS「产品不存在」（文案不在前端源码内）。erp 模块无写 crm_product 代码、crm 模块无引用 erp_product 代码，故在 ERP 产品管理建档的产品在 CRM 合同里永远选不到、保存必失败。CollectionUtils.convertSet 只过滤 null 不过滤 0，productId=0 的历史明细也会触发该报错。

crm_product 的字段落库语义（2026-10-12 逐条实测确认）：编码列名是 no（varchar，P + 8 位补零序号，随 id 递增，如 id=3→P00000003、id=4→P00000004），erp_product 才叫 code，两表列名不可混用；unit 是 int 存字典 crm_product_unit 的值（套=13、个=1、台=8、箱=12），status 是 int 存字典 crm_product_status（1=上架、0=下架），category_id 是外键指向 crm_product_category.id（id=3=其他），owner_user_id 指向 system_users.id（159=huyixuan/胡艺璇）。这些列在库里一律存数字或 id 而非中文标签，页面靠字典翻译回显，核对时不能把 int 值当作脏数据或写错表的证据。
