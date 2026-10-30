---
title: "解决PlatementBaseApi多子类@Component注入歧义"
usage_scenario:
    - "Spring项目中基类与多个子类均被@Component标注，导致@Autowired基类类型时出现Bean数量冲突"
    - "排查类似'Field xxx required a single bean, but N were found'错误时的诊断与修复路径"
    - "在ruoyi-common模块platement包下处理API层继承体系的Bean注入问题"
keywords:
    - "@Qualifier"
    - "PlatementBaseApi"
    - "Bean注入歧义"
source: "auto"
---

## 任务描述
解决Spring启动失败问题：`PlatementOndutyApi`中`@Autowired PlatementBaseApi baseApi`因存在15个同类型Bean（基类自身+14个子类）导致注入歧义。

## 执行过程
```mermaid
graph TD
    A[启动报错：15个PlatementBaseApi Bean] --> B[Read PlatementOndutyApi.java定位注入点]
    B --> C[Grep确认14个子类均extends PlatementBaseApi]
    C --> D[Grep确认所有子类及基类均含@Component]
    D --> E[确定唯一注入点：仅PlatementOndutyApi.java存在此注入]
    E --> F[添加@Qualifier\("platementBaseApi"\)指定基类Bean]
    F --> G[执行mvn clean compile验证修复]
```

## 任务总结
通过`@Qualifier("platementBaseApi")`精准指定注入`PlatementBaseApi`基类的默认Bean名称（首字母小写驼峰），成功解决多继承@Component Bean注入歧义问题；项目中仅此一处直接注入基类，修复后编译通过。
