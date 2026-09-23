package com.training.day07;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/** Day 7 - Q2 测试：Book（含 String 字段，注意 null 安全） */
class BookTest {

    @Test
    void toStringHasReadableFormat() {
        assertEquals("三体 / 刘慈欣 / 2008", new Book("三体", "刘慈欣", 2008).toString());
    }

    @Test
    void equalsComparesValues() {
        assertEquals(new Book("三体", "刘慈欣", 2008), new Book("三体", "刘慈欣", 2008));
    }

    @Test
    void notEqualsDifferentValues() {
        Book base = new Book("三体", "刘慈欣", 2008);
        assertNotEquals(base, new Book("三体", "刘慈欣", 2009), "年份不同");
        assertNotEquals(base, new Book("三体", "别人", 2008), "作者不同");
        assertNotEquals(base, new Book("别的书", "刘慈欣", 2008), "书名不同");
    }

    @Test
    void equalsHandlesNullFields() {
        // ⚠️ 关键：如果 equals 里写成 title.equals(o.title)，这里会抛 NullPointerException
        //    必须用 Objects.equals(title, o.title)
        Book a = new Book(null, null, 0);
        Book b = new Book(null, null, 0);
        assertEquals(a, b, "两边字段都是 null 时应该相等，不能抛 NPE");
        assertNotEquals(a, new Book("三体", null, 0));
    }

    @Test
    void equalsHandlesNullAndOtherType() {
        Book b = new Book("三体", "刘慈欣", 2008);
        assertFalse(b.equals(null));
        assertFalse(b.equals("三体"));
        assertFalse(b.equals(new Point(1, 2)));
    }

    @Test
    void hashCodeConsistentWithEquals() {
        assertEquals(new Book("三体", "刘慈欣", 2008).hashCode(),
                new Book("三体", "刘慈欣", 2008).hashCode());
    }

    @Test
    void usableInHashSet() {
        Set<Book> set = new HashSet<>();
        set.add(new Book("三体", "刘慈欣", 2008));
        set.add(new Book("三体", "刘慈欣", 2008));
        assertEquals(1, set.size());
    }

    @Test
    void equalsHandlesEqualButDistinctStringInstances() {
        // ⚠️ 这一条测试专门堵一个洞：
        //    如果你在 equals 里对 String 字段写成  book.title == title，
        //    上面 7 个测试照样全绿 —— 因为字符串字面量会进常量池，"三体" == "三体" 是 true。
        //    必须用 new String(...) 强制造出两个不同对象，才能把这个 bug 逼出来。
        Book a = new Book(new String("三体"), new String("刘慈欣"), 2008);
        Book b = new Book(new String("三体"), new String("刘慈欣"), 2008);
        assertEquals(a, b, "内容相同的 String 必须用 equals / Objects.equals 比，不能用 ==");

        assertNotEquals(a, new Book(new String("三体"), new String("刘慈欣"), 2009), "年份不同");
        assertNotEquals(a, new Book(new String("三体"), new String("别人"), 2008), "作者不同");
    }
}