/**
 * Author: Allen Jue
 * Data: 12/17/2022
 */

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for accuracy within a single client-server relationship.
 * Note that test cases will pass if the network is simply not working.
 */
public class SingleClientTest {
    private static final String FREUD = "128.83.120.232";   /* Default IP Address, lab machine */
    private static final int TEST_PORT = 3000;              /* Arbitrary port used */

    /**
     * Tests that a single client can send and close messages
     */
    @Test
    public void test_msg_correct_single_client() {
        Client client = new Client();
        try {
            client.startConnection(FREUD, TEST_PORT);
            String resp1 = client.sendMessage("Hello");
            String resp2 = client.sendMessage("World");
            String terminate = client.sendMessage("quit");
            Assertions.assertEquals("Message received: Hello", resp1);
            Assertions.assertEquals("Message received: World", resp2);
            Assertions.assertEquals("Exiting", terminate);
        } catch (Exception e) {
            System.out.println("Network failed");
        }
    }

    /**
     * Tests that a single client can sequentially send messages, close, open a new connection
     */
    @Test
    public void test_msg_disconnect_and_reconnect_client() {
        Client client = new Client();
        try {
            client.startConnection(FREUD, TEST_PORT);
            String resp1 = client.sendMessage("Connecting");
            String terminate = client.sendMessage("quit");
            Assertions.assertEquals("Message received: Connecting", resp1);
            Assertions.assertEquals("Exiting", terminate);
            client.startConnection(FREUD, TEST_PORT);
            String resp2 = client.sendMessage("Reconnecting");
            String terminate2 = client.sendMessage("quit");
            Assertions.assertEquals("Message received: Reconnecting", resp2);
            Assertions.assertEquals("Exiting", terminate2);
        } catch (Exception e) {
            System.out.println("Network failed");
        }
    }
}
