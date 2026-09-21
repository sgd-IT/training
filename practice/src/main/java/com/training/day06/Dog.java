package com.training.day06;

/**
 * Day 6 - Q1: Dog 继承抽象类 Animal（你来补全）
 *
 * 先读 Animal.java，注意 speak() 是 abstract 的——没有方法体。
 *
 * 你要做的事只有一件：
 *   把 speak() 里的 throw 换成：return "汪汪";
 *
 * ⚠️ 但真正要理解的是这件事——动手验证（2 分钟）：
 *   把下面整个 speak() 方法删掉，然后编译，编译器会说：
 *     Dog is not abstract and does not override abstract method speak() in Animal
 *   抄下这句报错，再把它加回来。
 *
 *   对比 Day 5：Shape.area() 返回 0 是"假实现"，忘了重写只是静默算错；
 *   Animal.speak() 是"真空缺"，忘了实现**编译就过不去**。
 *   这就是 abstract 存在的全部理由：把运行时的静默错误，提前到编译期。
 *
 * 规则：先在文件顶部写 3-5 行伪代码注释，再写实现。
 */
public class Dog extends Animal {

    public Dog(String name) {
        super(name); // 校验在 Animal 构造器里做（Day 5 的 super 校验链）
    }

    @Override
    public String speak() {
        // TODO: 改成 return "汪汪";
        return "汪汪";
    }
}
