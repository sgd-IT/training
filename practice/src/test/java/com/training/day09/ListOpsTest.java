package com.training.day09;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** Day 9 - Q2 测试：ListOps（遍历中删除的四种写法） */
class ListOpsTest {

    /** 每个测试都拿一份新副本，避免互相干扰 */
    private List<String> sample() {
        return new ArrayList<>(List.of("a", "b", "a", "c"));
    }

    @Test
    void forEachRemoveThrowsConcurrentModificationException() {
        List<String> list = sample();
        assertThrows(ConcurrentModificationException.class,
                () -> ListOps.removeByForEach(list, "a"),
                "单线程也会抛 —— 这个异常跟'几个线程'无关，跟'谁在改'有关");
    }

    @Test
    void iteratorRemoveWorks() {
        List<String> list = sample();
        ListOps.removeByIterator(list, "a");
        assertEquals(List.of("b", "c"), list);
    }

    @Test
    void removeIfWorks() {
        List<String> list = sample();
        ListOps.removeByRemoveIf(list, "a");
        assertEquals(List.of("b", "c"), list);
    }

    @Test
    void indexRemoveSilentlySkipsAdjacentDuplicates() {
        List<String> list = new ArrayList<>(List.of("a", "a", "a"));
        ListOps.removeByIndex(list, "a");
        assertEquals(List.of("a"), list,
                "索引删除不抛异常，但会漏元素 —— 这个 bug 比 CME 危险得多，因为它不报错");
    }

    @Test
    void indexRemoveLooksCorrectWhenNothingIsAdjacent() {
        List<String> list = new ArrayList<>(List.of("a", "b", "c"));
        ListOps.removeByIndex(list, "a");
        assertEquals(List.of("b", "c"), list,
                "没有相邻重复时它看起来完全正确 —— 所以这个 bug 极难发现");
    }

    // ─────────────────────────────────────────────────────────────
    // 教练补的。"for-each 里 remove 一定抛 CME" 这句话【不完整】——
    // 删的正好是倒数第二个元素时，cursor == size，hasNext() 直接返回
    // false，循环正常结束，一次异常都不抛。见 _probe/Day9Probe.java 段 A。
    // ─────────────────────────────────────────────────────────────

    @Test
    void forEachRemoveCanEscapeTheExceptionEntirely() {
        // 对照组：同一句代码，这个数据抛
        List<String> throwsCase = new ArrayList<>(List.of("a", "b", "c"));
        assertThrows(ConcurrentModificationException.class,
                () -> ListOps.removeByForEach(throwsCase, "a"));

        // 实验组：换个数据，同样的错误代码——不抛了，而且"看起来成功了"
        List<String> escapes = new ArrayList<>(List.of("a", "b"));
        ListOps.removeByForEach(escapes, "a");
        assertEquals(List.of("b"), escapes,
                "delete 倒数第二个 -> cursor==size -> hasNext()==false -> 静默退出。"
                        + "所以这个坑的可怕之处不是'会炸'，而是'有时候不炸'");
    }
}