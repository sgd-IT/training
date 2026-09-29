package com.training.day09;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Day 9 - Q1 测试：MiniList（手写迷你 ArrayList）
 *
 * 这一组测试把"size"和"capacity"两个概念钉死。
 */
class MiniListTest {

    @Test
    void newListIsEmptyAndHasNoArrayYet() {
        MiniList<String> list = new MiniList<>();
        assertEquals(0, list.size());
        assertEquals(0, list.capacity(), "懒初始化：还没 add 过，数组还没分配");
    }

    @Test
    void firstAddAllocatesDefaultCapacity() {
        MiniList<String> list = new MiniList<>();
        list.add("a");
        assertEquals(1, list.size());
        assertEquals(4, list.capacity(), "第一次 add 才分配 DEFAULT_CAPACITY = 4");
    }

    @Test
    void sizeIsNotCapacity() {
        MiniList<String> list = new MiniList<>();
        for (int i = 0; i < 3; i++) {
            list.add("x");
        }
        assertEquals(3, list.size(), "真实元素个数");
        assertEquals(4, list.capacity(), "底层数组长度 —— 还有 1 个空位");
    }

    @Test
    void growsByDoubling() {
        MiniList<String> list = new MiniList<>();
        for (int i = 0; i < 4; i++) {
            list.add("x");
        }
        assertEquals(4, list.capacity(), "刚好装满，还不该扩容");

        list.add("x");
        assertEquals(5, list.size());
        assertEquals(8, list.capacity(), "满了 -> 翻倍到 8");

        for (int i = 0; i < 4; i++) {
            list.add("x");
        }
        assertEquals(9, list.size());
        assertEquals(16, list.capacity(), "再满 -> 再翻倍到 16");
    }

    @Test
    void keepsInsertionOrder() {
        MiniList<String> list = new MiniList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        assertEquals("a", list.get(0));
        assertEquals("b", list.get(1));
        assertEquals("c", list.get(2));
    }

    @Test
    void growthDoesNotLoseData() {
        MiniList<Integer> list = new MiniList<>();
        for (int i = 0; i < 20; i++) {
            list.add(i);
        }
        assertEquals(20, list.size());
        for (int i = 0; i < 20; i++) {
            assertEquals(i, list.get(i).intValue(), "扩容时数据必须原样搬过去");
        }
    }

    @Test
    void getOutOfBounds() {
        MiniList<String> list = new MiniList<>();
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(0),
                "空列表取 0 —— 注意：这时数组还是 null，不能直接碰 data");
        list.add("a");
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(1), "size=1 时取下标 1");
        assertThrows(IndexOutOfBoundsException.class, () -> list.get(-1), "负数下标");
    }

    @Test
    void toStringFormat() {
        MiniList<String> list = new MiniList<>();
        assertEquals("[]", list.toString(), "空列表");
        list.add("a");
        assertEquals("[a]", list.toString(), "一个元素不能有多余分隔符");
        list.add("b");
        list.add("c");
        assertEquals("[a, b, c]", list.toString());
    }

    // ─────────────────────────────────────────────────────────────
    // 下面两个是【教练补的】。原版 8 个测试全绿，但它们是假的 ——
    // 上面所有测试用的元素都非空，于是"遍历整个数组 + 跳过 null"
    // 这个错误实现，被 null 恰好不在集合里这件事完整地掩盖了。
    // 详见 _probe/NullProbe.java 的对照输出。
    // ─────────────────────────────────────────────────────────────

    @Test
    void toStringMustNotDropNullElements() {
        // null 是【合法元素】—— ArrayList 允许 add(null)，MiniList 也必须允许
        MiniList<String> list = new MiniList<>();
        list.add("a");
        list.add(null);
        list.add("b");
        assertEquals("[a, null, b]", list.toString(),
                "null 也是一个元素，不能被跳过（JDK 的 ArrayList 输出就是 [a, null, b]）");
    }

    @Test
    void singleNullElementIsNotAnEmptyList() {
        MiniList<String> list = new MiniList<>();
        list.add(null);
        assertEquals(1, list.size());
        assertEquals("[null]", list.toString(),
                "size 明明是 1，toString 不能显示成空列表");
    }
}