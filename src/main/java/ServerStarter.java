import java.io.IOException;
import java.net.ServerSocket;

public class ServerStarter {
    private static final int DEFAULT_PORT = 3000;

    public static void main(String[] args) throws IOException {
        MultiServer server = new MultiServer();
        server.start(DEFAULT_PORT);
    }
}
