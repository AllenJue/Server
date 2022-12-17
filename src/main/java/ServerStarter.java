/**
 * Author: Allen Jue
 * Date: 12/17/2022
 */

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Starting point for a server. Creates a server with a port of 3000.
 */
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
