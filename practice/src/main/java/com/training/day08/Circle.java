package com.training.day08;

import java.util.Objects;

/**
 * Day 8 - Q2: Circle —— 继承 Shape
 * <p>
 * 半径用 int，这样 equals 里可以直接用 == 比（避开 double 比较的坑）。
 * <p>
 * TODO Q2-2: area()      → Math.PI * radius * radius
 * TODO Q2-3: perimeter() → 2 * Math.PI * radius
 * TODO Q2-4: equals(Object o) —— Day 7 的四步，一步都不能少
 * TODO Q2-5: hashCode()
 * <p>
 * ⚠️ equals 第③步（类型检查）必须用  getClass() != o.getClass()
 * 想一想：如果写成  o instanceof Circle，会发生什么？
 * （面试高频题，检查时我会追问）
 * <p>
 * ⚠️ 先预测，再跑 —— 动手前把这两行写进注释：
 * // 预测：new Circle(2).equals(new Circle(2))      → true
 * // 预测：new Circle(2).equals(new Rectangle(2,2)) → false
 */
public class Circle extends Shape {

    private final int radius;

    public Circle(int radius) {
        super("圆");
        if (radius <= 0) {
            throw new IllegalArgumentException("radius 必须 > 0，实际: " + radius);
        }
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }


    @Override
    public double area() {
        //圆面积公式
        return Math.PI * radius * radius;
    }

    @Override
    public double perimeter() {
        //圆周长公式
        return 2 * Math.PI * radius;
    }


    @Override
    public boolean equals(Object o) {
        //判断是否一个对象
        if (this == o) return true;
        //判断请求参数是否为空，以及类型是否一致
        if (o == null || getClass() != o.getClass()) return false;
        //类型转换
        Circle circle = (Circle) o;
        return this.radius == circle.radius;
    }

    @Override
    public int hashCode() {
        return Objects.hash(radius);
    }
}