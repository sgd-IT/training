---
title: "Spring AOP中@Async自调用不生效"
usage_scenario:
    - "排查Spring应用中异步方法未按预期执行问题"
    - "设计需要异步执行的业务逻辑时避免自调用陷阱"
keywords:
    - "@Async"
    - "Spring AOP"
    - "自调用"
    - "代理失效"
source: "auto"
---

Bug class: Spring AOP代理失效
Root cause: 在同一个类内部通过this调用标有@Async的方法时，Spring AOP代理无法拦截该调用，导致@Async注解完全无效，方法实际以同步方式执行
Fix pattern: 将自调用改为通过注入的代理对象调用（如@Autowired注入自身接口），或直接移除@Async并显式使用线程池执行
Reusable lesson: 不要在同一类中通过this调用标有@Async的方法，因为这会绕过Spring代理，导致异步失效；而应通过代理对象调用或改用显式线程池。适用于所有使用Spring AOP和@Async注解的场景；不适用于纯Java多线程（如new Thread()）或非Spring管理的Bean。
