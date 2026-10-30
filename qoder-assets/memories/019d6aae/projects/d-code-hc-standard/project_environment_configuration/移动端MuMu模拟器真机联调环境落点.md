---
title: "移动端MuMu模拟器真机联调环境落点"
usage_scenario:
    - "在安卓模拟器上驱动移动端APP做UI验证并截图取证"
    - "排查adb命令找不到或终端截图损坏无法识别"
    - "定位APP下载的作业票文件路径与装包回归验证"
keywords:
    - "MuMu模拟器127.0.0.1:7555"
    - "adb落点D盘Tool sdk"
    - "HttpInterface.IP本机局域网地址"
    - "28080需监听才可联调"
    - "sdcard other下载目录"
    - "app-debug.apk装机验证"
    - "devtools热重启28080"
source: "auto"
---

hc-standard 移动端（other1，包名 com.sckj.other1）在 MuMu 模拟器上做真机联调的确定环境：① adb.exe 不在预设 SDK 路径列表内，实际落点 D:\Tool\sdk\platform-tools\adb.exe；定位方式是查 5037 端口（adb server 端口）的 OwningProcess，再用 Win3_Process 取 ExecutablePath；也可从 other1/local.properties 的 sdk.dir 直接推导 platform-tools 路径。调用必须用绝对路径加 & 前缀（PowerShell）。② 设备序列号为 127.0.0.1:7555（MuMu 模拟器，机型标识呈 V2203A），分辨率 1080x1920、density 480，screencap 出图即原始尺寸，adb shell input tap 可直接用截图像素坐标。③ APP 的 HttpInterface.IP 硬编码为 http://192.168.250.130:28080/，该地址正是本机局域网 IP，模拟器走桥接网络可直连宿主后端（验证方式：adb shell ping 通 + curl /captchaImage 返回 200），因此不需要改成 10.0.2.2 或重打包即可联调；一旦本机 IP 变化，APP 会同时失去后端连接与正确签字分支（见"移动端签字方式由 HttpInterface.IP 字符串匹配决定"）。④ APP 下载的作业票 PDF 落在 /sdcard/other/<作业编号>.pdf，不在 /sdcard/Download，也不在 Android/data 私有目录。⑤ 后端由 IDEA 以 devtools 启动（监听 28080），改完 Java 代码通常由 IDEA 自动编译生效，命令行 mvn -pl ruoyi-admin compile 因缺 -am 会报 ruoyi-generator:jar 找不到但不代表改动失败。⑥ UI 改动验证流程：先 gradlew assembleDebug 出 app/build/outputs/apk/debug/app-debug.apk，再 adb install -r 覆盖安装（只跑 compileDebugJavaWithJavac 不会刷新 APK，装上去可能是旧包），然后 adb shell am start -n com.sckj.other1/com.sckj.other1.activity.Activity_Splash 启动（MainActivity 与业务 Activity 均未 exported，直启会报 SecurityException，只能从启动页走 UI 点击导航）。⑦ 截图必须两步走：adb shell screencap -p /sdcard/x.png 后 adb pull 到本地；用 PowerShell 的 exec-out screencap -p > x.png 重定向会把对象格式化内容写入文件，Read 时报 failed to detect image type。⑧ 模拟器无 PDF 阅读器，APP 下载成功也只会走兜底 toast 且瞬时消失，界面看似无反应；须用 adb shell ls -l /sdcard/other/ 看文件落盘 + logcat 找 START VIEW application/pdf 才能判定下载成功，不能据"没弹出阅读器"误判接口失败。

## Notes
- 第③条"不需要改址即可联调"有前置条件：本机后端必须在 28080 处于监听状态。三端地址核查一轮实测 Get-NetTCPConnection -LocalPort 28080 无监听、访问 http://192.168.250.130:28080/captchaImage 直接连不上，此时 APP 虽指向本机也完全不通。排查顺序应为：先确认后端进程存活并监听 28080，再怀疑地址配置；不要一见 APP 弹"服务异常"就断定是 IP 配错而贸然改址重打包。
