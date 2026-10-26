package com.training.day12;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Day 12 - 测试：RaceLab
 * <p>
 * 设计原则见 {@link RaceLab} 类注释：**能精确断言的地方精确断言，
 * 不能的地方改成观察型**，另外用一个 latch 编排的测试把"丢失"变成必然。
 */
class RaceLabTest {

    private static final int THREADS = 8;
    private static final int PER_THREAD = 2000;
    private static final int EXPECTED = THREADS * PER_THREAD;

    @Test
    void atomicWithMergeIsExact() {
        assertEquals(EXPECTED, RaceLab.atomicWithMerge(THREADS, PER_THREAD),
                "ConcurrentHashMap.merge 是原子的，一次都不能丢");
    }

    @Test
    void synchronizedCheckThenActIsExact() {
        assertEquals(EXPECTED, RaceLab.synchronizedCheckThenAct(THREADS, PER_THREAD),
                "synchronized 包住读-算-写，一步都不能少");
    }

    @Test
    void checkThenActOnHashMapLosesUpdates() {
        // ⚠️ 观察型测试：不保证一定丢（概率事件），所以只断言"不超过"。
        // 真正的价值在下面那行输出 —— 让你看见自己丢了多少。
        int actual = RaceLab.checkThenAct(THREADS, PER_THREAD);
        System.out.println("[观察] 普通 HashMap 8 线程 × 2000 次：期望 " + EXPECTED + "，实得 " + actual
                + "，丢了 " + (EXPECTED - actual));
        assertTrue(actual <= EXPECTED, "只可能少，不可能多 —— 多出来说明逻辑写反了");
    }

    @Test
    void interleavingShowsExactlyHowAnUpdateIsLost() throws Exception {
        // 用 latch 精确编排，让"丢失更新"从概率事件变成必然事件。
        Map<String, Integer> map = new HashMap<>();
        map.put("k", 0);

        CountDownLatch aRead0 = new CountDownLatch(1);
        CountDownLatch bWritten1 = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(2);

        Thread a = new Thread(() -> {
            int old = map.get("k");          // ① A 读到 0
            aRead0.countDown();              // 告诉 B：我读完了
            try {
                bWritten1.await();           // 等 B 写 1
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            map.put("k", old + 1);           // ③ A 写回 0+1 = 1，把 B 的 1 覆盖成 1
            done.countDown();
        });

        Thread b = new Thread(() -> {
            try {
                aRead0.await();              // 等 A 读完
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            map.put("k", map.get("k") + 1);  // ② B 读到 0，写 1
            bWritten1.countDown();
            done.countDown();
        });

        a.start();
        b.start();
        assertTrue(done.await(5, TimeUnit.SECONDS), "两个线程 5 秒内必须跑完");
        a.join();
        b.join();

        assertEquals(1, map.get("k"),
                "两个线程各加了一次，结果却是 1 而不是 2 —— 这就是丢失更新");
    }

    @Test
    void concurrentHashMapRejectsNullKey() {
        ConcurrentHashMap<String, Integer> chm = new ConcurrentHashMap<>();
        assertThrows(NullPointerException.class, () -> chm.put(null, 1),
                "ConcurrentHashMap 不允许 null 键 —— 因为它要靠键的 hash 定位到桶再 CAS");
    }

    @Test
    void concurrentHashMapRejectsNullValue() {
        ConcurrentHashMap<String, Integer> chm = new ConcurrentHashMap<>();
        assertThrows(NullPointerException.class, () -> chm.put("a", null),
                "也不允许 null 值 —— 否则 get 返回 null 就分不清'没有'和'值是 null'（Day 10 讲过）");
    }

    @Test
    void hashtableRejectsNullTooButIsObsolete() {
        Hashtable<String, Integer> ht = new Hashtable<>();
        assertThrows(NullPointerException.class, () -> ht.put("a", null));
        // Hashtable 的每个方法都加 synchronized，等于整个表一把大锁 ——
        // 并发度退化成 1，性能惨烈。JDK 1.2 之后就被 ConcurrentHashMap 取代。
        assertEquals(0, ht.size(),
                "null 值在【落桶之前】就被挡掉了，一个元素都没进去");
    }

    @Test
    void hashMapAllowsNullKeyAndNullValue() {
        Map<String, Integer> map = new HashMap<>();
        map.put(null, 1);
        map.put("a", null);
        assertEquals(2, map.size(), "HashMap 两个都允许 —— 这正是它和 CHM 的一个实用差别");
        assertNull(map.get("a"));
    }
}