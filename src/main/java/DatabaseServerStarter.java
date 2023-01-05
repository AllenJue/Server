/**
 * Author: Allen Jue
 * Date: 01/04/2023
 */

/**
 * Starting point for a server. Creates a server with a port of 3000.
 */
public class DatabaseServerStarter {
    private static final int DEFAULT_PORT = 3000;

    public static void main(String[] args) {
        DatabaseMultiServer server = new DatabaseMultiServer();
        try {
            server.start(DEFAULT_PORT);
        } catch (Exception e) {
            System.out.println("Failed to start server");
            System.out.println(e);
        }
    }
}