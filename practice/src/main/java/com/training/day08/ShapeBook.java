package com.training.day08;

import java.util.ArrayList;
import java.util.List;

/**
 * Day 8 - Q3: ShapeBook —— 把 Day 3 / Day 5 / Day 6 / Day 7 全部用上
 *
 * 这一题回答："我学那些到底有什么用？"
 *
 * ═══════════════════════════════════════════════════════════
 * TODO Q3-1: dedupe() —— 去重，并保持第一次出现的顺序
 * ═══════════════════════════════════════════════════════════
 *   输入 [Circle(1), Rectangle(2,3), Circle(1)]
 *     →  [Circle(1), Rectangle(2,3)]
 *
 *   提示：Day 7 的 LinkedHashSet。**它靠 Circle.equals/hashCode 工作** ——
 *   如果你 Q2-4 写错了，这题会跟着红。这就是"知识点是连着的"。
 *
 * ═══════════════════════════════════════════════════════════
 * TODO Q3-2: totalArea() —— 所有形状面积之和；空集合返回 0.0
 * ═══════════════════════════════════════════════════════════
 *   提示：遍历，调 area()。**这里体现多态** ——
 *   你不需要知道每个元素到底是圆还是矩形。
 *
 * ═══════════════════════════════════════════════════════════
 * TODO Q3-3: largest() —— 面积最大的那个形状；空集合返回 null
 * ═══════════════════════════════════════════════════════════
 *
 * ═══════════════════════════════════════════════════════════
 * TODO Q3-4: describeAll() —— 用 ", " 连接每个形状的 toString()
 * ═══════════════════════════════════════════════════════════
 *   输入 [Circle(1), Rectangle(3,4)]
 *     →  "圆(面积=3.14), 矩形(面积=12.00)"
 *   空集合 → ""
 *
 *   ⚠️ 这里和 Day 6 的 Zoo.describeAll 是**同一个坑**：
 *      分隔符"除了最后一个"，和你上次踩的那个一模一样。
 *      这次别再改 4 遍。写完先预测第一个和最后一个的输出。
 *
 * ⚠️ 先预测，再跑：
 *     // 预测：new ShapeBook(List.of()).largest()      → ____
 *     // 预测：new ShapeBook(List.of()).totalArea()    → ____
 *     // 预测：new ShapeBook(List.of()).describeAll()  → "____"
 */
public class ShapeBook {

    private final List<Shape> shapes;

    public ShapeBook(List<Shape> shapes) {
        this.shapes = new ArrayList<>(shapes);
    }

    /** 返回内部列表的副本（防御性复制） */
    public List<Shape> getShapes() {
        return new ArrayList<>(shapes);
    }

    // TODO Q3-1: dedupe()
    public List<Shape> dedupe() {
        throw new UnsupportedOperationException("TODO Q3-1");
    }

    // TODO Q3-2: totalArea()
    public double totalArea() {
        throw new UnsupportedOperationException("TODO Q3-2");
    }

    // TODO Q3-3: largest()
    public Shape largest() {
        throw new UnsupportedOperationException("TODO Q3-3");
    }

    // TODO Q3-4: describeAll()
    public String describeAll() {
        throw new UnsupportedOperationException("TODO Q3-4");
    }
}