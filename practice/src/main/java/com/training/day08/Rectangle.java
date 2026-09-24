package com.training.day08;

/**
 * Day 8 - Q2: Rectangle —— 继承 Shape
 * <p>
 * TODO Q2-6: area()      → width * height
 * TODO Q2-7: perimeter() → 2 * (width + height)
 * TODO Q2-8: equals(Object o) —— 两个字段都要比
 * TODO Q2-9: hashCode()
 * <p>
 * ⚠️ 陷阱：Rectangle(3,4) 和 Rectangle(4,3) 的
 * area 都是 12、perimeter 都是 14 —— 完全一样。
 * 如果你的 equals 只比面积或周长，测试会抓到你。
 * equals 必须比"能唯一确定这个对象"的原始字段。
 * <p>
 * ⚠️ 先预测，再跑：
 * // 预测：new Rectangle(3,4).equals(new Rectangle(4,3)) → false
 */
public class Rectangle extends Shape {

    private final int width;
    private final int height;

    public Rectangle(int width, int height) {
        super("矩形");
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("width/height 必须 > 0，实际: " + width + "x" + height);
        }
        this.width = width;
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }


    @Override
    public double area() {
        return width * height;
    }

    @Override
    public double perimeter() {
        //矩形周长公式
        return 2 * (width + height);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rectangle rectangle = (Rectangle) o;
        return width == rectangle.width && height == rectangle.height;
    }

    @Override
    public int hashCode() {
        return width * 31 + height;
    }
}