package com.training.day05;

/**
 * Day 5 - 继承练习 · 基类 Shape（已实现——仔细读！这是今天的"教科书"）
 *
 * 你要注意三个设计点：
 *   1. protected 字段：子类可以直接访问（private 不行，只能通过方法）
 *   2. 构造器校验：和 Employee/Student 一样的套路——先校验后赋值
 *   3. area() 返回 0：基类不知道具体形状，等子类重写（override）
 *   4. describe() 里调用了 area()——注意！实际执行的是「子类重写后的版本」
 *      （这就是多态：方法调用跟着对象的真实类型走）
 */
public class Shape {

    protected final String name;

    public Shape(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("形状名不能为空");
        }
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /** 面积：基类默认 0，子类重写 */
    public double area() {
        return 0;
    }

    /** 描述自己：这里调 area()，实际执行子类的重写版本（多态！） */
    public String describe() {
        return name + " area=" + area();
    }
}
