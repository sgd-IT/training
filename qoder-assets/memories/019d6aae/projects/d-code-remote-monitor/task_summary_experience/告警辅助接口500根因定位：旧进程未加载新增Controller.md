---
title: "告警辅助接口500根因定位：旧进程未加载新增Controller"
usage_scenario:
    - "排查新写接口报500或路由不存在时"
    - "继续alarm-assist联调与提交这批新代码时"
    - "核对日志总结结论可信度时"
keywords:
    - "alarm-assist接口500"
    - "NoResourceFoundException"
    - "IDEA旧进程"
    - "进程启动时间比对class时间戳"
    - "firewatch新增Controller未提交"
source: "auto"
---

## Task description
- Core requirement: 用户贴出一份 Spring Boot（enjoyiot iot-server，PID 46864，Tomcat 48080）运行日志的详细总结，其中 GET /admin-api/eiot/firewatch/alarm-assist/get?alarmId=31596 返回 500，异常 org.springframework.web.servlet.resource.NoResourceFoundException（文案 "No static resource admin-api/eiot/firewatch/alarm-assist/get"），要求定位并解决。
- Task background: 该日志总结给出的建议是"检查 Controller 是否缺 @GetMapping / 包扫描路径 / 接口未实现"；当前打开文件为 AlarmAssistPanel.vue，属于大屏告警辅助判断功能，后端 alarm-assist 接口是新近开发的代码。

## Execution process
1. grep 全仓 alarm-assist / AlarmAssist：确认前后端两侧都存在对应文件（前端 src/api/eiot/firewatch/alarmAssist.api.ts，后端 FireWatchAlarmAssistController.java + FireWatchAlarmAssistService/Impl + vo/assist/AlarmAssistRespVO），排除"接口未实现"。
2. 读 Controller 与前端 api 源码：@RestController + @RequestMapping("/eiot/firewatch/alarm-assist") + @GetMapping("/get")，与前端 /eiot/firewatch/alarm-assist/get 加框架 /admin-api 前缀完全一致，排除注解缺失与路径不匹配。
3. 列 module-eiot-biz/target/classes/.../controller/admin/firewatch 目录：FireWatchAlarmAssistController.class 实际存在，LastWriteTime = 2026-10-14 10:44:03（此前 Glob 0 匹配是模式问题，PowerShell 直列目录为准）。
4. 查进程与 jar：Get-Process -Id 46864 得 StartTime = 2026-10-13 17:11:50，命令行带 idea_rt.jar javaagent（IntelliJ 启动 ServerApplication，直跑 target/classes）；server/target 无新构建 jar。
5. 查 git 与热部署：git log --follow 对该 Controller 无输出、git status 显示其为 ?? untracked（全新未提交，连同 FireSpaceController、vo/assist、vo/space）；grep devtools 在 server 模块 0 匹配。
6. 定论并回复：报错时间 11:49 > class 编译 10:44 > 进程启动 10/13 17:11，运行中的旧 JVM 未加载新增 Controller，Spring 容器无该路由映射，请求落到 ResourceHttpRequestHandler 抛 NoResourceFoundException 并被全局异常处理器包成 500；修复只需重启 ServerApplication（建议先 Build Project），无需改任何代码；同时提醒日志总结的问题 1 建议会把排查带偏，以及新代码尚未分批提交。

## Related files
- /anwei-fire-remote-monitor/module-eiot/module-eiot-biz/src/main/java/com/enjoyiot/module/eiot/controller/admin/firewatch/FireWatchAlarmAssistController.java
- /anwei-fire-remote-monitor-vue/src/api/eiot/firewatch/alarmAssist.api.ts

## Notes
- 外部生成（或人工撰写）的日志总结即使格式详尽，其"根因分析/建议"仍可能是误诊；应以仓库实测证据独立复核，不要顺着它的方向去查注解和包扫描。
- 用 Glob 匹配 target/classes 里的 class 可能因模式写法返回 0，判断产物是否存在要用 PowerShell 直接列目录。
- 本轮未做任何代码修改，也未重启应用（属用户侧 IDEA 操作），修复动作尚待用户执行验证。

## Task overview
定位完成（未落码）：alarm-assist/get 的 500 根因确认为"IDEA 旧进程早于新增 Controller 的编译时间且无 devtools，路由未注册"，源码与路径拼接均正确，唯一修复动作是重启 ServerApplication；已明确纠正日志总结中的误诊结论，并提示 firewatch 相关新文件仍未提交 Git。
