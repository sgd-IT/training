---
title: "APP作业票详情页五页签结构与Fragment挂载映射"
usage_scenario:
    - "确认某功能在APP端有无查看入口"
    - "改造作业票详情页某页签字段或按钮"
    - "排查按文案grep APP布局文件零命中"
keywords:
    - "ActivityHotWork五页签"
    - "FragmentJobSlipGas取样分析"
    - "tvWorkGuardian监护人"
    - "DialogWorkPeopleMultiple"
    - "isEditable状态白名单"
    - "APP票证详情入口"
source: "auto"
---

移动端 APP（other1 项目）查看票证详情的唯一入口是作业票列表点击进入 ActivityHotWork，该页为 5 页签结构，各页签固定挂载如下 Fragment：

- tv1 基本信息 → mBaseFragment（即各票型建票/编辑表单 Fragment，如 FragmentHotWork）；监护人字段 tvWorkGuardian 的多选弹窗 DialogWorkPeopleMultiple 就在这一页，页面只显示逗号分隔的姓名串，**不含内部/外施队来源标识**。
- tv2 取样分析 → FragmentJobSlipGas（气体检测历史列表 + 添加/修改/作废，编辑权限由 isEditable() 按 workStatus 白名单控制）。
- tv3 安全措施 → FragmentJobSlipMeasure。
- tv4 现场会签 → FragmentJobSlipSign（含实际开工时间展示、下载入口）。
- tv5 监控 → FragmentJobSlipMonitoring（视频/监控，纯动态渲染，布局 fragment_job_slip_monitoring.xml 内无任何硬编码 android:text 标签，故按文案 grep 该页字段必然零命中，须改 grep Java 侧 setText 或对应 bean 字段）。

改造启示：判断"某功能在 APP 是否有入口"应以 ActivityHotWork 的页签绑定与 setRight()/setTopRightButton 的状态分支为准，不能只按业务名词全局 grep——气体检测、监护人、中断恢复等能力的入口都收敛在这一个 Activity 的五个页签里。jhr 为园区平台必填的纯姓名串，任何"监护人来源标识"类展示需求只能靠姓名反查 base_contractor.company_type 实现，不可改动 jhr 存储结构。
