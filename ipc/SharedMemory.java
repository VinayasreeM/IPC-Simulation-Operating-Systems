package ipc;

import utils.Logger;

public class SharedMemory {
    private static final String MODULE = "SharedMem";
    private static int sharedValue = 0;

    public static void runSimulation() {
        Logger.logDivider("SHARED MEMORY — WITHOUT SYNC (Race Condition)");
        Logger.logWarning(MODULE, "Two threads writing to shared memory WITHOUT synchronization...");
        sharedValue = 0;
        runWithoutSync();

        // After delay, show fixed version
        new Thread(() -> {
            try {
                Thread.sleep(3500);
                Logger.logDivider("SHARED MEMORY — WITH SYNC (Fixed)");
                Logger.logInfo(MODULE, "Same scenario WITH synchronized blocks...");
                sharedValue = 0;
                runWithSync();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private static void runWithoutSync() {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                int temp = sharedValue;
                try { Thread.sleep(50); } catch (InterruptedException e) {}
                sharedValue = temp + 1;
                Logger.logError(MODULE, "Thread-1 wrote sharedValue = " + sharedValue);
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                int temp = sharedValue;
                try { Thread.sleep(50); } catch (InterruptedException e) {}
                sharedValue = temp + 1;
                Logger.logError(MODULE, "Thread-2 wrote sharedValue = " + sharedValue);
            }
        });

        t1.start(); t2.start();
        new Thread(() -> {
            try {
                t1.join(); t2.join();
                Logger.logWarning(MODULE, "Final sharedValue (expected 10, got) = " + sharedValue + " ← RACE CONDITION!");
            } catch (InterruptedException e) {}
        }).start();
    }

    private static void runWithSync() {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                synchronized (SharedMemory.class) {
                    int temp = sharedValue;
                    try { Thread.sleep(50); } catch (InterruptedException e) {}
                    sharedValue = temp + 1;
                    Logger.logSent(MODULE, "Thread-1 safely wrote sharedValue = " + sharedValue);
                }
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                synchronized (SharedMemory.class) {
                    int temp = sharedValue;
                    try { Thread.sleep(50); } catch (InterruptedException e) {}
                    sharedValue = temp + 1;
                    Logger.logReceived(MODULE, "Thread-2 safely wrote sharedValue = " + sharedValue);
                }
            }
        });

        t1.start(); t2.start();
        new Thread(() -> {
            try {
                t1.join(); t2.join();
                Logger.logInfo(MODULE, "Final sharedValue (expected 10, got) = " + sharedValue + " ✔ CORRECT!");
            } catch (InterruptedException e) {}
        }).start();
    }
}