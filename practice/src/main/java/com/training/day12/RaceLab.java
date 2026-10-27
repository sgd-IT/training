package com.training.day12;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Day 12 - Q1~Q3: RaceLab —— 线程不安全到底是"什么"不安全
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 一句话
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   map.put(k, map.get(k) + 1)     ← 这一行不是一步，是三步
 * </pre>
 * <pre>
 *   ① get(k)        读 0
 *   ② 0 + 1         算 1
 *   ③ put(k, 1)     写 1
 * </pre>
 * 两个线程如果都在 ① 之后、③ 之前挤进来，就会**同时读到 0**，
 * 然后都写 1 —— 两次加一，最终结果只有 1。这叫**丢失更新**（lost update）。
 * <p>
 * ⚠️ 注意：**HashMap 本身没有"坏掉"**。数组还是那个数组，链表还是那个链表。
 * 出事的是"读-算-写"这三步之间被别人插了队。
 * 所以 `Collections.synchronizedMap` / `synchronized (map)` 包住这三步就能救 ——
 * 这也是为什么"用线程安全的容器"和"把复合操作弄成原子"是两件事。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 三个版本，跑同一件事
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   Q1 checkThenAct            HashMap  + get / put            → 会丢
 *   Q2 synchronizedCheckThenAct HashMap + synchronized 包住     → 不丢
 *   Q3 atomicWithMerge         ConcurrentHashMap + 一个原子方法 → 不丢 ← 生产里该用这个
 * </pre>
 * <p>
 * ═══════════════════════════════════════════════════════════
 * ⚠️ 先说清楚测试为什么这样设计
 * ═══════════════════════════════════════════════════════════
 * 并发丢数据是**概率事件** —— 同一份代码这次丢 300，下次丢 280，偶尔一个不丢。
 * 所以"断言必须丢多少"的测试是坏测试（会随机红）。
 * <p>
 * 这组测试的处理方式：
 * <pre>
 *   · Q2 / Q3 必须【精确相等】（它们有原子性保证，断言精确值是安全的）
 *   · Q1 只断言"不超过"，把丢了多少打印出来 —— 它是【观察型】测试
 *   · 另有一个用 latch 精确编排的测试，把"丢失"变成【必然发生】，不靠概率
 * </pre>
 * <p>
 * ═══════════════════════════════════════════════════════════
 * TODO 清单
 * ═══════════════════════════════════════════════════════════
 *   TODO Q1: checkThenAct            lambda 里写 get / +1 / put 三步
 *   TODO Q2: synchronizedCheckThenAct 同样三步，但整段包在 synchronized (map) 里
 *   TODO Q3: atomicWithMerge         一行原子操作（提示：merge 或 compute）
 * <p>
 * ⚠️ 先预测，再跑（写【输出值】）：
 * <pre>
 * // 预测：checkThenAct(8, 2000)             → 2001(不受多线程影响)
 * // 预测：synchronizedCheckThenAct(8, 2000) → 2001
 * // 预测：atomicWithMerge(8, 2000)          → 2001
 * </pre>
 * ⚠️ 别调大线程数来"让它更容易失败" —— 先想清楚**为什么**会丢，再谈概率。
 */
public class RaceLab {

    static final String KEY = "counter";

    /**
     * 给定的并发脚手架：开 threads 个线程，每个跑 perThread 次 task，一起发令起跑。
     * <p>
     * 用 CountDownLatch 而不是直接 start()，是为了让所有线程**尽量同时**冲进去 ——
     * 否则线程 1 可能早就跑完了，压根撞不上。
     */
    static void runConcurrently(int threads, int perThread, Runnable task) {
        CountDownLatch start = new CountDownLatch(1);
        // ⚠️ 线程里抛的异常默认只会打到 stderr，join() 照样正常返回 ——
        //    那样"任务炸了"会被伪装成"数字就是 0"，测试的失败信息全是误导。
        //    所以这里必须自己接住，跑完再在主线程里抛出去。
        AtomicReference<Throwable> failure = new AtomicReference<>();
        Thread[] ts = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            ts[i] = new Thread(() -> {
                try {
                    start.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                for (int j = 0; j < perThread; j++) {
                    try {
                        task.run();
                    } catch (Throwable t) {
                        failure.compareAndSet(null, t);
                        return;
                    }
                }
            });
            ts[i].start();
        }
        start.countDown();
        for (Thread t : ts) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        Throwable t = failure.get();
        if (t != null) {
            throw new RuntimeException("并发任务里抛异常了", t);
        }
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q1
    // ═══════════════════════════════════════════════════════════
    /**
     * 最朴素的写法：普通 {@code HashMap}，lambda 里三步走。
     *
     * @return 跑完之后 map 里 KEY 的值（理想值 = threads × perThread）
     */
    public static int checkThenAct(int threads, int perThread) {
        Map<String, Integer> map = new HashMap<>();
        map.put(KEY, 0);
        runConcurrently(threads, perThread, () -> {
            int oldValue = map.get(KEY);
            int newValue = oldValue + 1;
            map.put(KEY, newValue);
        });
        return map.get(KEY);
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q2
    // ═══════════════════════════════════════════════════════════
    /**
     * 同样三步，但整段包在 {@code synchronized (map)} 里。
     * <p>
     * 想清楚：锁的是谁？为什么锁 {@code map} 这个对象就够？
     * 如果两个线程锁的是**不同**的对象，还能互斥吗？
     */
    public static int synchronizedCheckThenAct(int threads, int perThread) {
        Map<String, Integer> map = new HashMap<>();
        map.put(KEY, 0);
        runConcurrently(threads, perThread, () -> {
            synchronized (map) {
                int oldValue = map.get(KEY);
                int newValue = oldValue + 1;
                map.put(KEY, newValue);
            }
        });
        return map.get(KEY);
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q3
    // ═══════════════════════════════════════════════════════════
    /**
     * 生产代码该有的样子：{@code ConcurrentHashMap} + **一个原子方法**。
     * <p>
     * 整个 lambda 只有一行。别用 get / put —— 那又变回三步了。
     * 提示：Day 10 学的 {@code merge} 在 {@code ConcurrentHashMap} 上是**原子的**。
     * <p>
     * ⚠️ 为什么这个不需要 {@code synchronized}？因为"读-算-写"三步被 JDK 挪进了内部，
     * 用 CAS 保证同一个桶上的操作不会交错。**锁没了，但原子性还在。**
     */
    public static int atomicWithMerge(int threads, int perThread) {
        Map<String, Integer> map = new ConcurrentHashMap<>();
        map.put(KEY, 0);
        runConcurrently(threads, perThread, () -> {
            map.merge(KEY, 1, Integer::sum);
        });
        return map.get(KEY);
    }
}