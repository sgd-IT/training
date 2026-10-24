package com.training.day11;

/**
 * Day 11 - Q1: MiniHashMap —— 用你的手走一遍 HashMap 的主干
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 为什么今天要手写一个 Map
 * ═══════════════════════════════════════════════════════════
 * Day 9 你手写过 MiniList，看清了"数组 + size + 扩容"。
 * Map 只多了一件事：**用哈希把键换算成一个数组下标**。
 * <pre>
 *   put("cat", 1)
 *        ↓  hash("cat")  → 某个 int（比如 98262）
 *        ↓  indexFor(98262, 16)  → 98262 &amp; 15 = 6
 *   table[6] 里挂一个 Node(key="cat", value=1)
 * </pre>
 * 一句话：**Map = 数组 + 哈希函数 + 冲突链表**。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 结构：数组 + 链表（JDK 8 起还会变红黑树，今天只写链表够用）
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   table:  [ null ][ null ][ N ][ null ] ... [ null ]
 *                            │
 *                            ▼
 *                     Node(hash,key=cat,value=1) → Node(...) → null
 * </pre>
 * 同一个下标上挂多个 Node，就叫**哈希冲突**。链表的长度就是冲突的程度。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 三个常量，先记住数字
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   初始容量           16         （必须是 2 的幂，indexFor 靠它才快）
 *   负载因子           0.75f      （threshold = 16 × 0.75 = 12）
 *   阈值 threshold     12         （size &gt; 12 就扩容）
 * </pre>
 * 为什么 0.75 而不是 1？—— 填得太满，冲突链表变长，查询退化；
 * 填得太空，白占内存。0.75 是 JDK 权衡出来的值，**记住数字 + 记住这个权衡**。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * ⚠️ 为什么下标是 (capacity - 1) &amp; hash 而不是 hash % capacity
 * ═══════════════════════════════════════════════════════════
 * 当 capacity 是 2 的幂时，这两者**结果完全一样**，但 {@code &} 快得多
 * （位运算 vs 除法）。这就是容量必须是 2 的幂的唯一原因 ——
 * 换成 15 这种数，&amp; 就取不出 0..14 的全部下标了。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * TODO 清单
 * ═══════════════════════════════════════════════════════════
 *   TODO Q1-1: indexFor(hash, capacity)  —— 一行，算出桶下标
 *   TODO Q1-2: get(key)                  —— 算下标 → 遍历链表 → 比对 key
 *   TODO Q1-3: put(key, value)           —— 键在就覆盖并返回旧值；不在就插到链表
 *   TODO Q1-4: resize()                  —— 容量翻倍，把所有 Node 重新挂一遍
 * <p>
 * ⚠️ 三条硬要求（测试会盯死）：
 * <pre>
 *   · put 一个已存在的键 → **返回旧值**，size 不变（HashMap.put 的原语义）
 *   · put 一个新键       → 返回 null
 *   · null 键要支持      → 它的 hash 当成 0，所以永远落在 0 号桶
 * </pre>
 * ⚠️ 先预测，再跑（写【输出值】）：
 * <pre>
 * // 预测：new MiniHashMap&lt;&gt;() 的 capacity 和 threshold     → ____
 * // 预测：连续 put 13 个不同键之后，capacity 和 size        → ____
 * // 预测：put("a",1) 之后再 put("a",2) 的返回值             → ____
 * </pre>
 * ⚠️ 别去抄 JDK 的 HashMap 源码 —— 它为了性能写得极其扭曲（懒初始化、
 * treeify、成环检测、红黑树分裂）。你要写的是**能讲清楚原理的版本**，不是它的复制品。
 */
public class MiniHashMap<K, V> {

    /** 初始容量：16，必须是 2 的幂。 */
    static final int DEFAULT_CAPACITY = 16;

    /** 负载因子 0.75f。 */
    static final float LOAD_FACTOR = 0.75f;

