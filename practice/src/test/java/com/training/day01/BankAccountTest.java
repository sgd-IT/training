package com.training.day01;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BankAccountTest {

    @Test
    void openAccount() {
        BankAccount a = new BankAccount("Tom", 100);
        assertEquals("Tom", a.getOwner());
        assertEquals(100, a.getBalance(), 1e-9);
    }

    @Test
    void depositIncreasesBalance() {
        BankAccount a = new BankAccount("Tom", 100);
        a.deposit(50);
        assertEquals(150, a.getBalance(), 1e-9);
    }

    @Test
    void withdrawDecreasesBalance() {
        BankAccount a = new BankAccount("Tom", 100);
        a.withdraw(40);
        assertEquals(60, a.getBalance(), 1e-9);
    }

    @Test
    void withdrawMoreThanBalanceFails() {
        BankAccount a = new BankAccount("Tom", 100);
        assertThrows(IllegalStateException.class, () -> a.withdraw(150));
        assertEquals(100, a.getBalance(), 1e-9);
    }

    @Test
    void invalidDepositFails() {
        BankAccount a = new BankAccount("Tom", 100);
        assertThrows(IllegalArgumentException.class, () -> a.deposit(0));
        assertThrows(IllegalArgumentException.class, () -> a.deposit(-5));
    }

    @Test
    void invalidOpenFails() {
        assertThrows(IllegalArgumentException.class, () -> new BankAccount("", 100));
        assertThrows(IllegalArgumentException.class, () -> new BankAccount(null, 100));
        assertThrows(IllegalArgumentException.class, () -> new BankAccount("Tom", -1));
    }
}
