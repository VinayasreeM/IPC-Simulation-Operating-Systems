package utils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.function.BiConsumer;
import java.awt.Color;

public class Logger {
    private static BiConsumer<String, Color> logCallback;

    public static final Color GREEN  = new Color(0, 180, 80);
    public static final Color BLUE   = new Color(30, 120, 255);
    public static final Color RED    = new Color(220, 50, 50);
    public static final Color ORANGE = new Color(220, 140, 0);
    public static final Color GRAY   = new Color(130, 130, 150);
    public static final Color WHITE  = new Color(220, 220, 230);

    public static void setCallback(BiConsumer<String, Color> callback) {
        logCallback = callback;
    }

    private static String getTimestamp() {
        return "[" + LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + "]";
    }

    public static void log(String module, String msg) {
        send(getTimestamp() + " " + module + " | " + msg, WHITE);
    }

    public static void logSent(String module, String msg) {
        send(getTimestamp() + " " + module + " | ➤ SENT: " + msg, GREEN);
    }

    public static void logReceived(String module, String msg) {
        send(getTimestamp() + " " + module + " | ✔ RECEIVED: " + msg, BLUE);
    }

    public static void logError(String module, String msg) {
        send(getTimestamp() + " " + module + " | ✖ " + msg, RED);
    }

    public static void logWarning(String module, String msg) {
        send(getTimestamp() + " " + module + " | ⚠ " + msg, ORANGE);
    }

    public static void logInfo(String module, String msg) {
        send(getTimestamp() + " " + module + " | ℹ " + msg, GRAY);
    }

    public static void logDivider(String label) {
        send("━━━━━━━━━━  " + label + "  ━━━━━━━━━━", new Color(80, 80, 100));
    }

    private static void send(String msg, Color color) {
        if (logCallback != null) logCallback.accept(msg, color);
        else System.out.println(msg);
    }
}