---
title: "APP版本更新链路的版本号格式与强制更新约束"
usage_scenario:
    - "在PC端登记新版本供APP检测更新"
    - "排查APP不弹升级提示的原因"
    - "决定新版本是否强制更新"
keywords:
    - "base_version_update"
    - "versionNo纯数字"
    - "is_force默认强制"
    - "getBaseVersionUpdate取最新"
    - "APP升级弹窗"
source: "auto"
---

hc-standard 的 APP 版本更新链路（2026-09-28 实测确认）由三部分构成，改动任一环节须同时满足以下不变量：

1. 数据侧：表 base_version_update 的 version_no 必须是**纯整数字符串**（与 APK 内 versionCode 对应）。APP 端 MainActivity 用 `Integer.parseInt(bean.getVersionNo().trim())` 后与本地 versionCode 做 `<` 比较触发升级弹窗——填成带点格式（如 "1.1.89"）会抛 NumberFormatException 被静默吞掉，导致 APP **永远检测不到更新且不报错**。库中 2026 年 6~8 月的历史行（id 83/85/86 等）version_no 即为错误的带点格式，属存量缺陷。
2. 取数侧：BaseVersionUpdateMapper.xml 的 getBaseVersionUpdate 为 `order by create_time desc LIMIT 1`，即 APP 只认**创建时间最新的一条**；新增记录立即生效，无需修改或删除历史错误行。
3. 表单侧：PC 页面 ruoyi-ui/src/views/base/versionUpdate/index.vue 提交 versionNo / versionName / updateReason / downloadUrl 四字段（downloadUrl 由上传 apk 自动生成，形如 http://host:18080/profile/upload/日期/xxx.apk），**表单不含 is_force**，而该列 DB 默认值为 1（强制更新）→ 通过界面新增的版本一律是强制更新（弹窗不可跳过、按返回键退出 APP）。切正式环境这类"旧包指向已失效"的版本应保持强制；若某次不希望强制，需在保存后单独把该行 is_force 改为 0。

适配边界：适用于登记/排查 APP 检测不到更新的问题；不适用于 APP 内其他业务接口的版本参数（如签名记录的 signVersion）。
