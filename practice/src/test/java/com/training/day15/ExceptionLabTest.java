package com.training.day15;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Day 15 测试 —— 契约都在这里，先读断言消息再动手。
 * <p>
 * 12 个随 TODO 变绿；最后 5 个是**天然绿**（考概念，不用写实现）。
 */
class ExceptionLabTest {

    // ═══════════════════════════════════════════════════════════
    // Q1 · try-with-resources
    // ═══════════════════════════════════════════════════════════

    @Test
    void bodyExceptionIsThePrimaryOne() {
        Throwable t = ExceptionLab.bodyFailsAndCloseFails();
        assertNotNull(t, "要把主异常 return 出来");
        assertEquals("body failed", t.getMessage(),
                "body 抛的才是主异常 —— close 的不能把它覆盖掉");
    }

    @Test
    void closeFailureBecomesSuppressed() {
        Throwable t = ExceptionLab.bodyFailsAndCloseFails();
        assertEquals(1, t.getSuppressed().length,
                "close 抛的异常不能丢，要挂在主异常的 suppressed 上 —— 这叫「两个都不丢」");
    }

    @Test
    void suppressedCarriesTheCloseFailure() {
        Throwable t = ExceptionLab.bodyFailsAndCloseFails();
        Throwable[] suppressed = t.getSuppressed();
        assertTrue(suppressed.length == 1, "suppressed 应该正好 1 个");
        assertTrue(suppressed[0].getMessage().contains("close failed"),
                "suppressed[0] 就是 close 里抛的那个异常，实际是：" + suppressed[0].getMessage());
    }

    // ═══════════════════════════════════════════════════════════
    // Q2 · 关闭顺序
    // ═══════════════════════════════════════════════════════════

    @Test
    void resourcesCloseInReverseOrder() {
        assertEquals(List.of("second.close", "first.close"), ExceptionLab.closeOrder(),
                "多个资源的关闭顺序 —— 先预测，再跑");
    }

    @Test
    void bothResourcesWereClosed() {
        assertEquals(2, ExceptionLab.closeOrder().size(), "两个都要关，别漏");
    }

    // ═══════════════════════════════════════════════════════════
    // Q3 · 异常链
    // ═══════════════════════════════════════════════════════════

    @Test
    void wrapKeepsTheCauseObject() {
        IllegalStateException root = new IllegalStateException("db down");
        ExceptionLab.ServiceException se = ExceptionLab.wrap(root);
        assertSame(root, se.getCause(),
                "cause 必须是原来那个对象本身，不是复制一个 —— assertSame 不是 assertEquals");
    }

    @Test
    void wrapMessageKeepsTheOriginal() {
        IllegalStateException root = new IllegalStateException("db down");
        String message = ExceptionLab.wrap(root).getMessage();
        assertEquals("service failed: db down", message,
                "包装后原 message 必须带上，否则日志里只剩一句没用的 \"service failed\"");
    }

    @Test
    void rootCauseWalksToTheBottom() {
        IllegalStateException root = new IllegalStateException("root");
        Throwable wrapped = new RuntimeException("mid", new IllegalStateException("outer", root));
        assertSame(root, ExceptionLab.rootCause(wrapped),
                "三层包装，要一路走到最底下那层");
    }

    @Test
    void rootCauseOfPlainThrowableIsItself() {
        IllegalStateException alone = new IllegalStateException("alone");
        assertSame(alone, ExceptionLab.rootCause(alone),
                "没有 cause 时，根因就是它自己 —— 别返回 null");
    }

    // ═══════════════════════════════════════════════════════════
    // Q4 · 手写 try-with-resources（今天真正的考题）
    // ═══════════════════════════════════════════════════════════

    @Test
    void manualVersionKeepsBodyExceptionAsPrimary() {
        Throwable t = ExceptionLab.bodyFailsAndCloseFailsWithoutTwr();
        assertEquals("body failed", t.getMessage(),
                "如果你拿到的是 \"body-close close failed\"，说明 close 把主异常覆盖了");
    }

    @Test
    void manualVersionAlsoSuppressesTheCloseFailure() {
        Throwable t = ExceptionLab.bodyFailsAndCloseFailsWithoutTwr();
        assertEquals(1, t.getSuppressed().length,
                "手写版也要把 close 的异常挂成 suppressed");
        assertTrue(t.getSuppressed()[0].getMessage().contains("close failed"),
                "挂上去的必须是 close 那个异常");
    }

    @Test
    void manualAndTwrHaveTheSameShape() {
        Throwable a = ExceptionLab.bodyFailsAndCloseFails();
        Throwable b = ExceptionLab.bodyFailsAndCloseFailsWithoutTwr();
        assertEquals(a.getMessage(), b.getMessage(), "两个版本的主异常 message 必须一致");
        assertEquals(a.getSuppressed().length, b.getSuppressed().length,
                "两个版本的 suppressed 个数必须一致 —— 说明你手写的展开逻辑对上了");
    }

    // ═══════════════════════════════════════════════════════════
    // 天然绿 · 不用写 TODO 就能过，考的是概念
    // ═══════════════════════════════════════════════════════════

    @Test
    void throwsIsOnlyMetadataNotRuntimeEnforcement() throws Exception {
        int declared = ExceptionLab.class.getMethod("declaredThrows").getExceptionTypes().length;
        assertEquals(2, declared,
                "throws 子句只写在 class 文件的 Exceptions 属性里（IOException + InterruptedException），"
                        + "JVM 运行时不拿它做任何检查");
    }

    @Test
    void checkedExceptionIsEnforcedByTheCompiler() {
        assertThrows(IOException.class, ExceptionLab::declaredThrows,
                "能拿到 IOException，但重点是：调用处不 try/catch 就编译不过 —— 那是编译器的强制");
    }

    @Test
    void runtimeExceptionsNeedNoThrowsClause() {
        assertThrows(IllegalArgumentException.class, () -> {
            throw new IllegalArgumentException("unchecked");
        }, "unchecked 不需要 throws 声明，编译器也不检查");
    }

    @Test
    void errorIsNotAnException() {
        assertFalse(Exception.class.isAssignableFrom(Error.class),
                "Error 和 Exception 是 Throwable 下平行的两条分支");
        assertFalse(Exception.class.isAssignableFrom(AssertionError.class),
                "AssertionError 属于 Error 那条分支，不是 Exception");
        assertTrue(Throwable.class.isAssignableFrom(Error.class));
    }

    @Test
    void withoutCauseTheChainIsBroken() {
        assertNull(new ExceptionLab.ServiceException("no cause").getCause(),
                "只传 message 的构造器 → cause = null → 根因永久丢失。这就是 Q3 要考的东西");
    }
}
