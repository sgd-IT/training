---
title: "移动端下载PC受权限保护文件采用新开免菜单权限接口"
usage_scenario:
    - "移动端调用PC导出打印接口返回403时"
    - "设计APP与PC共用的文件下载能力时"
    - "排查APP下载文件在手机上找不到时"
keywords:
    - "APP专用下载接口"
    - "菜单权限403"
    - "ApiSpecialController"
    - "PreAuthorize放宽取舍"
    - "FileProvider落点核验"
source: "auto"
---

Conclusion: 移动端需要下载 PC 端已具备的文件（如作业票 PDF）时，不在原 PC 接口上放宽权限，而是在 APP 专用控制器（ApiSpecialController，路径 /api/special/**）新开一个只要求登录、不带 @PreAuthorize 菜单权限的下载接口，服务端复用同一套 PDF 生成逻辑后从 profile/print 目录读文件流输出。

Rationale (trade-off): RuoYi 的 @PreAuthorize("@ss.hasPermi('base:workSpecial:list')") 按 sys_role_menu 判定，APP 账号通常未授予 PC 菜单权限，直调 PC 接口必 403；给 APP 角色补 PC 菜单权限会污染 PC 端可见菜单与按钮，放宽 PC 接口注解则削弱后台管控。新开接口的代价是两处 Controller 有少量重复的输出代码，但权限边界清晰且不动既有 PC 链路。

Rejected alternatives: 1) APP 直接用系统浏览器打开 PC 导出 URL——被否，因该接口需 Authorization: Bearer token，浏览器无法携带；2) 给 APP 账号授予 base:workSpecial:list/export 菜单权限后复用 PC 接口——可行但会把 PC 台账菜单暴露给移动端账号；3) 去掉 PC 接口权限校验——影响面大，属不合理选项。

Applicable / expiry conditions: 适用于本仓库 other1 移动端需要访问带菜单权限注解的 PC 能力（导出、打印、文件下载类）的场景；若后续为移动端建立独立权限体系或统一授权策略使 APP 账号可持有 PC 功能权限，应回退为复用原接口而不再新增免权限入口。新增此类接口必须保持仅要求登录、不接受匿名访问。

Notes on where the downloaded file lands: 该接口返回的是文件流，APP 侧拿到后由客户端自行决定落地位置——本仓库实测落在 /sdcard/other/<作业编号>.pdf（非 /sdcard/Download、非私有目录），经 FileProvider（authority com.sckj.other1.fileprovider，file_path.xml 的 external-path path="other"，URI 段呈 /voice/）发出 VIEW intent；模拟器无 PDF 阅读器时走 catch 兜底提示保存路径，此时界面看似无反应属正常，需用 ls /sdcard/other 与 logcat 确认下载已成功落盘，不能据"没弹阅读器"误判接口失败。
