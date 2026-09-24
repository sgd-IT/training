package com.training.day08;

/**
 * Day 8 - Q2: 抽象类 Shape —— Phase 1 综合（Day 5 继承/多态 + Day 6 抽象 + Day 7 toString）
 *
 * 这个类故意写得很薄：它自己不知道任何形状的面积怎么算，
 * 只声明"每个形状都必须能算出面积和周长"。
 *
 * ═══════════════════════════════════════════════════════════
 * 关键设计：toString() 写在父类里，却调用子类的 area()
 * ═══════════════════════════════════════════════════════════
 *   Shape.toString() 里会调 area()。而 area() 是抽象的 ——
 *   父类没有实现，运行期分派到子类。
 *
 *   Day 5 的结论在这里再次生效：
 *     javac 记录的类名 ≡ 声明类型；JVM 执行谁的代码只看真实类型。
 *   所以 new Circle(1).toString() 打印的面积，是 Circle 算的。
 *
 * ═══════════════════════════════════════════════════════════
 * TODO Q2-1: 重写 toString()
 * ═══════════════════════════════════════════════════════════
 *   格式： getName() + "(面积=" + 保留两位小数的 area() + ")"
 *
 *     new Circle(1).toString()       →  "圆(面积=3.14)"
 *     new Rectangle(3, 4).toString() →  "矩形(面积=12.00)"
 *
 *   保留两位小数： String.format("%.2f", area())
 *   （Day 4 学过 String 的 API，format 是其中的格式化方法）
 *
 * ⚠️ 先预测，再跑（第 5 条硬规矩）：
 *   动手前先在注释里写  // 预测：new Circle(1).toString() → "____"
 */
public abstract class Shape {

    private final String name;

    protected Shape(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /** 面积 —— 子类必须实现 */
    public abstract double area();

    /** 周长 —— 子类必须实现 */
    public abstract double perimeter();


    @Override
    public String toString() {
        // 预测：new Circle(1).toString() → "圆(面积=3.14)"
        return getName() + "(面积=" + String.format("%.2f", area()) + ")";
    }
}