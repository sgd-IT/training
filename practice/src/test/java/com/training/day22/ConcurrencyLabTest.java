package com.training.day22;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Day 22 测试 —— 契约都在断言消息里，先读再动手。
 * <p>
 * **8 个随 TODO 变绿**（Q1 / Q2 / Q3a / Q3b / Q4 / Q5 / Q6 / Q7）；
 * 最后 3 个是**天然绿**（不用写实现，考概念，先跑一遍看它们说什么）。
 * <p>
 * ⚠️ 今天的测试有一条跟前面 21 天都不一样：
 * <b>Q4 是「绿 = bug 存在」</b> —— 它断言的是 {@code < 400000}。
 * 你要是手滑加了 synchronized，这条会变红。别慌，那说明你修了不该修的东西。
 * <p>
 * ⚠️ 还有一条：<b>并发测试绿了也不代表对</b>，只代表这次没撞上。
 * 详见类注释第五节。
 */
class ConcurrencyLabTest {

    // ═══════════════════════════════════════════════════════════
    // Q1 · 起线程 + 线程名
    // ═══════════════════════════════════════════════════════════

    @Test
    void q1_nameComesFromTheNewThread() throws InterruptedException {
        assertEquals("day22-worker", new ConcurrencyLab().nameInNewThread(),
                "读到 main = 你调的是 run()，根本没开新线程；读到 null = 没 join 就读了");
    }

    // ═══════════════════════════════════════════════════════════
    // Q2 · run() 不是 start()
    // ═══════════════════════════════════════════════════════════

    @Test
    void q2_runExecutesOnTheCallingThread() {
        assertEquals("main", new ConcurrencyLab().nameViaRun(),
                "run() 只是一次普通方法调用 —— 线程名跟调用方一样，一条新线程都没开");
    }

    // ═══════════════════════════════════════════════════════════
    // Q3 · join 的意义
    // ═══════════════════════════════════════════════════════════

    @Test
    void q3a_noJoinMeansYouReadBeforeTheyFinish() throws InterruptedException {
        assertEquals(0, new ConcurrencyLab().sizeBeforeJoin(new CopyOnWriteArrayList<>()),
                "3 条线程都在 sleep(200)，主线程 microsecond 级就返回了 —— start() 是异步的，不等就是 0");
    }

    @Test
    void q3b_joinWaitsForAllOfThem() throws InterruptedException {
        List<String> sink = new CopyOnWriteArrayList<>();
        assertEquals(3, new ConcurrencyLab().sizeAfterJoin(sink),
                "join 完之后必须正好 3 个；少于 3 = 有的线程没 join");
        assertTrue(sink.containsAll(List.of("day22-0", "day22-1", "day22-2")),
                "3 个名字都要在，实际 = " + sink);
    }

    // ═══════════════════════════════════════════════════════════
    // Q4 · ★ 证明丢更新真的存在（绿 = bug 复现成功）
    // ═══════════════════════════════════════════════════════════

    @Test
    void q4_lostUpdateIsReal() throws InterruptedException {
        int expected = 8 * 50_000;
        int actual = new ConcurrencyLab().plainIncrement(8, 50_000);
        System.out.println("[Q4] 期望 " + expected + "，实得 " + actual
                + "，丢了 " + (expected - actual) + " 次");
        assertTrue(actual < expected,
                "期望 < " + expected + "（证明丢更新存在），实际 = " + actual
                        + " —— 等于说明你加了保护，这题不该加");
    }

    // ═══════════════════════════════════════════════════════════
    // Q5 · synchronized 修好
    // ═══════════════════════════════════════════════════════════

    @Test
    void q5_synchronizedFixesIt() throws InterruptedException {
        assertEquals(400_000, new ConcurrencyLab().lockedIncrement(8, 50_000),
                "加了锁就必须一次不丢；少一次 = 你锁的地方不对（比如锁了方法外的代码，或者用了两个不同的锁对象）");
    }

    // ═══════════════════════════════════════════════════════════
    // Q6 · AtomicInteger 修好
    // ═══════════════════════════════════════════════════════════

    @Test
    void q6_atomicFixesIt() throws InterruptedException {
        assertEquals(400_000, new ConcurrencyLab().atomicIncrement(8, 50_000),
                "原子类必须一次不丢，且不许加锁");
    }

    // ═══════════════════════════════════════════════════════════
    // Q7 · volatile 可见性
    // ═══════════════════════════════════════════════════════════

    @Test
    void q7_stopMustBeVisibleAcrossThreads() {
        assertTimeoutPreemptively(Duration.ofSeconds(10), () -> {
            assertTrue(new ConcurrencyLab().stopIsVisible(2000),
                    "循环没退出 = 那个字段缺的修饰符你还没加（可见性）；"
                            + "或者 join 没带超时、daemon 没设");
        }, "超过 10 秒还没返回 —— 大概率你写了无超时的 join()");
    }

    // ═══════════════════════════════════════════════════════════
    // 天然绿 1~3 · 不写实现，先跑一遍看断言在说什么
    // ═══════════════════════════════════════════════════════════

    @Test
    void concept1_newThreadIsInNewState() {
        Thread t = new Thread(() -> {
        });
        assertEquals(Thread.State.NEW, t.getState(),
                "造出来但没 start，状态是 NEW —— 这时它一次都没被调度过");
    }

    @Test
    void concept2_startOnlyOnce() {
        Thread t = new Thread(() -> {
        });
        t.start();
        assertThrows(IllegalThreadStateException.class, t::start,
                "同一条线程只能 start 一次 —— 第二次抛 IllegalThreadStateException");
    }

    @Test
    void concept3_interruptOnlySetsAFlag() throws InterruptedException {
        Thread t = new Thread(() -> {
        });
        t.start();
        t.join();
        Thread.currentThread().interrupt();
        assertTrue(Thread.currentThread().isInterrupted(),
                "interrupt() 只是把标志位置 true，并不会强行打断正在跑的代码");
        assertTrue(Thread.interrupted(),
                "Thread.interrupted() 会返回当前标志并**顺手清掉** —— 这是它跟 isInterrupted() 的唯一区别");
        assertFalse(Thread.currentThread().isInterrupted(),
                "上一步已经清掉了，所以现在是 false");
    }
}
