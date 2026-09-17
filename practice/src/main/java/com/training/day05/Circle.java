package com.training.day05;

/**
 * Day 5 - Q1: Circle 继承 Shape（你来补全）
 * <p>
 * extends 已经给你写好了。你要加三样东西：
 * 1. private final double r（半径）字段
 * 2. 构造器里补校验：r <= 0 → IllegalArgumentException("半径必须为正数")
 * （注意：super("圆形") 必须是构造器第一行，不能挪到校验后面！）
 * 3. 重写 area()：返回 Math.PI * r * r，加上 @Override 注解
 * <p>
 * 规则：先写注释版伪代码，再写实现。
 */
public class Circle extends Shape {
    private final double r;

    public Circle(double r) {
        super("圆形");
        // TODO: 校验 r > 0
        if (r <= 0) {
            throw new IllegalArgumentException("半径必须为正数");
            // TODO: 赋值 this.r
        }
        this.r = r;
    }

    // TODO: @Override public double area() { return Math.PI * r * r; }
    @Override
    public double area() {
        return Math.PI * r * r;
    }

}
