package com.training.day07;

import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

/** Day 7 - Q4 测试：ValueUtils（java.util.Objects 工具箱） */
class ValueUtilsTest {

    @Test
    void isSameHandlesBothNull() {
        assertTrue(ValueUtils.isSame(null, null), "两个都是 null 应该算相等");
    }

    @Test
    void isSameHandlesOneNull() {
        assertFalse(ValueUtils.isSame(null, "a"));
        assertFalse(ValueUtils.isSame("a", null));
    }

    @Test
    void isSameComparesByValue() {
        // 用 new String 强制造出两个不同对象（避免字符串常量池干扰）
        assertTrue(ValueUtils.isSame(new String("abc"), new String("abc")));
        assertFalse(ValueUtils.isSame(new String("abc"), new String("abd")));
    }

    @Test
    void describeHandlesNull() {
        assertEquals("null", ValueUtils.describe(null), "null 应该变成字符串 \"null\"，不能抛 NPE");
    }

    @Test
    void describeCallsToString() {
        Object custom = new Object() {
            @Override
            public String toString() {
                return "我是自定义的toString";
            }
        };
        assertEquals("我是自定义的toString", ValueUtils.describe(custom),
                "describe 内部应该调用了 toString()");
        assertEquals("abc", ValueUtils.describe("abc"));
    }

    @Test
    void hashOfMatchesObjectsHash() {
        assertEquals(Objects.hash("a", 1, true), ValueUtils.hashOf("a", 1, true));
        assertEquals(ValueUtils.hashOf("a", 1), ValueUtils.hashOf("a", 1), "同样的输入必须给出同样的哈希");
    }
}