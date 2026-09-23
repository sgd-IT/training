package com.training.day08;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Day 8 - Q2 测试：Shape 抽象类 + 多态
 *
 * 这一组测试的核心是最后两条：
 *   toString() 写在父类，却打印出子类算的面积。
 */
class ShapeTest {

    @Test
    void shapeIsAbstract() {
        // 抽象类不能 new —— 这是编译期保证，测试里只能用反射确认
        assertTrue(Modifier.isAbstract(Shape.class.getModifiers()), "Shape 必须是 abstract");
    }

    @Test
    void polymorphismThroughBaseType() {
        Shape s = new Circle(1);            // 声明类型 Shape，真实类型 Circle
        assertEquals(Math.PI, s.area(), 1e-9, "调的是 Circle 的 area()");
        assertEquals("圆", s.getName());
    }

    @Test
    void toStringComesFromBaseClassButAreaComesFromSubclass() {
        // ★ 本日最关键的两行：
        //   toString() 的代码在 Shape 里（父类写的），
        //   面积却是 Circle / Rectangle 算的（子类实现的）。
        assertEquals("圆(面积=3.14)", new Circle(1).toString());
        assertEquals("矩形(面积=12.00)", new Rectangle(3, 4).toString());
    }

    @Test
    void baseTypeArrayHoldsBoth() {
        Shape[] shapes = { new Circle(1), new Rectangle(2, 3) };
        double sum = 0;
        for (Shape s : shapes) {
            sum += s.area();
        }
        assertEquals(Math.PI + 6, sum, 1e-9, "遍历时不需要知道每个元素到底是圆还是矩形");
    }

    @Test
    void invalidArgumentsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Circle(0));
        assertThrows(IllegalArgumentException.class, () -> new Circle(-1));
        assertThrows(IllegalArgumentException.class, () -> new Rectangle(0, 5));
        assertThrows(IllegalArgumentException.class, () -> new Rectangle(5, -2));
    }
}