---
title: "厂区作业地址主数据落点：base_area区域树而非地址字典或地址表"
usage_scenario:
    - "改造报备或作业票的作业地址输入控件"
    - "查找厂区区域或地址数据来源"
    - "核对历史报备地址与区域表匹配度"
keywords:
    - "base_area工厂区域树"
    - "b_area_code字典"
    - "作业地址无主数据表"
    - "base_ticket_bb仅address文本列"
    - "getAreaTree区域选择弹窗"
source: "auto"
---

厂区"作业地址/作业地点"没有专门的地址主数据表，也没有地址类字典（sys_dict_type 全量核查无任何 address/地点字典；不存在表名含 addr/place/site 的业务表）。可作为地址来源的已验证实体有三处：

1. base_area（工厂区域）—— 唯一完整的地址主数据，树形结构（area_id / parent_id / ancestors / area_code / area_name / grade 风险等级 / area_beacon 区域信标 / del_flag），层级为车间 → 产线或罐区 → 楼层或具体点位（如 邻醛车间→邻醛一线→1楼、对酚车间→包装房/应急池1-3/对酚车间大门）。同名子区域在不同父级下大量重复（多个"1楼"、"2#"），因此只存 area_name 无法定位，需带父级路径。该表存在有效重复行（如"对酚车间大门1"有 area_id 687 与 688 两条均 del_flag='0'），且部分名称带随手编号色彩。后端能力已齐备：BaseAreaController(/base/area/list，需 base:area:list 权限)、BaseAreaServiceImpl.buildAreaTree/treeSelect、PC 端 src/api/base/area.js(listArea/listAreaExcludeChild)、APP 免登接口 ApiBaseController.getAreaTree。

2. b_area_code 字典 —— 仅 11 条一级区域编码（mx11~mx21：邻醛一线、邻醛二线、邻酸水处理、对酚车间、对酚水处理、仓库、锅炉房、液氯气化车间、污水站、氯化锌车间、再生车间），粒度只到车间级；PC 端仅在 base/area 区域管理页使用，APP 端零引用。

3. sys_area —— 省/市/镇三级行政区划表（province_code/city_code/area_type），与厂区内部作业位置无关，不可用作作业地址选项源。

时序/口径约束：base_ticket_bb（作业报备）只有 address 一个 varchar 文本列，没有 areaId/areaName 列，选中区域后仍须以文本形式上报园区，且园区校验 PlatementValidator 限制 address 长度 ≤100 字符，拼父级路径时需控长。与之相反，base_work_special（特殊作业票）同时具备 workAddr（作业地点文本）+ area_id + area_name 三列，即"区域选择"与"地点文本"在该实体中是并存的两个字段，APP 端 8 个特殊作业票 fragment 已通过 getAreaTree("0") + Dialog_Unit 弹窗做区域必选（提交前校验 areaId == -1 则提示"请选择作业区域"），而报备页 ActivityWorkReport 的 editAddress 仍是纯 EditText 手输（编辑态被 setEnabled(false) 禁用）。

现状结论：报备历史 address 为自由文本口语化写法，与区域表命名普遍"方向对得上、字对不上"（"对酚车间大门旁"↔区域表"对酚车间大门1"、"邻醛包装房"↔"包装房"、"污水站污水平台"↔"污水预处理平台"），并夹杂测试脏数据（"测试"、"阿菊"、"525"、"双十一"等），故历史值 distinct 结果不宜直接作为下拉选项源。
