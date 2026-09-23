package com.training.day08;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/** Day 8 - Q2 测试：Circle（多态 + Object 三兄弟） */
class CircleTest {

    @Test
    void areaAndPerimeter() {
        Circle c = new Circle(2);
        assertEquals(Math.PI * 4, c.area(), 1e-9, "π * 2 * 2");
        assertEquals(Math.PI * 4, c.perimeter(), 1e-9, "2 * π * 2");
    }

    @Test
    void getters() {
        Circle c = new Circle(2);
        assertEquals(2, c.getRadius());
        assertEquals("圆", c.getName());
    }

    @Test
    void equalsComparesRadius() {
        assertEquals(new Circle(2), new Circle(2));
        assertNotEquals(new Circle(2), new Circle(3));
    }

    @Test
    void equalsHandlesNullOtherTypeAndItself() {
        Circle c = new Circle(2);
        assertTrue(c.equals(c), "自反");
        assertFalse(c.equals(null), "null 不相等");
        assertFalse(c.equals("圆"), "类型不同不相等");
    }

    @Test
    void circleNeverEqualsRectangle() {
        // ⚠️ 这一条专门验证 equals 第③步用的是 getClass() 而不是 instanceof
        assertNotEquals(new Circle(2), new Rectangle(2, 2), "圆 ≠ 矩形");
    }

    @Test
    void hashCodeConsistentWithEquals() {
        assertEquals(new Circle(2).hashCode(), new Circle(2).hashCode());
    }

    @Test
    void usableAsHashSetElement() {
        Set<Circle> set = new HashSet<>();
        set.add(new Circle(2));
        set.add(new Circle(2));
        set.add(new Circle(3));
        assertEquals(2, set.size(), "内容相同的去重，不同的保留");
    }
}