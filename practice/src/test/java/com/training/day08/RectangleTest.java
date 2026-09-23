package com.training.day08;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Day 8 - Q2 测试：Rectangle（多态 + Object 三兄弟） */
class RectangleTest {

    @Test
    void areaAndPerimeter() {
        Rectangle r = new Rectangle(3, 4);
        assertEquals(12.0, r.area(), 1e-9);
        assertEquals(14.0, r.perimeter(), 1e-9);
    }

    @Test
    void getters() {
        Rectangle r = new Rectangle(3, 4);
        assertEquals(3, r.getWidth());
        assertEquals(4, r.getHeight());
        assertEquals("矩形", r.getName());
    }

    @Test
    void equalsComparesWidthAndHeight() {
        assertEquals(new Rectangle(3, 4), new Rectangle(3, 4));
        assertNotEquals(new Rectangle(3, 4), new Rectangle(3, 5), "高度不同");
        assertNotEquals(new Rectangle(3, 4), new Rectangle(5, 4), "宽度不同");
    }

    @Test
    void swappedSidesAreNotEqual() {
        // ⚠️ Rectangle(3,4) 和 Rectangle(4,3)：
        //    面积都是 12、周长都是 14 —— 完全一样！
        //    如果你的 equals 只比面积或周长，这里会红。
        assertNotEquals(new Rectangle(3, 4), new Rectangle(4, 3),
                "长宽互换不是同一个矩形 —— equals 必须比原始字段，不能比算出来的面积/周长");
    }

    @Test
    void equalsHandlesNullAndOtherType() {
        Rectangle r = new Rectangle(3, 4);
        assertTrue(r.equals(r));
        assertFalse(r.equals(null));
        assertFalse(r.equals("矩形"));
        assertFalse(r.equals(new Circle(3)), "矩形 ≠ 圆");
    }

    @Test
    void hashCodeConsistentWithEquals() {
        assertEquals(new Rectangle(3, 4).hashCode(), new Rectangle(3, 4).hashCode());
    }

    @Test
    void usableAsHashMapKey() {
        Map<Rectangle, String> map = new HashMap<>();
        map.put(new Rectangle(3, 4), "A");
        assertEquals("A", map.get(new Rectangle(3, 4)),
                "取不到 —— 检查 equals/hashCode 是否重写正确");
        assertNull(map.get(new Rectangle(4, 3)), "长宽互换的不该取到");
    }
}