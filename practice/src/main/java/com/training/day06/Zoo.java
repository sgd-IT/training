package com.training.day06;

/**
 * Day 6 - Q5: Zoo —— 用抽象类型当参数（你来补全，今天最综合的一题）
 *
 * 这个方法接收一个 Animal 数组。注意参数类型是 Animal（抽象类），
 * 意味着传进来的可以是 Dog、Cat、Duck……任何 Animal 的子类。
 * 这就是 Day 5 学的多态，用到方法签名上。
 *
 * 你要做的事：
 *   把数组里每只动物的 describe() 收集起来，用 ", " 连接成一个字符串。
 *
 *   例：{ new Dog("旺财"), new Cat("咪咪") }
 *       → "旺财 says 汪汪, 咪咪 says 喵喵"
 *
 * 空数组 → 返回空字符串 ""（注意：不能返回 "null"，也不能多出一个逗号）
 *
 * 提示（Day 1 学过的老工具）：
 *   StringBuilder 的 append + 一个"不是第一个就加逗号"的判断
 *   或者用 String.join(", ", ...) —— 两种都行，你自己选
 *
 * 踩坑预警：如果写成"每次 append 完都补一个逗号"，最后会多一个尾逗号，
 * 测试 emptyArrayGivesEmptyString 和 describesMixedAnimals 都会挂。
 *
 * 规则：先在文件顶部写 3-5 行伪代码注释，再写实现。
 */
public class Zoo {

    /** 把一群动物的自我介绍拼成一句话 */
    public static String describeAll(Animal[] animals) {
        // TODO: 遍历 animals，用 ", " 连接每只的 describe()
        throw new UnsupportedOperationException("Day 6 - Q5 待实现");
    }
}
