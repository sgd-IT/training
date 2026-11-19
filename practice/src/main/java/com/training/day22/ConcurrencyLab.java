package com.training.day22;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Day 22 - Q1~Q7: ConcurrencyLab —— 线程基础与并发三大问题
 * <p>
 * ═══════════════════════════════════════════════════════════
 * ★ 必读：今天开工前先把这一整段读完（考点全在这里）
 * ═══════════════════════════════════════════════════════════
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 一、线程是什么
 * ═══════════════════════════════════════════════════════════
 * 线程 = 一条独立的**执行流**。同一个进程里的线程共享堆、共享静态变量，
 * 但**各自有独立的栈**（各自的局部变量、各自的调用栈）。
 * <p>
 * 起线程只有三步：造出来 → {@code start()} → 等它（{@code join()}）。
 * 造的时候要给它一段任务，只有两种写法：
 * <pre>
 *   Runnable r = () -&gt; { ... };           // 有任务，无返回值（Day 16 的 SAM 就在这用）
 *   new Thread(r, "名字").start();         // 名字是排障时唯一能认出它的东西
 *
 *   Thread t = new Thread() {             // 继承 Thread 再重写 run() —— 不推荐，因为类只能单继承
 *       public void run() { ... }
 *   };
 * </pre>
 * 真实项目里**很少自己 new Thread**，都用线程池（Day 24 讲）。但原理必须先懂。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 二、★★ start() 和 run() 完全不是一回事（今天第一个坑）
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   t.start();   // ① 向 JVM 申请一条新线程 ② 新线程自己去调 run()   ← 真并发
 *   t.run();     // 就是一次**普通方法调用**！还在当前线程里跑        ← 假并发
 * </pre>
 * 调 {@code run()} 不会报错，程序照样"跑通"，但它一行并发代码都没执行 ——
 * 这是并发 bug 里最隐蔽的一类：**看起来很对**。
 * <p>
 * 判别方法只有一个：在 body 里打印 {@code Thread.currentThread().getName()}。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 三、★ join() = 「等这条线程死」
 * ═══════════════════════════════════════════════════════════
 * {@code start()} 是**异步**的：调完立刻返回，线程在后台自己跑。
 * 所以主线程如果不等，就会读到「别人还没干完」的中间状态。
 * {@code t.join()} 让**当前线程**阻塞，直到 t 结束。
 * <p>
 * ⚠️ 今天的 Q3 就是这个：不 join 的话，你读到的 size 是 0。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 四、★★★ 并发三大问题（今天的正题）
 * ═══════════════════════════════════════════════════════════
 * <b>1. 原子性</b> —— {@code count++} 是**三步**，不是一步：
 * <pre>
 *   ① 把 count 从内存读进寄存器
 *   ② 寄存器 +1
 *   ③ 写回内存
 * </pre>
 * 两条线程同时执行到 ①，都读到 5，都算出 6，都写回 6 —— **加了一次，丢了一次**。
 * 这叫 lost update。它跟「谁先谁后」无关，纯粹是三步之间可以被插队。
 * <br>解法：{@code synchronized}（同一时刻只允许一条线程进临界区）、
 * {@code AtomicInteger}（CAS 指令，不阻塞）。
 * <p>
 * <b>2. 可见性</b> —— 一条线程改了共享变量，另一条线程**可能永远看不到**。
 * <pre>
 *   原因：JIT 把 while (!stop) 里的 stop 读进了寄存器，循环里不再回内存读
 *        （编译器认为循环里没人改它 —— 它不知道**别的线程**会改）
 * </pre>
 * 解法：{@code volatile} —— 读直接从内存读，写立刻刷回内存，且**禁止指令重排**。
 * <p>
 * <b>3. 有序性</b> —— 编译器和 CPU 都会**重排**指令（为了效率），
 * 单线程里重排保证结果不变，多线程里就可能看到「先执行了后面那句」。
 * {@code volatile} 和 {@code synchronized} 都带内存屏障，能禁止重排。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 五、★★★ 为什么并发领域「测试全绿 ≠ 写对了」
 * ═══════════════════════════════════════════════════════════
 * 这是今天最该带走的一句话。前面 21 天，测试绿 = 写对了。
 * <b>从今天起不成立了：</b>
 * <pre>
 *   丢更新是**概率**事件 —— 跑 100 次全对，第 101 次错，上线后每 10 万次错一次
 *   测试通过只能证明「这次没撞上」，不能证明「不会撞上」
 * </pre>
 * 所以并发的正确性靠**推理**（这三步之间能不能插队？这个变量有没有可能读到旧值？），
 * 测试只用来**复现**你推理出来的问题。
 * <p>
 * ═══════════════════════════════════════════════════════════
 * 六、今天的 TODO
 * ═══════════════════════════════════════════════════════════
 * <pre>
 *   TODO Q1: nameInNewThread   起线程 + 线程名（必须真的 start）
 *   TODO Q2: nameViaRun        只调 run()，观察它在哪个线程里跑
 *   TODO Q3a/b: sizeBeforeJoin / sizeAfterJoin   join 的意义
 *   TODO Q4: plainIncrement    ★ 故意复现丢更新（这一题**不要修**）
 *   TODO Q5: lockedIncrement   用 synchronized 修好
 *   TODO Q6: atomicIncrement   用 AtomicInteger 修好
 *   TODO Q7: stopIsVisible     volatile 可见性（字段少一个修饰符）
 * </pre>
 * <p>
 * 规矩不变：**只改 TODO 里抛异常那一行**，签名、字段、类注释都不许动。
 */
