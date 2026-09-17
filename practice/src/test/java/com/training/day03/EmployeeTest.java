package com.training.day03;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EmployeeTest {

    @Test
    void constructorAssignsFields() {
        Employee e = new Employee("张三", 8000);
        assertEquals("张三", e.getName());
        assertEquals(8000, e.getSalary(), 0.0001);
    }

    @Test
    void shortConstructorDelegatesWithDefaultSalary() {
        Employee e = new Employee("李四");
        assertEquals("李四", e.getName());
        assertEquals(0, e.getSalary(), 0.0001); // this(name, 0) 委托的默认工资
    }

    @Test
    void constructorRejectsEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> new Employee("", 8000));
        assertThrows(IllegalArgumentException.class, () -> new Employee(null, 8000));
    }

    @Test
    void constructorRejectsNegativeSalary() {
        assertThrows(IllegalArgumentException.class, () -> new Employee("张三", -1));
    }

    @Test
    void payRaiseWorks() {
        Employee e = new Employee("张三", 8000);
        e.payRaise(500);
        assertEquals(8500, e.getSalary(), 0.0001);
    }

    @Test
    void payRaiseRejectsNonPositive() {
        Employee e = new Employee("张三", 8000);
        assertThrows(IllegalArgumentException.class, () -> e.payRaise(0));
        assertThrows(IllegalArgumentException.class, () -> e.payRaise(-100));
        assertEquals(8000, e.getSalary(), 0.0001); // 失败后工资不能变
    }

    @Test
    void annualSalaryDefaultIsTwelveMonths() {
        Employee e = new Employee("张三", 8000);
        assertEquals(96000, e.annualSalary(), 0.0001); // 8000 × 12
    }

    @Test
    void annualSalaryCustomMonths() {
        Employee e = new Employee("张三", 8000);
        assertEquals(40000, e.annualSalary(5), 0.0001);
    }

    @Test
    void annualSalaryRejectsInvalidMonths() {
        Employee e = new Employee("张三", 8000);
        assertThrows(IllegalArgumentException.class, () -> e.annualSalary(0));
        assertThrows(IllegalArgumentException.class, () -> e.annualSalary(-3));
    }
}
