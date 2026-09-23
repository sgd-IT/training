package com.training.day08;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Day 8 - Q3 测试：ShapeBook
 *
 * 这一组把 Day 3（List）、Day 5/6（多态）、Day 7（equals/hashCode）串在一起。
 */
class ShapeBookTest {

    @Test
    void dedupeRemovesDuplicatesAndKeepsOrder() {
        ShapeBook book = new ShapeBook(List.of(
                new Circle(1), new Rectangle(2, 3), new Circle(1), new Rectangle(2, 3)));
        assertEquals(List.of(new Circle(1), new Rectangle(2, 3)), book.dedupe(),
                "顺序必须是第一次出现的顺序");
    }

    @Test
    void dedupeEmpty() {
        assertEquals(List.of(), new ShapeBook(List.of()).dedupe());
    }

    @Test
    void totalAreaIsPolymorphic() {
        ShapeBook book = new ShapeBook(List.of(
                new Circle(1), new Rectangle(2, 3), new Rectangle(1, 1)));
        assertEquals(Math.PI + 6 + 1, book.totalArea(), 1e-9);
    }

    @Test
    void totalAreaOnEmptyIsZero() {
        assertEquals(0.0, new ShapeBook(List.of()).totalArea(), 1e-9);
    }

    @Test
    void largestByArea() {
        ShapeBook book = new ShapeBook(List.of(
                new Rectangle(2, 3), new Circle(2), new Rectangle(1, 1)));
        assertEquals(new Circle(2), book.largest(), "圆(2) 面积约 12.57，最大");
    }

    @Test
    void largestOnEmptyIsNull() {
        assertNull(new ShapeBook(List.of()).largest());
    }

    @Test
    void describeAllJoinsWithCommaSpace() {
        ShapeBook book = new ShapeBook(List.of(new Circle(1), new Rectangle(3, 4)));
        assertEquals("圆(面积=3.14), 矩形(面积=12.00)", book.describeAll());
    }

    @Test
    void describeAllSingleElementHasNoTrailingComma() {
        assertEquals("圆(面积=3.14)", new ShapeBook(List.of(new Circle(1))).describeAll(),
                "只有一个元素时不能有多余的分隔符");
    }

    @Test
    void describeAllOnEmptyIsEmptyString() {
        assertEquals("", new ShapeBook(List.of()).describeAll());
    }

    @Test
    void constructorDefensivelyCopies() {
        List<Shape> input = new ArrayList<>();
        input.add(new Circle(1));
        ShapeBook book = new ShapeBook(input);
        input.add(new Circle(2));
        assertEquals(1, book.getShapes().size(), "改外面的 list 不该影响 book");
    }
}