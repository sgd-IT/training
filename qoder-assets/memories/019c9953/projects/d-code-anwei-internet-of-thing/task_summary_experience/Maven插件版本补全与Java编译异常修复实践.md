---
title: "Maven插件版本补全与Java编译异常修复实践"
usage_scenario:
    - "修复Maven构建警告时参考插件版本规范"
    - "处理Java编译异常InterruptedException时复用异常处理模板"
keywords:
    - "Maven"
    - "surefire-plugin"
    - "version"
    - "InterruptedException"
    - "编译错误"
---

## Task description
- Core requirement: 修复Maven构建警告和Java编译错误
- Task background: pom.xml中maven-surefire-plugin缺少version标签；MonitorCenterController.java中调用throws InterruptedException的方法未捕获或声明该异常，导致编译失败

## Execution process
1. pom.xml：为maven-surefire-plugin添加<version>2.22.2</version>标签以消除构建警告
2. MonitorCenterController.java：在alarmPopUpWindow方法中添加try-catch块捕获InterruptedException，并在catch中恢复线程中断标志、记录错误日志并返回失败响应
3. 验证：修改后重新编译通过

## Related files
- d:\code\anwei-internet-of-thing\pom.xml
- d:\code\anwei-internet-of-thing\monkey-monitor-api\src\main\java\com\monkey\general\modules\mon\controller\MonitorCenterController.java

## Notes
- None

## Task overview
成功修复了Maven构建警告和Java编译异常：为pom.xml中的maven-surefire-plugin补充了version标签；在MonitorCenterController.java中正确处理了InterruptedException，确保编译通过。整个过程严格遵循了用户确认流程。
