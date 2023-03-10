/**
 * Author: Allen Jue
 * Date: 01/04/2023
 */

import java.io.IOException;
import java.util.Scanner;

/**
 * Starts a Client. To make a connection, the Server must be active,
 * the IP address of the Server must be known, and a client must be started.
 */
public class ClientStarter {
    private static final String FREUD = "128.83.120.232";   /* Default IP Address, lab machine */
    private static final String SUGAR_BOMBS = "128.83.139.36";
    private static final String LOCAL = "127.0.0.1";
    private static final int TEST_PORT = 3000;              /* Arbitrary port used */

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.startConnection(LOCAL, TEST_PORT);
        String credentials = client.getCredInput();
        boolean authenticated = client.authenticate(credentials, false);
        Scanner sc = new Scanner(System.in);
        while(authenticated) {
            String resp = client.sendMessage(sc.nextLine());
            if(resp.equals("Exiting")) {
                break;
            }
            System.out.println(resp);
        }
        sc.close();
    }
}
