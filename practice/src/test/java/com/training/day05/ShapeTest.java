package com.training.day05;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ShapeTest {

    @Test
    void circleArea() {
        assertEquals(Math.PI * 4, new Circle(2.0).area(), 0.0001);
    }

    @Test
    void rectangleArea() {
        assertEquals(12.0, new Rectangle(3, 4).area(), 0.0001);
    }

    @Test
    void namesInheritedFromShape() {
        assertEquals("圆形", new Circle(1.0).getName());
        assertEquals("矩形", new Rectangle(1, 2).getName());
    }

    @Test
    void circleRejectsNonPositiveRadius() {
        assertThrows(IllegalArgumentException.class, () -> new Circle(0));
        assertThrows(IllegalArgumentException.class, () -> new Circle(-1));
    }

    @Test
    void rectangleRejectsNonPositiveSide() {
        assertThrows(IllegalArgumentException.class, () -> new Rectangle(0, 4));
        assertThrows(IllegalArgumentException.class, () -> new Rectangle(3, -2));
    }

    @Test
    void baseShapeHasZeroArea() {
        assertEquals(0, new Shape("多边形").area(), 0.0001);
    }

    @Test
    void describeUsesOverriddenArea() {
        // 多态：describe() 定义在 Shape 里，但 area() 执行的是子类的重写版本
        assertEquals("圆形 area=" + Math.PI, new Circle(1.0).describe());
        assertEquals("矩形 area=12.0", new Rectangle(3, 4).describe());
    }

    @Test
    void shapeRejectsEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> new Shape(""));
        assertThrows(IllegalArgumentException.class, () -> new Shape(null));
    }
}
