package com.training.day06;

/**
 * Day 6 - 抽象类教科书（已实现——仔细读！）
 *
 * 和 Day 5 的 Shape 对比着看，只有一处关键差别：
 *
 *   Day 5:   public double area() { return 0; }    ← 有方法体，"假实现"
 *   Day 6:   public abstract String speak();       ← 没有方法体，以分号结尾！
 *
 * 为什么 Day 5 那个是雷？
 *   忘了重写的子类不会报错，area() 静默返回 0 —— 算错了还不知道。
 *   abstract 把这件事变成编译错误：非抽象子类不实现它，编译就通不过。
 *
 * 三条铁律（今天必须记住）：
 *   1. abstract 方法没有方法体，以分号结尾
 *   2. 有 abstract 方法的类，自己必须是 abstract class
 *   3. abstract class 不能 new（编译错误）
 *
 * 注意 describe()：它调用 speak()——和 Day 5 的 Shape.describe() 一模一样的多态套路。
 */
public abstract class Animal {

    protected final String name;

    public Animal(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("名字不能为空");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /** 抽象方法：没有方法体。谁继承谁必须实现它 */
    public abstract String speak();

    /** 具体方法：内部调用抽象方法，实际执行子类的实现（多态） */
    public String describe() {
        return name + " says " + speak();
    }
}
