package com.training.day16;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

/**
 * Day 16 - Q1~Q4: LambdaLab —— Lambda 与函数式接口的**机制**
 * <p>
 * ═══════════════════════════════════════════════════════════
 * ★ 必读：今天开工前先把这一整段读完（考点全在这里）
 * ═══════════════════════════════════════════════════════════
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 一、函数式接口 = 「只有一个抽象方法」的接口
 * ═══════════════════════════════════════════════════════════
 * {@code @FunctionalInterface} 这个注解**什么都不执行** —— 它只是让编译器在
 * 你写了两个抽象方法时报警。真正的判定规则是：
 * <pre>
 *   接口里有且只有一个抽象方法（SAM = Single Abstract Method）
 * </pre>
 * 加不加注解，都能用 lambda 赋值。JDK 自带的 {@code Function} / {@code Consumer} /
 * {@code Supplier} / {@code Predicate} 全部满足这个条件。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 二、★ Lambda 不是匿名内部类（今天第一个反直觉点）
 * ═══════════════════════════════════════════════════════════
 * 编译后你会发现两者的类名长得完全不一样：
 * <pre>
 *   lambda 写法   → Xxx$$Lambda$14/0x0000...
 *   匿名内部类    → Xxx$1
 * </pre>
 * 匿名内部类会在编译期**生成一个 .class 文件**（磁盘上真的有）；
 * lambda 不会 —— 它编译成一条 {@code invokedynamic} 指令，运行时由
 * {@code LambdaMetafactory} 在内存里**动态生成**类。
 * <p>
 * 面试问「lambda 和匿名内部类有什么区别」，说到 {@code invokedynamic} /
 * 运行时生成，才叫答到机制；只说「lambda 更简洁」是背的。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 三、★★ 变量捕获：只能捕获 effectively final（今天真正的核心）
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   int count = 0;
 *   Runnable r = () -&gt; count++;     // 编译错误：count 不是 effectively final
 * </pre>
 * <b>为什么不能改</b>：lambda 捕获的局部变量是**值的一份拷贝**（存在 lambda 对象的
 * 字段里），不是引用。如果允许改，你改的是那份拷贝，外面的 {@code count} 还是 0 ——
 * 代码看起来在工作，实际没有。所以编译器干脆禁止。
 * <p>
 * <b>那为什么 {@code int[]} 可以？</b>
 * <pre>
 *   int[] box = {0};
 *   Runnable r = () -&gt; box[0]++;    // 合法
 * </pre>
 * 因为 {@code box} 这个**引用**没变（还是 effectively final），
 * 改的是引用指向的那个对象的**内容**。拷贝的是引用，两个引用指向同一个数组。
 * <p>
 * 一句话：<b>不能改的是「变量」，不是「变量指向的对象」。</b>
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 四、★ Lambda 里的 this 是谁
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   lambda       里的 this → 外层对象
 *   匿名内部类   里的 this → 匿名内部类自己
 * </pre>
 * 因为 lambda **没有自己的 this** —— 它根本不是一个类。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 五、组合方法的顺序（andThen vs compose）
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   f.andThen(g)    先 f 后 g      f.andThen(g).apply(x) 等价于 g(f(x))
 *   g.compose(f)    先 f 后 g      上面那个的镜像写法
 *   p.and(q) / p.or(q)            有短路：左边已定结果，右边根本不执行
 * </pre>
 * <p>
 * ═══════════════════════════════════════════════════════════
 * TODO 清单
 * ═══════════════════════════════════════════════════════════
 *   TODO Q1: doubler      用 lambda 返回一个 Transformer（入参 * 2）
 *   TODO Q2: counter      用 int[] 做计数器，依次返回 0,1,2,...
 *   TODO Q3: chain        把两个 Transformer 串起来：先 first 再 second
 *   TODO Q4: lambdaThis   返回 () -&gt; 外层类名（看 lambda 的 this 是谁）
 * <p>
 * ⚠️ 先预测，再跑（写【输出值】）：
 * <pre>
 * // 预测：doubler().getClass().getName() 大概长什么样 →
 * // 预测：lambdaThis().get() 返回什么 →
 * // 预测：anonymousThis().get() 返回什么 →
 * // 预测：counter() 连续调三次 getAsInt() 得到 →
 * </pre>
 */
