package com.training.day05;

/**
 * Day 5 - Q2: Rectangle 继承 Shape（你来补全）
 *
 * 和 Circle 一样的套路：
 *   1. private final double w、h 字段
 *   2. 构造器补校验：w <= 0 或 h <= 0 → IllegalArgumentException("边长必须为正数")
 *      （super("矩形") 保持在第一行）
 *   3. 重写 area()：返回 w * h，加 @Override 注解
 *
 * 规则：先写注释版伪代码，再写实现。
 */
public class Rectangle extends Shape {

    private final double w;
    private final double h;
    public Rectangle(double w, double h) {
        super("矩形");
        // TODO: 校验 w > 0 且 h > 0
        if(w <= 0 || h <= 0){
            throw new IllegalArgumentException("边长必须为正数");
        }
        // TODO: 赋值 this.w、this.h
        this.w = w;
        this.h = h;
    }

    // TODO: private final double w、h 字段声明

    // TODO: @Override public double area() { return w * h; }
    @Override
    public double area() {
        return w * h;
    }
}
