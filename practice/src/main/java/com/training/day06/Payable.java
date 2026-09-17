package com.training.day06;

/**
 * Day 6 - 接口教科书（已实现）
 *
 * Payable = "可支付的东西"，任何实现它的类都必须给出 monthlyPay()。
 *
 * 这个接口专门用来演示：接口可以脱离继承单独使用。
 * Freelancer（自由职业者）不是什么 Employee 的子类，它没有父类——
 * 但它 implements Payable，于是"能被支付"这件事就成立了。
 *
 * 这就是接口的价值：把"能力"从"继承体系"里解放出来。
 */
public interface Payable {

    /** 月收入 */
    double monthlyPay();
}
