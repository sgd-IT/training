package com.training.day07;

/**
 * Day 7 - Q1: 给 Point 重写 Object 三兄弟（你来补全）
 *
 * 先读 RawPoint.java 和 RawPointTest.java —— 那是"不重写"的样子。
 * 你的任务：让 Point 变成"按内容比较"的类。
 *
 * ═══════════════════════════════════════════════════════════
 * 第一步：三个方法的签名，一个字都不能错
 * ═══════════════════════════════════════════════════════════
 *
 *   @Override public String  toString()
 *   @Override public boolean equals(Object o)     ← 参数是 Object ！不是 Point ！
 *   @Override public int     hashCode()
 *
 * ⚠️⚠️ 今天最大的坑，没有之一 ⚠️⚠️
 *
 *   如果你写成   public boolean equals(Point o)     ← 参数是 Point
 *   那不是"重写"，那是"重载"！两个方法**同时存在**。
 *
 *   后果：别人写 obj1.equals(obj2) 时，如果 obj1 的**声明类型是 Object**，
 *   会去调 Object.equals（比地址），你写的那个 equals(Point) **永远不被调用**。
 *   —— 代码看起来完全正确，测试却怎么都过不了，而且极难排查。
 *
 *   所以：**这三个方法一律加 @Override**。
 *   @Override 的作用是"如果我没真的重写父类方法，就编译报错"——
 *   它是编译器给你的免费保险。
 *
 * ═══════════════════════════════════════════════════════════
 * 第二步：三个方法各自要做到什么
 * ═══════════════════════════════════════════════════════════
 *
 * 【1】toString()
 *     返回 "(3, 4)" 这种格式（x 在前、y 在后、逗号后面恰好一个空格）
 *     new Point(3, 4).toString()  →  "(3, 4)"
 *     new Point(-1, 0).toString() →  "(-1, 0)"
 *
 * 【2】equals(Object o) —— 按内容比，四件事按顺序做：
 *     ① o 和 this 是同一个对象吗？   是 → 直接 return true
 *        （这叫"自反"，同时也是性能优化）
 *     ② o 是 null 吗？              是 → return false
 *     ③ o 的类型对吗？              不是 Point → return false
 *        提示：用  getClass() != o.getClass()  判断
 *     ④ 前面都过了 → 把 o 强转成 Point，再比 x 和 y
 *        提示：int 直接用 == 比
 *
 * 【3】hashCode() —— 返回一个 int。只有一条规则：
 *     **equals 相等的两个对象，hashCode 必须相等。**
 *     （反过来不要求：hashCode 相等时 equals 可以不等 —— 那叫"哈希冲突"）
 *
 *     写法二选一：
 *       简单版：Objects.hash(x, y)          （需要 import java.util.Objects）
 *       手动版：31 * x + y                   （31 是个经典选择，写完问我为什么）
 *
 * ═══════════════════════════════════════════════════════════
 * ⚠️ 今天的新规矩：两轮推演
 * ═══════════════════════════════════════════════════════════
 *
 *   昨天 Zoo 那题你改了 4 次才对，根因是"在编辑器里试错"。
 *   **每个方法写之前，先在注释里把推演写出来。** 例如 equals：
 *
 *     推演1: p1.equals(p1)              → ①同一个对象          → true
 *     推演2: p1.equals(new Point(3, 4)) → ①②③都过 → 强转 →
 *                                         x 都是 3、y 都是 4   → true
 *     推演3: p1.equals(null)            → ②null                → false
 *     推演4: p1.equals("字符串")        → ③类型不对            → false
 *
 *   **推演写完再动代码。这一步我检查时会看。**
 */
public class Point {

    private final int x;
    private final int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // TODO Q1-1: 重写 toString()（记得 @Override）
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    // TODO Q1-2: 重写 equals(Object o)（参数必须是 Object！）
    @Override
    public boolean equals(Object o){
        // a.equals(b)  等价于  b.equals(a)  true
        //a.equals(new Point(3, 4))  true  因为他们是同一个对象
        // a.equals(null)  false
        // a.equals("字符串")  false  因为类型不对
        if (this==o){
            return true;
        }
        if (o==null){
            return false;
        }
        if (getClass() != o.getClass()){
            return false;
        }
        Point point = (Point) o;
        return point.x == x && point.y == y;

    }


    // TODO Q1-3: 重写 hashCode()
    @Override
    public int hashCode() {
        return 31 * x + y;
    }

}