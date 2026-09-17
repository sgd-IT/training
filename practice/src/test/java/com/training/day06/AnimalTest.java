package com.training.day06;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AnimalTest {

    @Test
    void dogSpeaks() {
        assertEquals("汪汪", new Dog("旺财").speak());
    }

    @Test
    void catSpeaks() {
        assertEquals("喵喵", new Cat("咪咪").speak());
    }

    @Test
    void describeUsesPolymorphicSpeak() {
        // describe() 定义在 Animal 里，但 speak() 执行的是子类实现（Day 5 的多态）
        assertEquals("旺财 says 汪汪", new Dog("旺财").describe());
        assertEquals("咪咪 says 喵喵", new Cat("咪咪").describe());
    }

    @Test
    void inheritsNameValidationFromAnimal() {
        // 校验在 Animal 构造器里——子类绕不过（Day 5 的 super 校验链）
        assertThrows(IllegalArgumentException.class, () -> new Dog(""));
        assertThrows(IllegalArgumentException.class, () -> new Dog(null));
        assertThrows(IllegalArgumentException.class, () -> new Cat(""));
        assertThrows(IllegalArgumentException.class, () -> new Cat(null));
    }

    @Test
    void getNameInheritedFromAnimal() {
        assertEquals("旺财", new Dog("旺财").getName());
        assertEquals("咪咪", new Cat("咪咪").getName());
    }
}
