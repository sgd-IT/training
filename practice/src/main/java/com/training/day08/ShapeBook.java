package com.training.day08;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Day 8 - Q3: ShapeBook —— 把 Day 3 / Day 5 / Day 6 / Day 7 全部用上
 * <p>
 * 这一题回答："我学那些到底有什么用？"
 * <p>
 * ═══════════════════════════════════════════════════════════
 * TODO Q3-1: dedupe() —— 去重，并保持第一次出现的顺序
 * ═══════════════════════════════════════════════════════════
 *   输入 [Circle(1), Rectangle(2,3), Circle(1)]
 *     →  [Circle(1), Rectangle(2,3)]
 * <p>
 * 提示：Day 7 的 LinkedHashSet。**它靠 Circle.equals/hashCode 工作** ——
 * 如果你 Q2-4 写错了，这题会跟着红。这就是"知识点是连着的"。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * TODO Q3-2: totalArea() —— 所有形状面积之和；空集合返回 0.0
 * ═══════════════════════════════════════════════════════════
 *   提示：遍历，调 area()。**这里体现多态** ——
 *   你不需要知道每个元素到底是圆还是矩形。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * TODO Q3-3: largest() —— 面积最大的那个形状；空集合返回 null
 * ═══════════════════════════════════════════════════════════
 * <p>
 * ═══════════════════════════════════════════════════════════
 * TODO Q3-4: describeAll() —— 用 ", " 连接每个形状的 toString()
 * ═══════════════════════════════════════════════════════════
 *   输入 [Circle(1), Rectangle(3,4)]
 *     →  "圆(面积=3.14), 矩形(面积=12.00)"
 *   空集合 → ""
 * <p>
 * ⚠️ 这里和 Day 6 的 Zoo.describeAll 是**同一个坑**：
 * 分隔符"除了最后一个"，和你上次踩的那个一模一样。
 * 这次别再改 4 遍。写完先预测第一个和最后一个的输出。
 * <p>
 * ⚠️ 先预测，再跑：
 * // 预测：new ShapeBook(List.of()).largest()      → 最大的面积
 * // 预测：new ShapeBook(List.of()).totalArea()    → 0.0
 * // 预测：new ShapeBook(List.of()).describeAll()  → getName() + "(面积=" + String.format("%.2f", area()) + ")"，getName() + "(面积=" + String.format("%.2f", area()) + ")";
 */
public class ShapeBook {

    private final List<Shape> shapes;

    public ShapeBook(List<Shape> shapes) {
        this.shapes = new ArrayList<>(shapes);
    }

    /**
     * 返回内部列表的副本（防御性复制）
     */
    public List<Shape> getShapes() {
        return new ArrayList<>(shapes);
    }

    public List<Shape> dedupe() {
        // 创建一个 LinkedHashMap 来去重并保持顺序
        LinkedHashMap<Shape, Boolean> map = new LinkedHashMap<>();
        //遍历出每一个
        for (Shape shape : shapes) {
            //存入map中，如果不存在则存入
            map.putIfAbsent(shape, true);
        }
        return new ArrayList<>(map.keySet());
    }

    public double totalArea() {
        return shapes.stream()
                .mapToDouble(Shape::area)
                .sum();
    }

    public Shape largest() {
        //先转为流，再比较面积，返回最大值，如果为空则返回 null
        return shapes.stream()
                .max((s1, s2) -> Double.compare(s1.area(), s2.area()))
                .orElse(null);
    }


    public String describeAll() {
        //先转为流，再转为字符串，用逗号连接
        return shapes.stream()
                .map(Shape::toString)
                .collect(Collectors.joining(", "));
    }
}