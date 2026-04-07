package ipc;

import utils.Logger;
import java.util.concurrent.LinkedBlockingQueue;

public class PipeSimulation {
    private static final String MODULE = "Pipes";

    public static void runSimulation() {
        Logger.logDivider("PIPE SIMULATION");
        Logger.logInfo(MODULE, "Starting one-way pipe between Process-1 (Producer) and Process-2 (Consumer)");

        LinkedBlockingQueue<String> pipe = new LinkedBlockingQueue<>();

        Thread producer = new Thread(() -> {
            try {
                String[] messages = {"Hello from Process-1!", "Data packet #1", "Data packet #2", "END"};
                for (String msg : messages) {
                    Thread.sleep(500);
                    pipe.put(msg);
                    Logger.logSent(MODULE, "Process-1 → \"" + msg + "\"");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(700);
                    String msg = pipe.take();
                    Logger.logReceived(MODULE, "Process-2 ← \"" + msg + "\"");
                    if (msg.equals("END")) {
                        Logger.logInfo(MODULE, "Process-2 received END signal. Pipe closed.");
                        break;
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
    }
}