public class LambdaLab {

    /**
     * 只有一个抽象方法的接口 —— 写不写 {@code @FunctionalInterface} 都能用 lambda。
     */
    public interface Transformer {
        int apply(int value);
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q1
    // ═══════════════════════════════════════════════════════════
    /**
     * 用 <b>lambda</b> 返回一个 {@link Transformer}：把入参乘 2。
     * <p>
     * 契约：
     * <ul>
     *   <li>{@code doubler().apply(3)} == 6</li>
     *   <li>{@code doubler()} 返回对象的类名里含 {@code $$Lambda}</li>
     * </ul>
     * <p>
     * ⚠️ 第二条在约束「必须真的用 lambda」—— 用匿名内部类糊过去的话
     * 类名是 {@code LambdaLab$1}，测试会红。
     */
    public static Transformer doubler() {
        // TODO Q1
        throw new UnsupportedOperationException("TODO Q1");
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q2
    // ═══════════════════════════════════════════════════════════
    /**
     * 返回一个「计数器」：每次 {@code getAsInt()} 依次返回 0, 1, 2, ...
     * <p>
     * 契约：
     * <ul>
     *   <li>连续调三次得到 0、1、2</li>
     *   <li>两次调用 {@code counter()} 得到的两个计数器**互不影响**</li>
     * </ul>
     * <p>
     * ⚠️ 不许用字段、不许用 {@code AtomicInteger}、不许用 static 变量 ——
     * <b>只能用方法里的局部状态</b>。
     * <p>
     * ⚠️ 想清楚：为什么不能写成
     * <pre>
     *   int count = 0;
     *   return () -&gt; count++;
     * </pre>
     * 而必须借助数组？（答不上来就看类注释第三节）
     */
    public static IntSupplier counter() {
        // TODO Q2
        throw new UnsupportedOperationException("TODO Q2");
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q3
    // ═══════════════════════════════════════════════════════════
    /**
     * 把两个 {@link Transformer} 串起来：<b>先执行 first，再把结果交给 second</b>。
     * <p>
     * 契约：
     * <ul>
     *   <li>{@code chain(v -&gt; v + 1, v -&gt; v * 10).apply(1)} == 20</li>
     *   <li>{@code chain(v -&gt; v * 10, v -&gt; v + 1).apply(1)} == 11</li>
     * </ul>
     * <p>
     * ⚠️ 两条都在，就说明「顺序」不是靠猜的 —— 顺序反了第二条会红。
     */
    public static Transformer chain(Transformer first, Transformer second) {
        // TODO Q3
        throw new UnsupportedOperationException("TODO Q3");
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q4
    // ═══════════════════════════════════════════════════════════
    /**
     * 返回一个 {@link Supplier}，<b>lambda 体里用 {@code this}</b>，返回外层对象的类名。
     * <p>
     * 契约：{@code lambdaThis().get()} 等于 {@code LambdaLab.class.getName()}。
     * <p>
     * ⚠️ 这个方法和下面的 {@link #anonymousThis()} 是**对照实验**：写法几乎一样，
     * 一个用 lambda、一个用匿名内部类，`this` 指的东西不一样。
     * 跑完测试再回来看类注释第四节。
     */
    public Supplier<String> lambdaThis() {
        // TODO Q4
        throw new UnsupportedOperationException("TODO Q4");
    }

    /**
     * 给好的对照实现，<b>不要改</b>：匿名内部类里的 {@code this} 是匿名类自己。
     */
    public Supplier<String> anonymousThis() {
        return new Supplier<String>() {
            @Override
            public String get() {
                return this.getClass().getName();
            }
        };
    }
}
