package ipc;

import utils.Logger;
import java.util.concurrent.Semaphore;

public class SemaphoreSimulation {
    private static final String MODULE = "Semaphore";
    private static int resource = 0;

    public static void runSimulation() {
        Logger.logDivider("SEMAPHORE — WITHOUT (Race Condition)");
        Logger.logWarning(MODULE, "4 threads accessing shared resource WITHOUT semaphore...");
        resource = 0;
        runWithoutSemaphore();

        new Thread(() -> {
            try {
                Thread.sleep(4000);
                Logger.logDivider("SEMAPHORE — WITH (Fixed)");
                Logger.logInfo(MODULE, "Same 4 threads WITH semaphore (only 1 at a time)...");
                resource = 0;
                runWithSemaphore();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private static void runWithoutSemaphore() {
        for (int i = 1; i <= 4; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    Logger.logError(MODULE, "Thread-" + id + " entering critical section!");
                    Thread.sleep(300);
                    resource++;
                    Logger.logError(MODULE, "Thread-" + id + " modified resource = " + resource + " (UNSAFE!)");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }

    private static void runWithSemaphore() {
        Semaphore semaphore = new Semaphore(1); // Only 1 thread at a time
        for (int i = 1; i <= 4; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    semaphore.acquire();
                    Logger.logSent(MODULE, "Thread-" + id + " acquired semaphore → entering critical section");
                    Thread.sleep(400);
                    resource++;
                    Logger.logReceived(MODULE, "Thread-" + id + " modified resource = " + resource + " (SAFE ✔)");
                    semaphore.release();
                    Logger.logInfo(MODULE, "Thread-" + id + " released semaphore");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }
}