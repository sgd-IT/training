package com.training.day06;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ZooTest {

    @Test
    void describesMixedAnimals() {
        Animal[] animals = { new Dog("旺财"), new Cat("咪咪") };
        assertEquals("旺财 says 汪汪, 咪咪 says 喵喵", Zoo.describeAll(animals));
    }

    @Test
    void worksWithDuckToo() {
        // Duck 也是 Animal —— 参数类型是抽象类，实物可以是任何子类（多态）
        Animal[] animals = { new Duck("唐老鸭") };
        assertEquals("唐老鸭 says 嘎嘎", Zoo.describeAll(animals));
    }

    @Test
    void singleAnimalHasNoTrailingComma() {
        assertEquals("旺财 says 汪汪", Zoo.describeAll(new Animal[] { new Dog("旺财") }));
    }

    @Test
    void emptyArrayGivesEmptyString() {
        assertEquals("", Zoo.describeAll(new Animal[0]));
    }
}
