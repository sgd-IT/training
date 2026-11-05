package com.training.day16;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Day 16 测试 —— 契约都在断言消息里，先读再动手。
 * <p>
 * 8 个随 TODO 变绿；最后 4 个是**天然绿**（不用写实现，考概念）。
 */
class LambdaLabTest {

    // ═══════════════════════════════════════════════════════════
    // Q1 · 函数式接口与 lambda
    // ═══════════════════════════════════════════════════════════

    @Test
    void doublerDoublesTheInput() {
        assertEquals(6, LambdaLab.doubler().apply(3));
        assertEquals(0, LambdaLab.doubler().apply(0));
        assertEquals(-4, LambdaLab.doubler().apply(-2));
    }

    @Test
    void doublerIsARealLambdaNotAnAnonymousClass() {
        String name = LambdaLab.doubler().getClass().getName();
        assertTrue(name.contains("$$Lambda"),
                "必须是真的 lambda（运行时生成的 $$Lambda 类），实际类名：" + name);
        assertFalse(name.contains("$1"),
                "匿名内部类的类名是 Xxx$1，说明你用了匿名类而不是 lambda");
    }

    // ═══════════════════════════════════════════════════════════
    // Q2 · 变量捕获
    // ═══════════════════════════════════════════════════════════

    @Test
    void counterCountsUpFromZero() {
        IntSupplier c = LambdaLab.counter();
        assertEquals(0, c.getAsInt(), "第一次");
        assertEquals(1, c.getAsInt(), "第二次");
        assertEquals(2, c.getAsInt(), "第三次");
    }

    @Test
    void twoCountersDoNotShareState() {
        IntSupplier a = LambdaLab.counter();
        IntSupplier b = LambdaLab.counter();
        assertEquals(0, a.getAsInt());
        assertEquals(1, a.getAsInt());
        assertEquals(0, b.getAsInt(),
                "每次调用 counter() 都必须是独立的一份状态 —— 用 static 或字段就会在这里红");
    }

    // ═══════════════════════════════════════════════════════════
    // Q3 · 组合顺序
    // ═══════════════════════════════════════════════════════════

    @Test
    void chainAppliesFirstThenSecond() {
        assertEquals(20, LambdaLab.chain(v -> v + 1, v -> v * 10).apply(1),
                "(1+1)*10 = 20 —— 先 first 再 second");
    }

    @Test
    void chainOrderIsNotGuessable() {
        assertEquals(11, LambdaLab.chain(v -> v * 10, v -> v + 1).apply(1),
                "1*10+1 = 11 —— 顺序反了这里就红，所以别猜");
    }

    // ═══════════════════════════════════════════════════════════
    // Q4 · lambda 的 this
    // ═══════════════════════════════════════════════════════════

    @Test
    void lambdaThisIsTheOuterObject() {
        LambdaLab lab = new LambdaLab();
        assertEquals(LambdaLab.class.getName(), lab.lambdaThis().get(),
                "lambda 没有自己的 this，所以 this 就是外层对象");
    }

    @Test
    void anonymousThisIsTheAnonymousClass() {
        LambdaLab lab = new LambdaLab();
        String name = lab.anonymousThis().get();
        assertTrue(name.startsWith(LambdaLab.class.getName() + "$"),
                "匿名内部类里的 this 是匿名类自己，实际：" + name);
    }

    // ═══════════════════════════════════════════════════════════
    // 天然绿 · 不用写 TODO 就能过，考的是概念
    // ═══════════════════════════════════════════════════════════

    @Test
    void functionalInterfaceMeansExactlyOneAbstractMethod() {
        long abstractCount = Arrays.stream(LambdaLab.Transformer.class.getDeclaredMethods())
                .filter(m -> java.lang.reflect.Modifier.isAbstract(m.getModifiers()))
                .count();
        assertEquals(1, abstractCount,
                "函数式接口的判定标准只有一条：有且只有一个抽象方法。"
                        + "注解 @FunctionalInterface 只是编译期检查，不是判定依据");
    }

    @Test
    void predicateAndShortCircuits() {
        List<String> visited = new ArrayList<>();
        Predicate<String> left = s -> {
            visited.add("left");
            return false;
        };
        Predicate<String> right = s -> {
            visited.add("right");
            return true;
        };
        assertFalse(left.and(right).test("x"));
        assertEquals(List.of("left"), visited,
                "and 的左边已经是 false，右边【根本不会执行】—— 短路");
    }

    @Test
    void methodReferenceEqualsLambda() {
        Function<String, String> byRef = String::trim;
        Function<String, String> byLambda = s -> s.trim();
        assertEquals(byLambda.apply("  hi  "), byRef.apply("  hi  "),
                "方法引用和 lambda 是同一件事的两种写法 —— 都指向同一个函数式接口");
        assertEquals("hi", byRef.apply("  hi  "));
    }

    @Test
    void composeIsTheMirrorOfAndThen() {
        Function<Integer, Integer> f = x -> x + 1;
        Function<Integer, Integer> g = x -> x * 10;
        assertEquals(20, f.andThen(g).apply(1), "f.andThen(g) = g(f(1)) = 20");
        assertEquals(f.andThen(g).apply(1), g.compose(f).apply(1),
                "g.compose(f) 和 f.andThen(g) 完全等价 —— 一个是给读代码的人看的顺序，另一个是给写代码的人看的");
    }
}
