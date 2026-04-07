package ipc;

import utils.Logger;
import java.util.concurrent.LinkedBlockingQueue;

public class MessageQueue {
    private static final String MODULE = "MsgQueue";
    private static final LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>(10);

    public static void runSimulation() {
        Logger.logDivider("MESSAGE QUEUE SIMULATION");
        Logger.logInfo(MODULE, "3 Producers sending to shared queue — 2 Consumers reading (FIFO order)");

        queue.clear();

        // 3 producers
        for (int i = 1; i <= 3; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    for (int j = 1; j <= 2; j++) {
                        Thread.sleep((long)(Math.random() * 600 + 300));
                        String msg = "MSG-P" + id + "-#" + j;
                        queue.put(msg);
                        Logger.logSent(MODULE, "Producer-" + id + " enqueued → \"" + msg + "\" | Queue size: " + queue.size());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }

        // 2 consumers
        for (int i = 1; i <= 2; i++) {
            final int id = i;
            new Thread(() -> {
                try {
                    int count = 0;
                    while (count < 3) {
                        Thread.sleep(800);
                        String msg = queue.poll();
                        if (msg != null) {
                            Logger.logReceived(MODULE, "Consumer-" + id + " dequeued ← \"" + msg + "\" | Queue size: " + queue.size());
                            count++;
                        }
                    }
                    Logger.logInfo(MODULE, "Consumer-" + id + " finished processing.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        }
    }
}