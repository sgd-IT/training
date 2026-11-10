package com.training.day17;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Day 17 测试 —— 契约都在断言消息里，先读再动手。
 * <p>
 * 8 个随 TODO 变绿；最后 5 个是**天然绿**（不用写实现，考概念）。
 */
class StreamLabTest {

    // ═══════════════════════════════════════════════════════════
    // Q1 · 惰性
    // ═══════════════════════════════════════════════════════════

    @Test
    void buildingAStreamRunsNothing() {
        List<String> log = new ArrayList<>();
        Stream<String> s = StreamLab.watched(List.of("a", "b", "c"), log);
        assertEquals(0, log.size(),
                "返回流的那一刻，一个元素都还没处理 —— 流是惰性的。"
                        + "你实际记了 " + log);
        assertNotNull(s);
    }

    @Test
    void terminalOperationRunsEverything() {
        List<String> log = new ArrayList<>();
        List<String> out = StreamLab.watched(List.of("a", "b", "c"), log).toList();
        assertEquals(List.of("a", "b", "c"), out, "元素本身不能被改动");
        assertEquals(List.of("a", "b", "c"), log,
                "终止操作一调用，元素才被逐个处理，而且顺序是固定的");
    }

    // ═══════════════════════════════════════════════════════════
    // Q2 · 短路
    // ═══════════════════════════════════════════════════════════

    @Test
    void firstAboveReturnsTheFirstMatch() {
        assertEquals(6, StreamLab.firstAbove(5, new ArrayList<>()));
        assertEquals(1, StreamLab.firstAbove(0, new ArrayList<>()));
    }

    @Test
    void onlySixNumbersWereTouched() {
        List<Integer> seen = new ArrayList<>();
        StreamLab.firstAbove(5, seen);
        assertEquals(List.of(1, 2, 3, 4, 5, 6), seen,
                "无限流能返回，只能是因为【找到就停】—— 这就是短路。"
                        + "如果这里看到超过 6 个数，说明中间用了不短路的方式收集");
    }

    // ═══════════════════════════════════════════════════════════
    // Q3 · 扁平化
    // ═══════════════════════════════════════════════════════════

    @Test
    void flattenConcatenatesEveryInnerList() {
        List<List<Integer>> nested = List.of(
                List.of(1, 2),
                List.of(3),
                List.of(),
                List.of(4, 5));
        assertEquals(List.of(1, 2, 3, 4, 5), StreamLab.flatten(nested),
                "map 只会给你 Stream<List<Integer>>，摊不平");
    }

    @Test
    void flattenOfNothingIsEmptyNotNull() {
        List<Integer> out = StreamLab.flatten(List.of());
        assertNotNull(out, "空输入要返回空列表，不是 null");
        assertTrue(out.isEmpty());
    }

    // ═══════════════════════════════════════════════════════════
    // Q4 · toList() 不可变
    // ═══════════════════════════════════════════════════════════

    @Test
    void immutableCopyKeepsContentAndOrder() {
        assertEquals(List.of("a", "b"), StreamLab.immutableCopy(List.of("a", "b")));
        assertTrue(StreamLab.immutableCopy(List.of()).isEmpty());
    }

    @Test
    void immutableCopyCannotBeModified() {
        List<String> copy = StreamLab.immutableCopy(List.of("a", "b"));
        assertThrows(UnsupportedOperationException.class, () -> copy.add("c"),
                "toList() 给的是不可变 List —— 用 collect(Collectors.toList()) 会在这里红");
    }

    // ═══════════════════════════════════════════════════════════
    // 天然绿 · 不用写 TODO 就能过，考的是概念
    // ═══════════════════════════════════════════════════════════

    @Test
    void aStreamCanOnlyBeConsumedOnce() {
        Stream<String> s = Stream.of("a", "b");
        assertEquals(2, s.count());
        IllegalStateException ex = assertThrows(IllegalStateException.class, s::count,
                "流是一条管道，走完一遍就废了 —— 再走只能是新的 list.stream()");
        assertTrue(ex.getMessage().contains("already been operated upon"),
                "JDK 的原话：" + ex.getMessage());
    }

    @Test
    void countSkipsPeekOnSizedStreams() {
        List<String> log = new ArrayList<>();
        long n = List.of("a", "b", "c").stream().peek(log::add).count();
        assertEquals(3, n);
        assertEquals(0, log.size(),
                "反直觉但真的：流的大小已知时，count() 只用元素个数就够了，"
                        + "JDK 9+ 会【跳过】peek 这类无状态中间操作");
    }

    @Test
    void streamNeverTouchesTheSourceCollection() {
        List<Integer> src = new ArrayList<>(List.of(1, 2, 3));
        List<Integer> doubled = src.stream().map(x -> x * 2).toList();
        assertEquals(List.of(2, 4, 6), doubled);
        assertEquals(List.of(1, 2, 3), src, "流不修改源集合 —— 它只是在源上盖了一条管道");
    }

    @Test
    void collectToListIsMutableWhileToListIsNot() {
        List<String> mutable = Stream.of("a", "b").collect(java.util.stream.Collectors.toList());
        mutable.add("c");
        assertEquals(List.of("a", "b", "c"), mutable,
                "collect(Collectors.toList()) 给的是普通 ArrayList，可以 add");

        List<String> immutable = Stream.of("a", "b").toList();
        assertThrows(UnsupportedOperationException.class, () -> immutable.add("c"),
                "toList() 不给改 —— 这就是 Q4 的答案来源");
    }

    @Test
    void limitMakesAnInfiniteStreamFinite() {
        List<Integer> first3 = Stream.iterate(1, n -> n + 1).limit(3).toList();
        assertEquals(List.of(1, 2, 3), first3,
                "limit 也是短路操作 —— 没有它，这个流永远收不完");
    }
}
