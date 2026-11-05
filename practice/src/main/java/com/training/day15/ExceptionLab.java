package com.training.day15;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Day 15 - Q1~Q4: ExceptionLab —— 异常**机制**（不是异常 API）
 * <p>
 * ═══════════════════════════════════════════════════════════
 * ★ 必读：今天开工前先把这一整段读完（考点全在这里）
 * ═══════════════════════════════════════════════════════════
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 一、Throwable 家族 —— 三条分支，别记成一条
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   Throwable
 *   ├── Error                     ← JVM 层面，你不该 catch（OutOfMemoryError / StackOverflowError）
 *   └── Exception
 *       ├── RuntimeException      ← unchecked，编译器不管
 *       └── 其他（IOException 等）  ← checked，编译器**强制**你处理
 * </pre>
 * <b>Error 和 Exception 是平行的两条分支，Error 不是 Exception 的子类。</b>
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 二、★ checked 的本质：只是编译器的检查
 * ═══════════════════════════════════════════════════════════
 * JVM 运行时**完全不区分** checked 和 unchecked —— 两者在字节码里都是
 * {@code athrow} 指令，都是往上抛栈帧。
 * <p>
 * checked 的"强制"只存在于**编译期**：编译器看方法签名上的 {@code throws} 子句，
 * 你调了它又不 try/catch 就报错。所以：
 * <pre>
 *   throws 子句 = class 文件里的 Exceptions 属性 = 给编译器看的元数据
 * </pre>
 * 反射能读到它（{@code Method.getExceptionTypes()}），JVM 却不拿它做任何检查。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 三、★ try-with-resources 到底编译成了什么（今天的核心）
 * ═══════════════════════════════════════════════════════════
 * 你写的：
 * <pre>
 *   try (R r = new R()) {
 *       body();
 *   }
 * </pre>
 * 编译器展开成（伪代码）：
 * <pre>
 *   R r = new R();
 *   Throwable primary = null;
 *   try {
 *       body();
 *   } catch (Throwable t) {
 *       primary = t;
 *       throw t;
 *   } finally {
 *       if (r != null) {
 *           if (primary != null) {
 *               try { r.close(); }
 *               catch (Throwable c) { primary.addSuppressed(c); }   ← 关键
 *           } else {
 *               r.close();
 *           }
 *       }
 *   }
 * </pre>
 * <b>关键在 addSuppressed</b>：body 和 close 同时失败时，
 * <b>body 的异常是主异常</b>，close 的异常**不会覆盖**它，而是挂到
 * {@code getSuppressed()} 上 —— 两个都不丢。
 * <p>
 * <b>为什么不能两个都抛</b>：Java 的方法只能抛一个异常。
 * 所以在 JDK 7 引入 suppressed 之前，close 里的异常会**直接吞掉** body 的异常，
 * 你只能看到"关闭失败"，看不到"真正出错的业务"。
 * <p>
 * <b>关闭顺序</b>：多个资源时**逆序**关闭（后开的先关），
 * 因为后面的资源可能依赖前面的（比如 BufferedWriter 依赖 FileWriter）。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 四、★ 异常链（cause）—— 包装时最容易丢的东西
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   // ❌ 丢掉原始异常：日志里只剩 "service failed"，栈里没有任何线索
 *   throw new ServiceException("service failed");
 *
 *   // ✅ 带上 cause：getCause() 能一路走到根因
 *   throw new ServiceException("service failed: " + e.getMessage(), e);
 * </pre>
 * <b>为什么 catch 到之后要包一层</b>：底层抛的是 {@code SQLException}，
 * 上层调用方不该依赖具体持久层实现，所以包成业务异常 ——
 * 但**必须把原异常当 cause 传进去**，否则根因（真正的 SQL 报错、行号、驱动信息）就永远丢了。
 * <p>
 * {@code rootCause} 就是顺着 {@code getCause()} 一路往下走到 null 前一个。
 * 实际排查线上问题时，你要看的就是它。
 * <p>
 * ⚠️ 先预测，再跑（写【输出值】）：
 * <pre>
 * // 预测：Q1 返回的异常的 getMessage() →body-close close failed
 * // 预测：Q1 返回的异常 getSuppressed().length →1
 * // 预测：Q2 返回的列表内容 →先返回 "second.close"，再返回 "first.close"
 * // 预测：new ServiceException("x").getCause() →null
 * </pre>
 */
