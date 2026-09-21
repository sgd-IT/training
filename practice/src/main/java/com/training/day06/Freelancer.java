package com.training.day06;

/**
 * Day 6 - Q4: Freelancer 纯接口实现（你来补全——这题有真活干）
 * <p>
 * 背景：Freelancer（自由职业者）不是 Employee 的子类，它没有父类。
 * 它只是"能被支付"——所以 implements Payable。
 * <p>
 * 你要做三件事，全都是 Day 3 的老套路（先校验后赋值）：
 * <p>
 * 1. 构造器里补三个校验（顺序随便，但都在赋值之前）：
 * name 为 null 或空  → IllegalArgumentException("姓名不能为空")
 * hourlyRate < 0      → IllegalArgumentException("时薪不能为负")
 * hours < 0           → IllegalArgumentException("工时不能为负")
 * 注意：三个字段已经是 final 了，所以校验必须在赋值前做完。
 * <p>
 * 2. monthlyPay() 返回 hourlyRate * hours
 * <p>
 * 3. getter 已经写好了，不用动
 * <p>
 * 规则：先在文件顶部写 3-5 行伪代码注释，再写实现。
 */
public class Freelancer implements Payable {

    private final String name;
    private final double hourlyRate;
    private final int hours;

    public Freelancer(String name, double hourlyRate, int hours) {
        // TODO: 三个校验（name 非空、hourlyRate >= 0、hours >= 0）
        if (name == null || name.isEmpty())
            throw new IllegalArgumentException("姓名不能为空");
        if (hourlyRate < 0)
            throw new IllegalArgumentException("时薪不能为负");
        if (hours < 0)
            throw new IllegalArgumentException("工时不能为负");
        this.name = name;
        this.hourlyRate = hourlyRate;
        this.hours = hours;
    }

    public String getName() {
        return name;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public int getHours() {
        return hours;
    }

    @Override
    public double monthlyPay() {
        return hourlyRate * hours;
    }
}
