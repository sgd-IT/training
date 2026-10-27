package com.training.day14;

import java.util.List;

/**
 * Day 14 - Q1~Q4: GenericsLab —— 泛型 + ☕ JDK 21 Record
 * <p>
 * ═══════════════════════════════════════════════════════════
 * ★ 必读：今天开工前先把这一整段读完（考点全在这里）
 * ═══════════════════════════════════════════════════════════
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 一、泛型存在的唯一理由
 * ═══════════════════════════════════════════════════════════
 * Day 9 你手写过 {@code MiniList}，里面存的是 {@code Object[]}：
 * <pre>
 *   list.add("a");
 *   String s = (String) list.get(0);    // 每次取都要强转，转错了运行时才炸
 * </pre>
 * 泛型把"强转"和"类型检查"从**运行时**搬到了**编译期**：
 * <pre>
 *   List&lt;String&gt; list = new ArrayList&lt;&gt;();
 *   // list.add(1);      ← 编译就报错，不用等到运行
 *   String s = list.get(0);   // 不用强转
 * </pre>
 * 一句话：**泛型是给编译器看的，不是给 JVM 看的。**
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 二、★ 类型擦除（Type Erasure）—— 今天的核心
 * ═══════════════════════════════════════════════════════════
 * 编译之后，{@code List<String>}、{@code List<Integer>}、{@code List} 在字节码里
 * <b>是同一个类</b>，元素全变成 {@code Object}。这叫擦除。
 * <p>
 * 证据（测试里有）：
 * <pre>
 *   List&lt;String&gt;.class == List&lt;Integer&gt;.class      // 都是 java.util.List
 *   new ArrayList&lt;String&gt;().getClass() == new ArrayList&lt;Integer&gt;().getClass()
 * </pre>
 * 擦除带来的**三个后果**，都考：
 * <pre>
 *   ① 不能 new T[]                ← Day 10 复述检验第 1 题，今天补上后半段
 *   ② 不能 new T()、不能 T.class
 *   ③ 不能在运行时判断 List&lt;String&gt; 还是 List&lt;Integer&gt;（instanceof 只能写 List）
 * </pre>
 * 为什么 {@code new T[]} 不行：数组在运行期必须知道自己装的是什么
 * （{@code ArrayStoreException} 就靠它拦），而 {@code T} 已经被擦成上界了，
 * 编译器不知道要给这条 {@code newarray} 指令填什么类型 —— JLS 要求数组元素类型是
 * **reifiable**（可在运行期完整表达），类型变量不是。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 三、★ 通配符 PECS（最难、面试最常问）
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   List&lt;? extends Number&gt;    上界通配符   →  只能【读】出来，不能 add
 *   List&lt;? super Integer&gt;     下界通配符   →  只能【写】进去，读出来只能是 Object
 *   List&lt;Number&gt;              没有通配符   →  又能读又能写，但只能传 List&lt;Number&gt;
 * </pre>
 * 为什么 {@code ? extends} 不能 add：
 * <pre>
 *   void f(List&lt;? extends Number&gt; l) { l.add(1); }     // 编译错误
 *   传进来可能是 List&lt;Double&gt; —— 往里塞 Integer 就毁了它
 * </pre>
 * PECS = <b>P</b>roducer <b>E</b>xtends, <b>C</b>onsumer <b>S</b>uper：
 * <pre>
 *   你从集合里往外【取】→ 用 extends（它是生产者）
 *   你往集合里【放】    → 用 super（它是消费者）
 * </pre>
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 四、☕ JDK 21 的 Record
 * ═══════════════════════════════════════════════════════════
 * 写一个"只装数据、不可变"的类，以前要几十行：字段 + 全参构造 + getter + equals
 * + hashCode + toString。Record 一行：
 * <pre>
 *   record Pair&lt;K, V&gt;(K key, V value) { }
 * </pre>
 * 编译器自动生成：
 * <pre>
 *   · 私有 final 字段 key / value
 *   · 全参构造器
 *   · 访问器 key() / value()    ← 注意：不是 getKey()！没有 get 前缀
 *   · equals / hashCode（按所有字段，Day 7 的契约）
 *   · toString → "Pair[key=a, value=1]"
 * </pre>
 * 三个限制：
 * <pre>
 *   · 字段全 final —— 不可变，创建后改不了
 *   · 不能继承别的类（record 已隐式 extends java.lang.Record）
 *   · 不能声明额外的实例字段
 * </pre>
 * 注意 Day 7 讲过 equals/hashCode 契约，record **一次性帮你写对** ——
 * 这正是它最值的地方（手写这两个方法出错率极高）。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * TODO 清单
 * ═══════════════════════════════════════════════════════════
 *   TODO Q1: Pair         用 record 声明，泛型两个参数 K / V，字段 key / value
 *   TODO Q2: maxOf        有界类型参数 &lt;T extends Comparable&lt;T&gt;&gt;，返回最大元素
 *   TODO Q3: sumOf        List&lt;? extends Number&gt; 求和（PECS 的读）
 *   TODO Q4: fillWith     List&lt;? super Integer&gt; 填数（PECS 的写）
 * <p>
 * ⚠️ 先预测，再跑（写【输出值】）：
 * <pre>
 * // 预测：new ArrayList&lt;String&gt;().getClass() == new ArrayList&lt;Integer&gt;().getClass() → ____
 * // 预测：new Pair&lt;&gt;("a", 1).toString() → ____
 * // 预测：new Pair&lt;&gt;("a", 1).equals(new Pair&lt;&gt;("a", 1)) → ____
 * </pre>
 */
