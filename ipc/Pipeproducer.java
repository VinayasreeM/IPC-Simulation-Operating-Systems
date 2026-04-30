package ipc;

public class PipeProducer {
    public static void main(String[] args) throws Exception {
        String[] messages = {
            "Hello from Producer!",
            "Data packet #1",
            "Data packet #2",
            "Data packet #3",
            "END"
        };
        for (String msg : messages) {
            Thread.sleep(600);
            System.out.println(msg);
            System.out.flush();
        }
    }
}