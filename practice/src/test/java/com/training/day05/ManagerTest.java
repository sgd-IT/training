package com.training.day05;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ManagerTest {

    @Test
    void annualSalaryIncludesBonus() {
        // 基本年薪 10000×12 = 120000，加奖金 20000
        Manager m = new Manager("王五", 10000, 20000);
        assertEquals(140000, m.annualSalary(), 0.0001);
    }

    @Test
    void inheritsNameAndSalaryFromEmployee() {
        Manager m = new Manager("王五", 10000, 20000);
        assertEquals("王五", m.getName());
        assertEquals(10000, m.getSalary(), 0.0001);
    }

    @Test
    void bonusGetterWorks() {
        assertEquals(20000, new Manager("王五", 10000, 20000).getBonus(), 0.0001);
    }

    @Test
    void rejectsNegativeBonus() {
        assertThrows(IllegalArgumentException.class, () -> new Manager("王五", 10000, -1));
    }

    @Test
    void inheritsNameValidation() {
        // name 的校验在 Employee 构造器里——子类拦不住，super 会拦
        assertThrows(IllegalArgumentException.class, () -> new Manager("", 10000, 0));
        assertThrows(IllegalArgumentException.class, () -> new Manager(null, 10000, 0));
    }

    @Test
    void inheritsSalaryValidation() {
        assertThrows(IllegalArgumentException.class, () -> new Manager("王五", -5000, 0));
    }
}
