import java.io.IOException;
import java.net.ServerSocket;

public class ServerStarter {
    private static final int DEFAULT_PORT = 3000;

    public static void main(String[] args) {
        MultiServer server = new MultiServer();
        try {
            server.start(DEFAULT_PORT);
        } catch (Exception e) {
            System.out.println("Failed to start server");
        }
    }
}
