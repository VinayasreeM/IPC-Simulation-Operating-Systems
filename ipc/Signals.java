package ipc;

import utils.Logger;

public class Signals {
    private static final String MODULE = "Signals";
    private static final Object lock = new Object();
    private static boolean signalSent = false;

    public static void runSimulation() {
        Logger.logDivider("SIGNALS SIMULATION");
        Logger.logInfo(MODULE, "Receiver waits for signal. Sender does work then signals receiver.");
        signalSent = false;

        Thread receiver = new Thread(() -> {
            synchronized (lock) {
                try {
                    Logger.logWarning(MODULE, "Receiver: waiting for signal from Sender...");
                    while (!signalSent) lock.wait();
                    Logger.logReceived(MODULE, "Receiver: signal received! Resuming execution.");
                    Logger.logInfo(MODULE, "Receiver: processing data after signal...");
                    Thread.sleep(500);
                    Logger.logInfo(MODULE, "Receiver: done.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });

        Thread sender = new Thread(() -> {
            try {
                Logger.logInfo(MODULE, "Sender: doing some work before signaling...");
                Thread.sleep(1000);
                Logger.logInfo(MODULE, "Sender: work done. Sending signal now...");
                Thread.sleep(500);
                synchronized (lock) {
                    signalSent = true;
                    lock.notifyAll();
                    Logger.logSent(MODULE, "Sender: signal sent via notify()!");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        receiver.start();
        try { Thread.sleep(200); } catch (InterruptedException e) {}
        sender.start();
    }
}