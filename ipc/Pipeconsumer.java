package ipc;

import java.util.Scanner;

public class PipeConsumer {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String msg = scanner.nextLine().trim();
            if (!msg.isEmpty()) {
                System.out.println("Received: " + msg);
                System.out.flush();
                if (msg.equals("END")) break;
            }
        }
    }
}