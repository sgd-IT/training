package com.training.day06;

/**
 * Day 6 - Q2: Cat 继承抽象类 Animal（你来补全）
 *
 * 和 Q1 一模一样的套路，自己写，别看 Dog。
 *   speak() 返回 "喵喵"
 *
 * 规则：先在文件顶部写 3-5 行伪代码注释，再写实现。
 */
public class Cat extends Animal {

    public Cat(String name) {
        super(name);
    }

    @Override
    public String speak() {
        // TODO: 改成 return "喵喵";
        //直接返回就好
        return "喵喵";
    }
}
