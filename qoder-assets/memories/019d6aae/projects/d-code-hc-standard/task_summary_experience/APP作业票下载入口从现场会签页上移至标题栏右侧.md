---
title: "APP作业票下载入口从现场会签页上移至标题栏右侧"
usage_scenario:
    - "在移动端标题栏增挂页面级操作按钮时"
    - "评审或迁移作业票功能入口落点时"
    - "模拟器装包截图验证APP改动时"
keywords:
    - "标题栏次要槽位"
    - "XHeadView双按钮间距"
    - "下载入口上移"
    - "页面级操作落点"
    - "MuMu装包截图取证"
source: "auto"
---

## Task description
- Core requirement: 用户质疑"为什么把下载作业票按钮放在 APP 现场会签页里，合理吗"，评审后选定方案 A —— 把整票下载入口从环节 Tab 上移到作业票详情页标题栏右侧。
- Task background: 此前实现 APP 下载作业票时，因现场会签页底部只剩一个按钮位而把「下载作业票」加在那里（fragment_job_slip_sign.xml 的 btn_download + FragmentJobSlipSign.downloadTicket/openPdf）。作业票详情页 ActivityHotWork 顶部是公共组件 XHeadView（title.xml），右侧只有单槽位 tv_right，被 setTopRightButton 的"开始作业/中断作业/继续作业"状态操作占用，导致页面级下载能力无处安放。

## Execution process
1. 交互评审定级（用 frontend-interaction-design skill）：判定原落点不合理，依据五条 —— ① 语义错位（该 Tab 职责是逐节点签字放行，下载整票与会签无关，P1）；② 服务端 ApiSpecialController 每次实时调 printWorkSpecialPdf 重新生成 PDF，签字前后下载内容不同且无提示（P1）；③ 整票能力挂在单一环节 Tab，发现性方向反了（P1）；④ 与「指派操作人」形成两个 40dp 全宽同色系 filled 按钮，低频辅助操作占了主操作权重（P2）；⑤ 关键证据：FragmentJobSlipSign 只对 btn 做 workStatus=="1" 隐藏判断，btn_download 无任何状态逻辑，说明它不属于该页（P2）。
2. title.xml：在 tv_right 左侧插入次要槽位 TextView tv_right_secondary（wrap_content、透明背景 @color/transparent、文字色 @color/blue、paddingHorizontal 6dp、layout_marginEnd 10dp、visibility=gone），靠样式而非尺寸区分主次。
3. XHeadView.java：新增 public void setOnSecondaryRight(String s, OnClickListener listener)（空串即隐藏）；新增 private void refreshRightSpacing() 动态维护右边距——tv_right 可见时取 10dp，只剩次要按钮时补 15dp（否则它成为最后一个可见子项会贴屏幕边）；setOnRight 末尾同步调用 refreshRightSpacing()；配 dp() 按 density 换算。
4. ActivityHotWork.java：downloadTicket()/openPdf() 从 Fragment 整体上移（mActivity.getJobSlipBean() → getJobSlipBean()），补齐 Uri/Environment/File/FileOutputStream/FileProvider/ResponseBody/Call/Callback/Response import；在 setRight() 的 if (getJobSlipBean() != null) 分支内先调 setTopDownloadButton()，保证新增票（bean 为 null）不显示下载。
5. 清理旧落点：fragment_job_slip_sign.xml 删除 btn_download；FragmentJobSlipSign.java 删除 btnDownload 绑定、downloadTicket()、openPdf() 及 7 个失效 import（java.io.File 因 upload(File) 仍在用故保留）。
6. 验证：gradlew compileDebugJavaWithJavac processDebugResources 与 assembleDebug 均 BUILD SUCCESSFUL；adb install -r 装到 MuMu 后逐项截图核验。

## Related files
- /other1/app/src/main/res/layout/title.xml
- /other1/app/src/main/java/com/sckj/other1/view/XHeadView.java
- /other1/app/src/main/java/com/sckj/other1/activity/ActivityHotWork.java
- /other1/app/src/main/res/layout/fragment_job_slip_sign.xml
- /other1/app/src/main/java/com/sckj/other1/fragment/FragmentJobSlipSign.java

## Notes
- 项目 res 下无任何下载图标（ic_home_dl 是配电箱设备图、ic_home_down 是下拉箭头，语义都不对），为避免引入新位图并保持标题栏一致性，改用与状态按钮同规格的文字按钮，靠底色/字色区分主次。
- PowerShell 里 `adb exec-out screencap -p > x.png` 会把对象格式化内容写进文件导致 PNG 无法识别（failed to detect image type）；必须改为 `adb shell screencap -p /sdcard/x.png` + `adb pull` 两步。
- adb 不在 PATH，绝对路径 D:\Tool\sdk\platform-tools\adb.exe（来自 other1/local.properties 的 sdk.dir=d:\\tool\\sdk）。am start 直启 ActivityHotWork 会因未 exported 报 SecurityException，只能走 UI 点击导航。
- 模拟器无 PDF 阅读器时下载走 catch 兜底 toast，界面看似无变化，需 ls /sdcard/other/ 加 logcat 确认 START VIEW application/pdf 已发出才算成功。
- XHeadView 是全应用公共头部组件，改动只新增默认 gone 的槽位，对 ActivitySpecialExamination 等其他页面无影响；后续要挂页面级次要操作可直接复用 setOnSecondaryRight。
- 遗留待用户决策：文案保留「下载」（PC 侧叫「导出」，跨端语义是否统一待定）；改动尚未提交。

## Task overview
已完成：下载入口从现场会签页迁至作业票详情页标题栏右侧次要槽位，两种状态（有/无状态按钮）与下载落盘均经 MuMu 真机实测通过，assembleDebug BUILD SUCCESSFUL 并已装机；旧按钮与相关逻辑清理干净，无残留引用。
