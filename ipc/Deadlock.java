package ipc;

import utils.Logger;
import java.util.concurrent.locks.ReentrantLock;

public class Deadlock {
    private static final String MODULE = "Deadlock";
    private static final ReentrantLock lockA = new ReentrantLock();
    private static final ReentrantLock lockB = new ReentrantLock();

    public static void runSimulation() {
        Logger.logDivider("DEADLOCK DEMONSTRATION");
        Logger.logWarning(MODULE, "Thread-1 locks A then wants B. Thread-2 locks B then wants A → DEADLOCK!");
        runDeadlock();

        new Thread(() -> {
            try {
                Thread.sleep(4000);
                Logger.logDivider("DEADLOCK FIXED");
                Logger.logInfo(MODULE, "Both threads now acquire locks in same order (A then B) → No deadlock!");
                runFixed();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private static void runDeadlock() {
        Thread t1 = new Thread(() -> {
            try {
                lockA.lock();
                Logger.logError(MODULE, "Thread-1: acquired Lock-A, waiting for Lock-B...");
                Thread.sleep(500);
                // Try to get B (but T2 holds it)
                if (lockB.tryLock()) {
                    Logger.logSent(MODULE, "Thread-1: acquired Lock-B (lucky, no deadlock this run)");
                    lockB.unlock();
                } else {
                    Logger.logError(MODULE, "Thread-1: CANNOT acquire Lock-B → DEADLOCK! Stuck forever.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                if (lockA.isHeldByCurrentThread()) lockA.unlock();
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                lockB.lock();
                Logger.logError(MODULE, "Thread-2: acquired Lock-B, waiting for Lock-A...");
                Thread.sleep(500);
                if (lockA.tryLock()) {
                    Logger.logSent(MODULE, "Thread-2: acquired Lock-A (lucky, no deadlock this run)");
                    lockA.unlock();
                } else {
                    Logger.logError(MODULE, "Thread-2: CANNOT acquire Lock-A → DEADLOCK! Stuck forever.");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                if (lockB.isHeldByCurrentThread()) lockB.unlock();
            }
        });

        t1.start(); t2.start();
    }

    private static void runFixed() {
        Thread t1 = new Thread(() -> {
            try {
                lockA.lock();
                Logger.logSent(MODULE, "Thread-1: acquired Lock-A");
                Thread.sleep(300);
                lockB.lock();
                Logger.logSent(MODULE, "Thread-1: acquired Lock-B → doing work safely ✔");
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                if (lockB.isHeldByCurrentThread()) lockB.unlock();
                if (lockA.isHeldByCurrentThread()) lockA.unlock();
                Logger.logInfo(MODULE, "Thread-1: released both locks.");
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                Thread.sleep(100);
                lockA.lock(); // Same order as T1
                Logger.logReceived(MODULE, "Thread-2: acquired Lock-A");
                Thread.sleep(300);
                lockB.lock();
                Logger.logReceived(MODULE, "Thread-2: acquired Lock-B → doing work safely ✔");
                Thread.sleep(300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                if (lockB.isHeldByCurrentThread()) lockB.unlock();
                if (lockA.isHeldByCurrentThread()) lockA.unlock();
                Logger.logInfo(MODULE, "Thread-2: released both locks. No deadlock!");
            }
        });

        t1.start(); t2.start();
    }
}