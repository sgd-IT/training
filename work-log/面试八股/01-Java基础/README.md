# 01-Java基础
> 层级：01｜建立：2026-10-30｜状态：空
## 【本类范围】
集合与底层结构、String 与不可变、泛型与类型擦除、异常体系、反射与注解、IO/NIO、equals/hashCode 契约、基础新特性（Stream、Optional、record）。
## 【高频考点】
- HashMap 扩容与红黑树转换阈值、并发场景为什么换 ConcurrentHashMap
- String 常量池、`+` 与 StringBuilder、intern 的坑
- 泛型擦除对重载的影响、List<? extends> 与 super 的读写边界
- 受检异常 vs 运行时异常、finally 里 return 的陷阱
- 反射的性能代价与替代（方法句柄、字节码增强）
- Stream 惰性求值与并行流误用
## 【与项目经历的关联】
- 业务里大量 VO/BO 转换与 Map 兜底取数，可挂 fastjson 静默丢字段的坑（03-踩坑手册）。
- 枚举 + 状态机建模（告警处置、隐患九态）可作“为什么用枚举不用常量”的举证。
- `equals/hashCode` 契约：Map 以业务单号为 key 去重时踩过的坑可复用。
## 【待补题目】
- [ ] HashMap 扩容机制与 JDK8 树化阈值
- [ ] 为什么 String 设计成不可变
- [ ] 泛型擦除与桥方法
- [ ] 异常体系与自定义业务异常的边界
- [ ] 反射 / 动态代理在项目里的实际用途
## 【题目记录】
新增按 `## <题面> · <YYYY-MM-DD>` 追加，字段用 `### 【答案】` `### 【追问】` `### 【关联】`。
