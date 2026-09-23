package com.training.day08;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Day 8 - Q1 测试：Employee（构造器委托 + 规范化） */
class EmployeeTest {

    @Test
    void fullConstructorNormalizes() {
        Employee e = new Employee("  张三  ", 8000, "  研发部  ");
        assertEquals("张三", e.getName(), "名字应该被 trim");
        assertEquals(8000, e.getSalary());
        assertEquals("研发部", e.getDept(), "部门应该被 trim");
    }

    @Test
    void blankOrNullDeptBecomesDefault() {
        assertEquals("未分配", new Employee("张三", 8000, "   ").getDept());
        assertEquals("未分配", new Employee("张三", 8000, null).getDept());
    }

    @Test
    void twoArgConstructor_reusesNormalization() {
        // ⚠️ 这一条专抓"复制校验"：如果你把校验/赋值抄了一遍却漏了 trim，这里红
        Employee e = new Employee("  李四  ", 9000);
        assertEquals("李四", e.getName(), "两参构造器也必须 trim —— 你委托给全参构造器了吗？");
        assertEquals(9000, e.getSalary());
        assertEquals("未分配", e.getDept(), "部门应该走默认值");
    }

    @Test
    void oneArgConstructor_reusesNormalizationAndValidation() {
        Employee e = new Employee("  王五  ");
        assertEquals("王五", e.getName(), "单参构造器也必须 trim");
        assertEquals(0, e.getSalary(), "默认薪水 0");
        assertEquals("未分配", e.getDept(), "默认部门 未分配");

        // ⚠️ 关键：'全是空格' 的校验只在全参构造器里 —— 只有委托才能共享它
        assertThrows(IllegalArgumentException.class, () -> new Employee("   "),
                "全是空格的名字，应该和 null 一样被拒绝（只有委托才会共享这条规则）");
        assertThrows(IllegalArgumentException.class, () -> new Employee(null));
        assertThrows(IllegalArgumentException.class, () -> new Employee(null, 100));
    }

    @Test
    void toStringFormat() {
        assertEquals("王五(未分配) 薪水=0", new Employee("王五").toString());
        assertEquals("李四(研发部) 薪水=9000", new Employee("李四", 9000, "研发部").toString());
        assertEquals("张三(未分配) 薪水=8000", new Employee("  张三  ", 8000).toString());
    }
}