public class GenericsLab {

    // ═══════════════════════════════════════════════════════════
    // TODO Q1
    // ═══════════════════════════════════════════════════════════
    /**
     * 一个只装两个值的不可变容器。
     * <p>
     * 用 {@code record} 一行声明，泛型参数 {@code K} / {@code V}，字段名 {@code key} / {@code value}。
     * 不要写构造器、不要写 getter、不要写 equals —— 编译器全给。
     * <p>
     * ⚠️ 注意访问器名字：{@code pair.key()}，不是 {@code pair.getKey()}。
     */
    public static class Pair<K, V> {
        // TODO Q1: 把上面这行 class 声明改成 record 声明，然后把下面这三个成员全删掉。
        //          （它们只是为了让测试能编译，不代表实现要这么写。）

        public Pair(K key, V value) {
            throw new UnsupportedOperationException("TODO Q1: 这个类是 record 才对");
        }

        public K key() {
            throw new UnsupportedOperationException("TODO Q1: 这个类是 record 才对");
        }

        public V value() {
            throw new UnsupportedOperationException("TODO Q1: 这个类是 record 才对");
        }
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q2
    // ═══════════════════════════════════════════════════════════
    /**
     * 返回列表中最大的元素，空列表返回 {@code null}。
     * <p>
     * 签名必须是：{@code public static <T extends Comparable<T>> T maxOf(List<T> items)}
     * <p>
     * 想一想：为什么不能写成 {@code <T> T maxOf(List<T>)}？
     * （提示：不写上界，编译器只知道 T 是 Object，它不会 compareTo）
     * <p>
     * ⚠️ 泛型方法的 {@code <T>} 写在**返回值之前**、修饰符之后，这个位置别记错。
     */
    public static <T extends Comparable<T>> T maxOf(List<T> items) {
        throw new UnsupportedOperationException("TODO Q2");
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q3
    // ═══════════════════════════════════════════════════════════
    /**
     * 把列表里所有数字加起来。
     * <p>
     * 签名必须用 {@code List<? extends Number>} —— 这样才能同时接收
     * {@code List<Integer>}、{@code List<Double>}、{@code List<Long>}。
     * <p>
     * ⚠️ 这个方法**只读不写**。正因为只读，{@code ? extends} 才成立。
     */
    public static double sumOf(List<? extends Number> numbers) {
        throw new UnsupportedOperationException("TODO Q3");
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q4
    // ═══════════════════════════════════════════════════════════
    /**
     * 往列表里填入 {@code 0, 1, ..., count-1}。
     * <p>
     * 签名必须用 {@code List<? super Integer>} —— 这样才能接收
     * {@code List<Integer>}、{@code List<Number>}、{@code List<Object>}。
     * <p>
     * ⚠️ 这个方法**只写不读**。正因为只写，{@code ? super} 才成立。
     * <p>
     * 想清楚：为什么 {@code List<? extends Integer>} 在这里会编译失败？
     */
    public static void fillWith(List<? super Integer> target, int count) {
        throw new UnsupportedOperationException("TODO Q4");
    }
}