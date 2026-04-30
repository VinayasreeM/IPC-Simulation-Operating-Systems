package ipc;

import utils.Logger;
import java.io.*;
import java.net.*;

public class SocketIPC {
    private static final String MODULE = "Sockets";
    private static final int PORT = 9876;

    public static void runSimulation() {
        Logger.logDivider("SOCKET IPC — REAL OS-LEVEL COMMUNICATION");
        Logger.logInfo(MODULE, "Creating real TCP socket connection on localhost:" + PORT);
        Logger.logInfo(MODULE, "Server and Client are separate threads with real OS socket handles.");

        // Server thread — listens for connection
        Thread serverThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                Logger.logInfo(MODULE, "Server: socket opened on port " + PORT + " — waiting for client...");

                Socket clientSocket = serverSocket.accept();
                Logger.logReceived(MODULE, "Server: client connected from " +
                    clientSocket.getInetAddress() + ":" + clientSocket.getPort());

                BufferedReader in = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                String message;
                while ((message = in.readLine()) != null) {
                    Logger.logReceived(MODULE, "Server received ← \"" + message + "\"");
                    if (message.equals("BYE")) {
                        out.println("BYE");
                        Logger.logInfo(MODULE, "Server: received BYE, closing connection.");
                        break;
                    }
                    // Echo back with response
                    String response = "ACK: " + message;
                    out.println(response);
                    Logger.logSent(MODULE, "Server replied → \"" + response + "\"");
                }

                clientSocket.close();
                Logger.logInfo(MODULE, "Server: socket closed.");

            } catch (IOException e) {
                if (!e.getMessage().contains("Socket closed"))
                    Logger.logError(MODULE, "Server error: " + e.getMessage());
            }
        });

        // Client thread — connects and sends messages
        Thread clientThread = new Thread(() -> {
            try {
                Thread.sleep(800); // Wait for server to start

                try (Socket socket = new Socket("localhost", PORT)) {
                    Logger.logSent(MODULE, "Client: connected to server at localhost:" + PORT);
                    Logger.logInfo(MODULE, "Client local port: " + socket.getLocalPort());

                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                    String[] messagesToSend = {
                        "Hello Server!",
                        "Sending data via real socket",
                        "IPC over TCP/IP",
                        "This is OS-level communication",
                        "BYE"
                    };

                    for (String msg : messagesToSend) {
                        Thread.sleep(700);
                        out.println(msg);
                        Logger.logSent(MODULE, "Client sent → \"" + msg + "\"");

                        if (msg.equals("BYE")) break;

                        String response = in.readLine();
                        if (response != null)
                            Logger.logReceived(MODULE, "Client got ← \"" + response + "\"");
                    }
                }
                Logger.logInfo(MODULE, "Client: socket closed. Communication complete.");

            } catch (IOException | InterruptedException e) {
                Logger.logError(MODULE, "Client error: " + e.getMessage());
            }
        });

        serverThread.setDaemon(true);
        serverThread.start();
        clientThread.start();

        new Thread(() -> {
            try {
                clientThread.join();
                serverThread.join(2000);
                Logger.logDivider("SOCKET IPC COMPLETE");
                Logger.logInfo(MODULE, "Real TCP socket communication finished successfully.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }
}