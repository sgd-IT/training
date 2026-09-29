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
}