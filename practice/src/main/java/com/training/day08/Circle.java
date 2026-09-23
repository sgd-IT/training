package com.training.day08;

/**
 * Day 8 - Q2: Circle —— 继承 Shape
 *
 * 半径用 int，这样 equals 里可以直接用 == 比（避开 double 比较的坑）。
 *
 * TODO Q2-2: area()      → Math.PI * radius * radius
 * TODO Q2-3: perimeter() → 2 * Math.PI * radius
 * TODO Q2-4: equals(Object o) —— Day 7 的四步，一步都不能少
 * TODO Q2-5: hashCode()
 *
 * ⚠️ equals 第③步（类型检查）必须用  getClass() != o.getClass()
 *    想一想：如果写成  o instanceof Circle，会发生什么？
 *    （面试高频题，检查时我会追问）
 *
 * ⚠️ 先预测，再跑 —— 动手前把这两行写进注释：
 *     // 预测：new Circle(2).equals(new Circle(2))      → ____
 *     // 预测：new Circle(2).equals(new Rectangle(2,2)) → ____
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

    // TODO Q2-2: area()
    @Override
    public double area() {
        throw new UnsupportedOperationException("TODO Q2-2");
    }

    // TODO Q2-3: perimeter()
    @Override
    public double perimeter() {
        throw new UnsupportedOperationException("TODO Q2-3");
    }

    // TODO Q2-4: equals(Object o)
    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException("TODO Q2-4");
    }

    // TODO Q2-5: hashCode()
    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("TODO Q2-5");
    }
}