public class ExceptionLab {

    // ═══════════════════════════════════════════════════════════
    // 给好的工具（不要改）
    // ═══════════════════════════════════════════════════════════

    /**
     * 一个 close 时可能炸的资源。
     * <p>
     * {@code failOnClose = true} 时，{@link #close()} 先记录日志再抛 {@link IllegalStateException}。
     */
    public static class FlakyResource implements AutoCloseable {

        private final String name;
        private final boolean failOnClose;
        private final List<String> closeLog;

        public FlakyResource(String name, boolean failOnClose, List<String> closeLog) {
            this.name = name;
            this.failOnClose = failOnClose;
            this.closeLog = closeLog;
        }

        public String name() {
            return name;
        }

        @Override
        public void close() {
            closeLog.add(name + ".close");
            if (failOnClose) {
                throw new IllegalStateException(name + " close failed");
            }
        }
    }

    /**
     * 业务自定义异常。两个构造器都在 —— 注意区别就在于**有没有 cause**。
     */
    public static class ServiceException extends RuntimeException {

        public ServiceException(String message) {
            super(message);
        }

        public ServiceException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    /**
     * 给好的：一个声明了 {@code throws} 的方法，只给测试用反射观察，不要改。
     */
    public static void declaredThrows() throws IOException, InterruptedException {
        throw new IOException("declared throws");
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q1
    // ═══════════════════════════════════════════════════════════

    /**
     * 用 <b>try-with-resources</b> 打开一个「body 会炸、close 也会炸」的资源，
     * 然后把**主异常**返回出来（不要让它从方法里逃出去，测试要检查它）。
     * <p>
     * 契约：
     * <ul>
     *   <li>资源名用 {@code "body-close"}，{@code failOnClose = true}</li>
     *   <li>body 里抛 {@code new IllegalStateException("body failed")}</li>
     *   <li>返回的异常 {@code getMessage()} 必须是 {@code "body failed"}</li>
     *   <li>返回的异常 {@code getSuppressed().length} 必须是 1</li>
     * </ul>
     * <p>
     * ⚠️ 想一想：close 的异常为什么**没有覆盖** body 的异常？谁把它们分开的？
     */
    // ─────────── 步骤（照着填，别跳步）────────────────────────────
    // 1. `try` 后面加一对【圆括号】，把资源声明放进【括号里】
    //      资源 = new FlakyResource("body-close", true, closeLog)
    //      —— 圆括号就是 try-with-resources 的标志（Q2 你已经写对过）
    // 2. 花括号里只留一句：throw new IllegalStateException("body failed")
    // 3. catch (IllegalStateException e) 里只留一句：return e;
    //      —— 关键：e 就是刚被抛出去那个对象，直接交出去，不要 new 新的
    // 4. 【删掉】所有 return new IllegalStateException("body failed")
    // ────────────────────────────────────────────────────────────
    public static Throwable bodyFailsAndCloseFails() {
        List<String> closeLog = new ArrayList<>();
        // TODO Q1: 用 try-with-resources；catch 到之后把主异常 return 出去
        try ( FlakyResource flakyResource = new FlakyResource("body-close", true, closeLog)){
            throw new IllegalStateException("body failed");
        } catch (IllegalStateException e) {
           return e;
        }
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q2
    // ═══════════════════════════════════════════════════════════

    /**
     * 打开两个都不炸的资源，然后让它们被关闭，返回 {@code closeLog}。
     * <p>
     * 契约：
     * <ul>
     *   <li>第一个资源名 {@code "first"}，第二个 {@code "second"}，都 {@code failOnClose = false}</li>
     *   <li>两个都要被关闭</li>
     * </ul>
     * <p>
     * ⚠️ 这一题只考一件事：**先关哪个**。先预测再跑，别猜。
     */
    public static List<String> closeOrder() {
        List<String> closeLog = new ArrayList<>();
        // TODO Q2
        FlakyResource first = new FlakyResource("first", false, closeLog);
        FlakyResource second = new FlakyResource("second", false, closeLog);
        try (first; second) {
        }
        return closeLog;
    }
    /**
     * 把任意异常包装成 {@link ServiceException}，**必须保留原因链**。
     * <p>
     * 契约：
     * <ul>
     *   <li>message 必须是 {@code "service failed: " + cause.getMessage()}</li>
     *   <li>{@code getCause()} 必须**就是**传进来那个对象（{@code assertSame}，不是复制一个）</li>
     * </ul>
     * <p>
     * ⚠️ 用哪个构造器？两个都在，选错的那个测试会红。
     */
    public static ServiceException wrap(Throwable cause) {
        // TODO Q3-a
        return new ServiceException("service failed: " + cause.getMessage(), cause);
    }

    /**
     * 一路顺着 {@code getCause()} 找到最底层的那个异常（根因）。
     * <p>
     * 契约：
     * <ul>
     *   <li>没有 cause 时，返回它自己</li>
     *   <li>有 cause 时，返回最深的那一层</li>
     * </ul>
     * <p>
     * ⚠️ 注意别写出死循环 —— 想一想：到底哪里是终点？为什么不可能无限深？
     */
    public static Throwable rootCause(Throwable t) {
        // TODO Q3-b
        while (t.getCause() != null) {
            t = t.getCause();
        }
        return t;
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q4 ★ 今天真正的考题
    // ═══════════════════════════════════════════════════════════

    /**
     * 和 {@link #bodyFailsAndCloseFails()} **完全一样的行为**，
     * 但**不许用 try-with-resources** —— 你要手写编译器展开出来的那段。
     * <p>
     * 契约（和 Q1 逐条相同）：
     * <ul>
     *   <li>资源名 {@code "body-close"}，{@code failOnClose = true}</li>
     *   <li>body 抛 {@code new IllegalStateException("body failed")}</li>
     *   <li>返回的异常 message 是 {@code "body failed"}，{@code getSuppressed().length} 是 1</li>
     * </ul>
     * <p>
     * 你必须自己写出：<b>谁调 close、close 炸了之后挂到谁身上</b>。
     * <p>
     * ⚠️ 不能写 {@code finally { r.close(); }} 就完事 —— 那样 close 的异常会
     * <b>覆盖</b> body 的异常，{@code getMessage()} 会变成 {@code "body-close close failed"}，
     * 测试会红。这一步就是这一题存在的全部意义。
     */
    // ─────────── 步骤（照着填）────────────────────────────────────
    // 1. 保留第一行：FlakyResource flakyResource = new FlakyResource(...)
    // 2. 在 try 之前，再声明一个变量存「主异常」，类型 Throwable，初值 null
    // 3. try 花括号里：throw new IllegalStateException("body failed")
    // 4. catch 里：只做一件事 —— 把 e 存进第 2 步那个变量
    //      ⚠️ 这里【不要】return，因为 finally 还没跑
    //      ⚠️ 这里【不要】调 close()，那会让 close 的异常顶掉主异常
    // 5. 新增 finally：里面的 close() 必须【再被一层 try/catch 包住】
    //      · close 成功  → 什么也不做
    //      · close 失败  → 主异常 != null：把 close 的异常 addSuppressed 到它身上
    //                     主异常 == null（body 没炸）：让 close 的异常正常抛出去
    // 6. 方法最后：return 主异常变量
    // 为什么必须这么写：Java 一个方法只能抛一个异常，所以必须有一个"主角"，
    // 另一个挂上去 —— try-with-resources 替你做的就是这个（见类注释第 60–85 行）
    // ────────────────────────────────────────────────────────────
    public static Throwable bodyFailsAndCloseFailsWithoutTwr() {
        List<String> closeLog = new ArrayList<>();
        FlakyResource flakyResource = new FlakyResource("body-close", true, closeLog);
        Throwable mainException = null;
        try{
            throw new IllegalStateException("body failed");
        }catch (IllegalStateException e){
            mainException = e;
        }finally {
            try {
                flakyResource.close();
            } catch (Throwable closeException) {
                if (mainException != null) {
                    mainException.addSuppressed(closeException);
                }else
                    throw closeException;
            }
        }
        return mainException;
    }
}
