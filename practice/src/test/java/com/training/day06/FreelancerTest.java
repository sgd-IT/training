package com.training.day06;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FreelancerTest {

    @Test
    void monthlyPayIsRateTimesHours() {
        // 200 × 40 = 8000
        assertEquals(8000, new Freelancer("李四", 200, 40).monthlyPay(), 0.0001);
    }

    @Test
    void rejectsEmptyName() {
        assertThrows(IllegalArgumentException.class, () -> new Freelancer("", 200, 40));
        assertThrows(IllegalArgumentException.class, () -> new Freelancer(null, 200, 40));
    }

    @Test
    void rejectsNegativeRate() {
        assertThrows(IllegalArgumentException.class, () -> new Freelancer("李四", -1, 40));
    }

    @Test
    void rejectsNegativeHours() {
        assertThrows(IllegalArgumentException.class, () -> new Freelancer("李四", 200, -1));
    }

    @Test
    void usableThroughInterfaceType() {
        // Freelancer 没有父类，纯粹靠 implements —— 接口能脱离继承单独使用
        Payable p = new Freelancer("李四", 200, 40);
        assertEquals(8000, p.monthlyPay(), 0.0001);
    }

    @Test
    void gettersWork() {
        Freelancer f = new Freelancer("李四", 200, 40);
        assertEquals("李四", f.getName());
        assertEquals(200, f.getHourlyRate(), 0.0001);
        assertEquals(40, f.getHours());
    }

    @Test
    void acceptsZeroRateAndZeroHours() {
        // 边界：0 是允许的（校验是 < 0 才抛，不是 <= 0）
        assertEquals(0, new Freelancer("李四", 0, 0).monthlyPay(), 0.0001);
    }
}
