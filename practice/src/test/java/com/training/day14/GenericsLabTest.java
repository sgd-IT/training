package com.training.day14;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Day 14 - 测试：GenericsLab + 类型擦除的证明
 * <p>
 * 前 11 个测 TODO；后面 4 个是**天然绿**，专门展示擦除的三个后果。
 */
class GenericsLabTest {

    @Test
    void pairHoldsBothValues() {
        GenericsLab.Pair<String, Integer> p = new GenericsLab.Pair<>("a", 1);
        assertEquals("a", p.key(), "访问器没有 get 前缀 —— 是 key() 不是 getKey()");
        assertEquals(1, p.value());
    }

    @Test
    void pairEqualsIsGeneratedByRecord() {
        assertEquals(new GenericsLab.Pair<>("a", 1), new GenericsLab.Pair<>("a", 1),
                "record 自动生成的 equals 按字段逐个比较 —— Day 7 的契约一次写对");
        assertNotEquals(new GenericsLab.Pair<>("a", 1), new GenericsLab.Pair<>("a", 2));
        assertNotEquals(new GenericsLab.Pair<>("a", 1), new GenericsLab.Pair<>("b", 1));
    }

    @Test
    void pairHashCodeIsGeneratedByRecord() {
        assertEquals(new GenericsLab.Pair<>("a", 1).hashCode(),
                new GenericsLab.Pair<>("a", 1).hashCode(),
                "equals 相等 → hashCode 必须相等（Day 7 契约）");
    }

    @Test
    void pairToStringIsReadable() {
        assertEquals("Pair[key=a, value=1]", new GenericsLab.Pair<>("a", 1).toString(),
                "record 的 toString 格式固定：类名[字段=值, 字段=值]");
    }

    @Test
    void pairIsARecord() {
        assertTrue(GenericsLab.Pair.class.isRecord(), "必须真的是 record，不是手写的普通类");
        assertEquals(2, GenericsLab.Pair.class.getRecordComponents().length,
                "两个 record 组件：key 和 value");
    }

    @Test
    void maxOfIntegers() {
        assertEquals(9, GenericsLab.maxOf(List.of(5, 9, 1, 3)));
    }

    @Test
    void maxOfStringsUsesNaturalOrder() {
        assertEquals("pear", GenericsLab.maxOf(List.of("apple", "pear", "fig")),
                "String 实现了 Comparable<String>，字典序最大的是 pear");
    }

    @Test
    void maxOfSingleElementAndEmpty() {
        assertEquals(7, GenericsLab.maxOf(List.of(7)));
        assertNull(GenericsLab.maxOf(List.<Integer>of()), "空列表返回 null —— 题目这么定义的");
    }

    @Test
    void sumOfAcceptsAnyNumberList() {
        assertEquals(6.0, GenericsLab.sumOf(List.of(1, 2, 3)), 0.0001,
                "List<Integer> 能传进来 —— 这就是 ? extends Number 的价值");
        assertEquals(4.5, GenericsLab.sumOf(List.of(1.5, 3.0)), 0.0001,
                "List<Double> 也能传进来，同一个方法");
        assertEquals(0.0, GenericsLab.sumOf(List.<Integer>of()), 0.0001);
    }

    @Test
    void fillWithAcceptsIntegerNumberAndObjectLists() {
        List<Integer> ints = new ArrayList<>();
        GenericsLab.fillWith(ints, 3);
        assertEquals(List.of(0, 1, 2), ints);

        List<Number> numbers = new ArrayList<>();
        GenericsLab.fillWith(numbers, 2);
        assertEquals(List.of(0, 1), numbers, "List<Number> 也能接收 —— List<Integer> 不行");

        List<Object> objects = new ArrayList<>();
        GenericsLab.fillWith(objects, 1);
        assertEquals(List.of(0), objects, "List<Object> 是 super 链的最顶端");
    }

    @Test
    void fillWithZeroAddsNothing() {
        List<Integer> ints = new ArrayList<>();
        GenericsLab.fillWith(ints, 0);
        assertEquals(List.of(), ints);
    }

    // ═══════════════════════════════════════════════════════════
    // 天然绿：类型擦除的三个后果
    // ═══════════════════════════════════════════════════════════

    @Test
    void erasureOneTypeAtRuntime() {
        assertEquals(new ArrayList<String>().getClass(), new ArrayList<Integer>().getClass(),
                "List<String> 和 List<Integer> 在运行时是同一个类 —— 泛型信息被擦掉了");
        assertEquals(ArrayList.class, new ArrayList<String>().getClass());
    }

    @Test
    void erasureArrayCreationIsIllegal() {
        // 这一段是【编译期】事实：下面这行如果写出来，编译就过不了。
        //     T[] arr = new T[10];
        // 所以库里都是 (T[]) new Object[n] + @SuppressWarnings，Day 9 的 MiniList 就是这么写的。
        assertTrue(true, "这一条考的是编译器行为，不是运行时行为");
    }

    @Test
    @SuppressWarnings({"unchecked", "rawtypes"})
    void erasureLetsYouPolluteAListWithoutTellingTheCompiler() {
        List<String> strings = new ArrayList<>();
        List raw = strings;          // 通过原生类型绕过编译期检查
        raw.add(123);                // 塞进一个 Integer —— 编译器被瞒过了

        assertEquals(1, strings.size(), "size 照样是 1，说明东西真进去了");
        assertThrows(ClassCastException.class, () -> {
            String s = strings.get(0);   // 取出来时才炸：编译器插了一句 (String) 强转
            System.out.println(s);
        }, "擦除的代价：类型错误从编译期被推到了运行时");
    }

    @Test
    void erasureInstanceofCannotUseTypeParameter() {
        List<String> strings = new ArrayList<>();
        // if (strings instanceof List<Integer>) { }   ← 编译错误：instanceof 的类型必须 reifiable
        assertTrue(strings instanceof List, "只能判断是不是 List，判断不了它装什么");
    }
}