    /** 链表节点。hash 存下来是为了 resize 时不用重算，也为了 get 时先比 hash。 */
    static final class Node<K, V> {
        final int hash;
        final K key;
        V value;
        Node<K, V> next;

        Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }
    }

    /** 桶数组。注意泛型擦除：这里只能是 Node[]，不能是 Node&lt;K,V&gt;[]（Day 10 复述检验 #1 刚讲过）。 */
    @SuppressWarnings("unchecked")
    Node<K, V>[] table = (Node<K, V>[]) new Node[DEFAULT_CAPACITY];

    /** 已存键值对个数。 */
    int size;

    /** 扩容阈值 = capacity × LOAD_FACTOR。 */
    int threshold = (int) (DEFAULT_CAPACITY * LOAD_FACTOR);

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /** 供测试观察 —— 真实 HashMap 没有这个公开方法，这里是为了让你"看得见"。 */
    public int capacity() {
        return table.length;
    }

    public int threshold() {
        return threshold;
    }

    /**
     * 键的哈希值。null 的 hash 定义为 0（这样 null 永远落在 0 号桶）。
     * <p>
     * 注意：这里只用 {@code key.hashCode()}，**没有做扰动**。
     * JDK 里还有一步 {@code h ^ (h >>> 16)}，那是 Q2 的主题。
     */
    int hash(K key) {
        return key == null ? 0 : key.hashCode();
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q1-1
    // ═══════════════════════════════════════════════════════════
    /**
     * 由 hash 算出数组下标，返回 0..capacity-1。
     * <p>
     * 一行。想想为什么是 {@code &amp;} 而不是 {@code %}（注释里已经写了，但你要能自己讲出来）。
     */
    static int indexFor(int hash, int capacity) {
        throw new UnsupportedOperationException("TODO Q1-1");
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q1-2
    // ═══════════════════════════════════════════════════════════
    /**
     * 查值。
     * <p>
     * 形状三步：
     * <pre>
     *   ① 算下标，拿到该桶的第一个 Node
     *   ② 顺着 next 走链表
     *   ③ 每个节点先比 hash（快），hash 相等再比 key（用 Objects.equals，Day 7 的 NPE 防线）
     * </pre>
     * 找不到返回 null。
     */
    public V get(K key) {
        throw new UnsupportedOperationException("TODO Q1-2");
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q1-3
    // ═══════════════════════════════════════════════════════════
    /**
     * 存值，返回**被覆盖的旧值**（新键返回 null）。
     * <p>
     * 形状：
     * <pre>
     *   ① 算下标
     *   ② 顺着链表找：hash 相等 &amp;&amp; key 相等 → 覆盖 value，return 旧值
     *   ③ 没找到 → 新节点插进链表（JDK 8 是尾插；今天随你，但要说得出区别）
     *   ④ size++；然后 **如果 size &gt; threshold 就 resize()**
     * </pre>
     * ⚠️ 顺序坑：先 put、后判阈值，还是先判阈值、后 put？两种都能过测试，
     *    但要能说出你选的那种在"容量刚好卡在 12"时差在哪。
     */
    public V put(K key, V value) {
        throw new UnsupportedOperationException("TODO Q1-3");
    }

    public boolean containsKey(K key) {
        return get(key) != null;
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q1-4
    // ═══════════════════════════════════════════════════════════
    /**
     * 扩容：容量翻倍，把旧数组里所有节点**重新算下标**挂到新数组上。
     * <p>
     * <pre>
     *   oldCap = table.length
     *   newCap = oldCap * 2
     *   threshold = (int) (newCap * LOAD_FACTOR)
     *   遍历旧 table 的每个桶，顺着链表把每个节点搬过去（每个节点都要重算 indexFor）
     * </pre>
     * ⚠️ 三个坑，测试都盯着：
     * <pre>
     *   · 节点搬过去时，next 要断开重挂，不能把旧链表的尾巴原样带过去
     *   · 一个桶里的节点搬运后可能被分到**两个不同的新桶**（这就是扩容后遍历顺序会变的原因）
     *   · size 不变！扩容不动元素个数
     * </pre>
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        throw new UnsupportedOperationException("TODO Q1-4");
    }
}