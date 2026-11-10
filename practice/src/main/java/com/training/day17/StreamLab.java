package com.training.day17;

import java.util.List;
import java.util.stream.Stream;

/**
 * Day 17 - Q1~Q4: StreamLab —— Stream I：**惰性、短路、一次性、扁平化**
 * <p>
 * ═══════════════════════════════════════════════════════════
 * ★ 必读：今天开工前先把这一整段读完（考点全在这里）
 * ═══════════════════════════════════════════════════════════
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 一、★★ 流是惰性的：没有终止操作，一行都不跑
 * ═══════════════════════════════════════════════════════════
 * {@code stream().map(...).filter(...)} 只是**登记**要做什么，返回一个新的流对象。
 * 真正开始遍历元素，是在**终止操作**被调用的那一刻：
 * <pre>
 *   中间操作（返回 Stream）：map / filter / flatMap / peek / limit / sorted / distinct
 *   终止操作（返回非 Stream）：toList / collect / forEach / count / findFirst / anyMatch
 * </pre>
 * 所以「在 map 里记一行日志」这件事，如果你不写终止操作，**一行日志都不会有**。
 * 这不是 bug，是设计。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 二、★ 短路：无限流能停下来，全靠它
 * ═══════════════════════════════════════════════════════════
 * {@code Stream.iterate(1, n -&gt; n + 1)} 是**无限**的。
 * 但它配上 {@code filter(...).findFirst()} 就能返回 —— 找到第一个就停，
 * 后面的元素**根本不生成**。
 * <pre>
 *   短路操作：limit / findFirst / findAny / anyMatch / allMatch / noneMatch
 * </pre>
 * 对照判断题：{@code iterate(1, n -&gt; n + 1).filter(n -&gt; n &gt; 5).count()} 会怎样？
 * 答案：**永远不返回** —— {@code count} 不是短路操作，程序挂死。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 三、★ 流只能消费一次
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   Stream&lt;String&gt; s = list.stream();
 *   s.count();
 *   s.count();     // IllegalStateException: stream has already been operated upon or closed
 * </pre>
 * 流不是集合。集合**存**数据，流只是一条**管道**，走完一遍就废了。
 * 要再走一遍，就再 {@code list.stream()} 一次。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 四、map vs flatMap
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   map      一个元素 → 一个元素          Stream&lt;List&lt;Integer&gt;&gt;
 *   flatMap  一个元素 → 一个流，再摊平     Stream&lt;Integer&gt;
 * </pre>
 * 把 {@code List<List<Integer>>} 摊平成一维，只能用 flatMap。
 * 用 map 得到的是 {@code Stream<Stream<Integer>>} —— 类型就不对。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 五、toList() 与 collect(toList()) 不是一回事
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   stream.toList()                      返回【不可变】List，add 抛 UnsupportedOperationException
 *   stream.collect(Collectors.toList())  返回【可变】ArrayList
 * </pre>
 * {@code toList()} 是 JDK 16 才加的，语义更安全；{@code collect} 是老写法。
 * 面试问「两者的区别」，答到「不可变 / 可变」才算答到机制。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * TODO 清单
 * ═══════════════════════════════════════════════════════════
 *   TODO Q1: watched        返回一个流，每处理一个元素就往 log 里记一笔（**不许提前遍历**）
 *   TODO Q2: firstAbove     在**无限**自然数流里找第一个 &gt; limit 的数，并记录被检查过的数
 *   TODO Q3: flatten        把 {@code List<List<Integer>>} 摊平成一维
 *   TODO Q4: immutableCopy  用 {@code toList()} 得到一个**不可改**的 List
 * <p>
 * ⚠️ 先预测，再跑（写【输出值】，不是写「大概是」）：
 * <pre>
 * // 预测：watched(...) 刚构造完、还没终止时，log.size() = ?
 * // 预测：firstAbove(5, seen) 返回 ?；seen = ?
 * // 预测：immutableCopy(...) 之后对结果 add("c") 会 → ?
 * // 预测：List.of("a","b","c").stream().peek(log::add).count() 之后 log.size() = ?（这题反直觉）
 * </pre>
 */
public class StreamLab {

    // ═══════════════════════════════════════════════════════════
    // TODO Q1
    // ═══════════════════════════════════════════════════════════
    /**
     * 返回一个流：元素和 {@code source} 一样，但**每处理一个元素**就往 {@code log} 里记一笔。
     * <p>
     * 契约：
     * <ul>
     *   <li>方法**返回的那一刻**，{@code log} 必须是空的 —— 一个元素都还没处理</li>
     *   <li>调用终止操作（{@code toList()}）之后，{@code log} 里按顺序有全部元素</li>
     * </ul>
     * <p>
     * ⚠️ 这就是今天的核心：**中间操作只是登记，终止操作才真的跑。**
     * 你要是写成「先 forEach 记 log，再返回 source.stream()」，第一条就红。
     * <p>
     * 提示：{@code peek} 是专门用来「顺手记一笔」的中间操作，它返回流，不改元素。
     */
    public static Stream<String> watched(List<String> source, List<String> log) {
        throw new UnsupportedOperationException("TODO Q1");
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q2
    // ═══════════════════════════════════════════════════════════
    /**
     * 从 **1, 2, 3, ... 无限自然数流** 里，找第一个「严格大于 {@code limit}」的数；
     * 每**检查**过一个数就把它记进 {@code seen}。
     * <p>
     * 契约：
     * <ul>
     *   <li>{@code firstAbove(5, seen)} == 6</li>
     *   <li>{@code seen} == {@code [1, 2, 3, 4, 5, 6]} —— 只检查了 6 个数</li>
     * </ul>
     * <p>
     * ⚠️ 这条测试只有在**短路**成立时才可能跑完：无限流不做短路是死循环。
     * 你要是用 {@code toList()} 收集中间结果，测试会挂死（这本身就是答案的一半）。
     * <p>
     * 提示：{@code Stream.iterate(1, n -> n + 1)}；记录写在 {@code filter} 里；
     * 收尾用 {@code findFirst()}。
     */
    public static int firstAbove(int limit, List<Integer> seen) {
        throw new UnsupportedOperationException("TODO Q2");
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q3
    // ═══════════════════════════════════════════════════════════
    /**
     * 把 {@code List<List<Integer>>} **摊平**成一维 {@code List<Integer>}，保持原顺序。
     * <p>
     * 契约：
     * <ul>
     *   <li>{@code flatten([[1,2],[3],[],[4,5]])} == {@code [1,2,3,4,5]}</li>
     *   <li>空的外层列表 → 空的结果（不能是 null）</li>
     * </ul>
     * <p>
     * ⚠️ 用 {@code map} 做不出来 —— 先想清楚 {@code map} 会给你什么类型。
     */
    public static List<Integer> flatten(List<List<Integer>> nested) {
        throw new UnsupportedOperationException("TODO Q3");
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q4
    // ═══════════════════════════════════════════════════════════
    /**
     * 把 {@code source} 变成一条流再收回来，返回的 List 内容相同，但**不可修改**。
     * <p>
     * 契约：
     * <ul>
     *   <li>内容与 {@code source} 相同、顺序相同</li>
     *   <li>对返回值调用 {@code add(...)} 抛 {@code UnsupportedOperationException}</li>
     * </ul>
     * <p>
     * ⚠️ 这里有一对反例要分清：{@code toList()} 给不可变的，
     * {@code collect(Collectors.toList())} 给可变的。别拿后者糊过来，第二条会红。
     */
    public static List<String> immutableCopy(List<String> source) {
        throw new UnsupportedOperationException("TODO Q4");
    }
}
