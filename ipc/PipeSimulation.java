package ipc;

import utils.Logger;
import java.io.*;

public class PipeSimulation {
    private static final String MODULE = "Pipes";

    public static void runSimulation() {
        Logger.logDivider("PIPE SIMULATION — REAL OS PROCESSES");
        Logger.logInfo(MODULE, "Spawning two real OS processes using ProcessBuilder...");

        try {
            String classpath = System.getProperty("java.class.path");

            // Spawn Producer process
            ProcessBuilder producerPB = new ProcessBuilder(
                "java", "-cp", classpath, "ipc.PipeProducer"
            );
            producerPB.redirectErrorStream(true);
            Process producer = producerPB.start();

            // Spawn Consumer process
            ProcessBuilder consumerPB = new ProcessBuilder(
                "java", "-cp", classpath, "ipc.PipeConsumer"
            );
            consumerPB.redirectErrorStream(true);
            Process consumer = consumerPB.start();

            long producerPID = producer.pid();
            long consumerPID = consumer.pid();

            Logger.logInfo(MODULE, "Producer process started  —  PID: " + producerPID);
            Logger.logInfo(MODULE, "Consumer process started  —  PID: " + consumerPID);
            Logger.logWarning(MODULE, "Tip: Open Task Manager and search these PIDs to verify real OS processes!");

            // Thread: read producer stdout, log it, forward to consumer stdin
            new Thread(() -> {
                try (
                    BufferedReader prodReader = new BufferedReader(
                        new InputStreamReader(producer.getInputStream()));
                    OutputStream consumerIn = consumer.getOutputStream()
                ) {
                    String line;
                    while ((line = prodReader.readLine()) != null) {
                        if (!line.isEmpty()) {
                            Logger.logSent(MODULE, "[PID " + producerPID + "] → \"" + line + "\"");
                            consumerIn.write((line + "\n").getBytes());
                            consumerIn.flush();
                            if (line.equals("END")) break;
                        }
                    }
                } catch (IOException e) {
                    Logger.logError(MODULE, "Pipe error: " + e.getMessage());
                }
            }).start();
            new Thread(() -> {
                try (BufferedReader consumerReader = new BufferedReader(
                        new InputStreamReader(consumer.getInputStream()))) {
                    String line;
                    while ((line = consumerReader.readLine()) != null) {
                        if (!line.isEmpty())
                            Logger.logReceived(MODULE, "[PID " + consumerPID + "] ← \"" + line + "\"");
                    }
                } catch (IOException e) {
                    Logger.logError(MODULE, "Consumer read error: " + e.getMessage());
                }
            }).start();
            new Thread(() -> {
                try {
                    producer.waitFor();
                    consumer.waitFor();
                    Logger.logInfo(MODULE, "Both OS processes exited. Real pipe closed.");
                    Logger.logInfo(MODULE, "PIDs " + producerPID + " & " + consumerPID + " are no longer active.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();

        } catch (IOException e) {
            Logger.logError(MODULE, "ProcessBuilder failed: " + e.getMessage());
            Logger.logWarning(MODULE, "Falling back to thread simulation...");
            runFallback();
        }
    }
    private static void runFallback() {
        java.util.concurrent.LinkedBlockingQueue<String> pipe =
            new java.util.concurrent.LinkedBlockingQueue<>();
        Thread producer = new Thread(() -> {
            try {
                String[] messages = {"Hello from Producer!", "Data packet #1", "Data packet #2", "END"};
                for (String msg : messages) {
                    Thread.sleep(500);
                    pipe.put(msg);
                    Logger.logSent(MODULE, "Producer → \"" + msg + "\"");
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
        Thread consumer = new Thread(() -> {
            try {
                while (true) {
                    String msg = pipe.take();
                    Logger.logReceived(MODULE, "Consumer ← \"" + msg + "\"");
                    if (msg.equals("END")) break;
                }
            } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        });
        producer.start(); consumer.start();
    }
}