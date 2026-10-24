package com.training.day11;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Day 11 - Q2 测试：HashSpread
 * <p>
 * 这一组测试的目的不是"函数写对了没"，而是**让扰动函数的收益变成一个数字**：
 * 同一批 hashCode，扰动前占 1 个桶，扰动后占 4 个桶。
 */
class HashSpreadTest {

    /** 低 16 位全 0、只有高位不同的四个 hashCode —— 真实世界里 HashMap 的灾难输入。 */
    private static final int[] HIGH_BIT_ONLY = {
            0x0000_0000, 0x0001_0000, 0x0002_0000, 0x0003_0000
    };

    @Test
    void indexWithoutSpreadTakesLowBits() {
        assertEquals(0, HashSpread.indexWithoutSpread(0x0000_0000, 16));
        assertEquals(0, HashSpread.indexWithoutSpread(0x0001_0000, 16),
                "0x0001_0000 的低 4 位是 0 —— 这就是灾难的来源");
        assertEquals(15, HashSpread.indexWithoutSpread(-1, 16));
    }

    @Test
    void disturbPullsHighBitsDown() {
        assertEquals(0, HashSpread.disturb(0), "0 扰动后还是 0");
        assertEquals(0x0002_0002, HashSpread.disturb(0x0002_0000),
                "高位 0x0002 被异或到低位上，低 4 位从 0b0000 变成 0b0010");
        assertEquals(0x0001_0001, HashSpread.disturb(0x0001_0000));
    }

    @Test
    void disturbUsesUnsignedShift() {
        // -1 = 0xFFFF_FFFF；>>> 16 = 0x0000_FFFF；异或 → 0xFFFF_0000 = -65536
        assertEquals(-65536, HashSpread.disturb(-1),
                "必须用 >>>（无符号右移）。若误用 >>，结果是 0xFFFFFFFF ^ 0xFFFFFFFF = 0");
        assertEquals(0, HashSpread.disturb(-1) & 0xFFFF, "低 16 位被抹成 0");
    }

    @Test
    void withoutSpreadCollapsesHighBitOnlyHashesIntoOneBucket() {
        assertEquals(1, HashSpread.countUsedBuckets(HIGH_BIT_ONLY, 16, false),
                "四个不同的键全挤在 0 号桶 —— 链表长度 4，查询退化");
    }

    @Test
    void spreadSeparatesHighBitOnlyHashes() {
        assertEquals(4, HashSpread.countUsedBuckets(HIGH_BIT_ONLY, 16, true),
                "扰动后落到 0、1、2、3 四个桶 —— 冲突被拆开了");
    }

    @Test
    void spreadNeverHurtsSequentialHashes() {
        int[] seq = new int[1000];
        for (int i = 0; i < seq.length; i++) {
            seq[i] = i;
        }
        assertEquals(16, HashSpread.countUsedBuckets(seq, 16, false),
                "连续整数不扰动也能铺满 16 个桶");
        assertEquals(16, HashSpread.countUsedBuckets(seq, 16, true),
                "扰动不能把已经很均匀的输入变坏");
    }

    @Test
    void countUsedBucketsIsBounded() {
        int[] two = {0, 1};
        assertTrue(HashSpread.countUsedBuckets(two, 16, false) <= 2,
                "桶数不可能超过元素个数");
        int[] twenty = new int[20];
        for (int i = 0; i < 20; i++) {
            twenty[i] = i;
        }
        assertTrue(HashSpread.countUsedBuckets(twenty, 16, false) <= 16,
                "桶数不可能超过容量");
    }

    @Test
    void emptyInputUsesNoBuckets() {
        assertEquals(0, HashSpread.countUsedBuckets(new int[0], 16, false),
                "空输入 = 0 个桶。别为这个写特判，循环本来就不进");
    }
}