package com.training.day06;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class InterfaceTest {

    @Test
    void duckSpeaks() {
        assertEquals("嘎嘎", new Duck("唐老鸭").speak());
    }

    @Test
    void duckSwims() {
        assertEquals("划水", new Duck("唐老鸭").swim());
    }

    @Test
    void duckFlies() {
        assertEquals("扑棱翅膀", new Duck("唐老鸭").fly());
    }

    @Test
    void interfaceTypeVariableDispatchesToImplementation() {
        // Day 5 的规则在这里照样成立：
        //   javac 记录 Swimmable.swim()（声明类型是接口）
        //   JVM 按实物类型 Duck 执行
        Swimmable s = new Duck("唐老鸭");
        assertEquals("划水", s.swim());
    }

    @Test
    void sameObjectIsBothAnimalAndSwimmable() {
        // 同一个对象，两个视角——这是接口最核心的价值
        Duck d = new Duck("唐老鸭");
        Animal a = d;
        Swimmable s = d;

        assertEquals("嘎嘎", a.speak()); // 从"动物"的视角
        assertEquals("划水", s.swim());  // 从"会游泳"的视角
    }

    @Test
    void describeWorksOnDuckToo() {
        assertEquals("唐老鸭 says 嘎嘎", new Duck("唐老鸭").describe());
    }
}