public class ConcurrencyLab {

    /** Q4 用：普通 int，故意不做任何保护。 */
    private int plainCount;

    /** Q5 用：这个字段要靠你自己加保护（同步方法 / 同步块都行）。 */
    private int lockedCount;

    /** Q6 用：原子类，方法自己就是原子的，不用你加锁。 */
    private final AtomicInteger atomicCount = new AtomicInteger();

    /** Q7 用：⚠️ 这个字段**少一个修饰符** —— 判断加不加，加了以后能一句话说出理由。 */
    private boolean stop;

    // ═══════════════════════════════════════════════════════════
    // TODO Q1
    // ═══════════════════════════════════════════════════════════
    /**
     * 起一条新线程，把它的名字设成 {@code "day22-worker"}；
     * 在**那条线程里**读 {@code Thread.currentThread().getName()}，等它结束，把读到的名字返回。
     * <p>
     * 契约：
     * <ul>
     *   <li>返回值必须**正好等于** {@code "day22-worker"}</li>
     *   <li>必须用 {@code start()}，不能用 {@code run()}（否则读到的是 {@code "main"}）</li>
     *   <li>必须 {@code join()} 之后再返回，否则可能读到 null</li>
     * </ul>
     * <p>
     * 提示：lambda 里拿不到可变的局部变量（Day 16 的 effectively-final），
     * 想跨线程把值带出来，用一个只改内容、不改引用的容器 —— 你选一个。
     */
    public String nameInNewThread() throws InterruptedException {
        throw new UnsupportedOperationException("TODO Q1");
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q2
    // ═══════════════════════════════════════════════════════════
    /**
     * 造一个 Thread（body 里读线程名），**不 start，直接调 {@code run()}**，返回读到的名字。
     * <p>
     * 契约：
     * <ul>
     *   <li>返回值必须**正好等于** {@code "main"}</li>
     * </ul>
     * <p>
     * ⚠️ 答对不算完 —— 收尾题会问：为什么是 main？这时到底有没有新线程？
     */
    public String nameViaRun() {
        throw new UnsupportedOperationException("TODO Q2");
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q3a
    // ═══════════════════════════════════════════════════════════
    /**
     * 起 3 条线程，名字分别是 {@code "day22-0"} / {@code "day22-1"} / {@code "day22-2"}；
     * 每条线程**先 {@code Thread.sleep(200)}，再**把自己的名字 {@code add} 进 {@code sink}。
     * 然后：**不 join**，立刻返回 {@code sink.size()}。
     * <p>
     * 契约：
     * <ul>
     *   <li>返回 {@code 0} —— 因为 3 条线程都还在 sleep，一条都没加进去</li>
     *   <li>这是**故意**的：这一题要你看清「start() 是异步的」</li>
     * </ul>
     */
    public int sizeBeforeJoin(List<String> sink) throws InterruptedException {
        throw new UnsupportedOperationException("TODO Q3a");
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q3b
    // ═══════════════════════════════════════════════════════════
    /**
     * 跟 Q3a 一模一样，唯一区别：**join 全部 3 条线程之后**再返回 {@code sink.size()}。
     * <p>
     * 契约：
     * <ul>
     *   <li>返回 {@code 3}</li>
     * </ul>
     */
    public int sizeAfterJoin(List<String> sink) throws InterruptedException {
        throw new UnsupportedOperationException("TODO Q3b");
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q4  ★ 这一题是**故意复现 bug**，不要修它
    // ═══════════════════════════════════════════════════════════
    /**
     * 起 {@code threads} 条线程，每条对 {@link #plainCount} 执行 {@code perThread} 次
     * <b>普通 {@code ++}</b>；全部 join 后返回 {@code plainCount} 的最终值。
     * <p>
     * 契约：
     * <ul>
     *   <li>{@code plainIncrement(8, 50_000)} 的返回值**几乎必然小于 400000**</li>
     *   <li>⚠️ 不许加 synchronized、不许用原子类 —— 这一题就是要你**把 bug 跑出来**</li>
     *   <li>跑完把实际值记到日志里，这就是证据</li>
     * </ul>
     * <p>
     * 提示：{@code plainCount++} 写在一行，但它是三步。
     */
    public int plainIncrement(int threads, int perThread) throws InterruptedException {
        throw new UnsupportedOperationException("TODO Q4");
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q5
    // ═══════════════════════════════════════════════════════════
    /**
     * 跟 Q4 完全一样，但操作的是 {@link #lockedCount}，并且用 {@code synchronized} 保证原子性。
     * <p>
     * 契约：
     * <ul>
     *   <li>{@code lockedIncrement(8, 50_000)} 必须**正好等于 400000**</li>
     *   <li>只能用 {@code synchronized}（同步方法或同步块都行），不许用原子类</li>
     * </ul>
     * <p>
     * 收尾题会问：锁的到底是什么？为什么锁住以后那三步就不能被插队了？
     */
    public int lockedIncrement(int threads, int perThread) throws InterruptedException {
        throw new UnsupportedOperationException("TODO Q5");
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q6
    // ═══════════════════════════════════════════════════════════
    /**
     * 跟 Q5 一样，但用 {@code AtomicInteger}（{@link #atomicCount}）保证原子性。
     * <p>
     * 契约：
     * <ul>
     *   <li>{@code atomicIncrement(8, 50_000)} 必须**正好等于 400000**</li>
     *   <li>不许加任何锁</li>
     * </ul>
     * <p>
     * 收尾题会问：原子类凭什么不加锁也能对？它的 {@code incrementAndGet()} 最终是什么指令？
     */
    public int atomicIncrement(int threads, int perThread) throws InterruptedException {
        throw new UnsupportedOperationException("TODO Q6");
    }

    // ═══════════════════════════════════════════════════════════
    // TODO Q7
    // ═══════════════════════════════════════════════════════════
    /**
     * 演示可见性。做法：
     * <ol>
     *   <li>起一条 <b>daemon</b> 线程（{@code setDaemon(true)} 必须在 {@code start()} 之前调），
     *       body 是 {@code while (!stop) { Thread.onSpinWait(); }}</li>
     *   <li>主线程 {@code Thread.sleep(100)}</li>
     *   <li>主线程把 {@link #stop} 置为 {@code true}</li>
     *   <li>{@code join(waitMillis)} 等它；返回这条线程**是否真的退出了**（{@code !isAlive()}）</li>
     * </ol>
     * <p>
     * 契约：
     * <ul>
     *   <li>{@code stopIsVisible(2000)} 必须返回 {@code true}</li>
     *   <li>如果不给字段加那个修饰符，这个循环**很可能永远不退出**（返回 false）—— 那就是可见性 bug</li>
     *   <li>daemon 是为了万一它不退出也不拖住 JVM（测试进程能正常结束）</li>
     * </ul>
     * <p>
     * ⚠️ 字段在类顶部声明好了，但**少一个修饰符**。你要做的是把它补上，并且能说清为什么。
     */
    public boolean stopIsVisible(long waitMillis) throws InterruptedException {
        throw new UnsupportedOperationException("TODO Q7");
    }
}
