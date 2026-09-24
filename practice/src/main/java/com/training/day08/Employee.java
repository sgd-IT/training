package com.training.day08;

/**
 * Day 8 - Q1: 错题重写（Day 3 原题升级版）—— 构造器委托
 *
 * ═══════════════════════════════════════════════════════════
 * 你在 Day 3 犯的错（当天测试没抓到，因为测不出"代码重复"）
 * ═══════════════════════════════════════════════════════════
 *   Employee(String name) 里把 name/salary 的校验和赋值**复制了一遍**，
 *   而不是委托给全参构造器。
 *
 *   当时的结论：测试抓不到，只能靠代码审查。
 *
 *   ⚠️ 这一题把那个错误变成了**测试能抓到的**：
 *      全参构造器里多了"规范化"（trim），
 *      谁复制逻辑、谁就会漏掉规范化 —— 一测就红。
 *
 * ═══════════════════════════════════════════════════════════
 * 规则：只有全参构造器做校验和规范化，另外两个必须复用它
 * ═══════════════════════════════════════════════════════════
 *
 *   全参 Employee(String name, int salary, String dept)  —— 已实现，先读懂它：
 *     · name 为 null 或"全是空格"  → 抛 IllegalArgumentException
 *     · salary < 0                → 抛 IllegalArgumentException
 *     · name  → trim
 *     · dept  → null 或"全是空格" 时变成 "未分配"，否则 trim
 *
 *   TODO Q1-1: 两参 Employee(String name, int salary) —— 工资有，部门默认
 *   TODO Q1-2: 单参 Employee(String name)             —— 工资默认 0，部门默认
 *
 *   实现方式提示： Day 3 学过的 this(...) 委托。**一行就够。**
 *   不要在这里写任何 if —— 写了就说明你在复制，而不是委托。
 *
 *   TODO Q1-3: toString() → "王五(未分配) 薪水=0"
 *              格式： name + "(" + dept + ") 薪水=" + salary
 *
 * ⚠️ 先预测，再跑：
 *     // 预测：new Employee("  张三  ").getName() → "____"
 *     //       （想清楚：单参构造器要不要自己 trim？还是委托就够了？）
 */
public class Employee {

    private final String name;
    private final int salary;
    private final String dept;

    /** 全参构造器：唯一做校验和规范化的地方 */
    public Employee(String name, int salary, String dept) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name 不能为空");
        }
        if (salary < 0) {
            throw new IllegalArgumentException("salary 不能为负: " + salary);
        }
        this.name = name.trim();
        this.salary = salary;
        this.dept = (dept == null || dept.trim().isEmpty()) ? "未分配" : dept.trim();
    }

    public Employee(String name, int salary) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name 不能为空");
        }
        if (salary < 0) {
            throw new IllegalArgumentException("salary 不能为负: " + salary);
        }
        this.name=name.trim();
        this.salary = salary;
        this.dept = "未分配";
    }


    public Employee(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name 不能为空");
        }
        this.name = name.trim();
        this.salary = 0;
        this.dept = "未分配";
    }

    public String getName() {
        return name;
    }

    public int getSalary() {
        return salary;
    }

    public String getDept() {
        return dept;
    }


    @Override
    public String toString() {
        return name + "(" + dept + ") 薪水=" + salary;
    }
}