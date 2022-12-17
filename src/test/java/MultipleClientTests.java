/**
 * Author: Allen Jue
 * Date: 12/17/2022
 */

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests the accuracy of the client-server relationship between multiple clients
 */
public class MultipleClientTests {
    private static final String FREUD = "128.83.120.232";
    private static final int TEST_PORT = 3000;

    /**
     * Test if multiple clients at once can work commands for each are sequentially adjacent
     */
    @Test
    public void test_open_multiple_clients() {
        Client client1 = new Client();
        Client client2 = new Client();
        try {
            client1.startConnection(FREUD, TEST_PORT);
            client2.startConnection(FREUD, TEST_PORT);
            String resp1 = client1.sendMessage("Hello");
            String terminate = client1.sendMessage("quit");
            Assertions.assertEquals("Message received: Hello", resp1);
            Assertions.assertEquals("Exiting", terminate);
            String resp2 = client2.sendMessage("Hello from thread 2");
            String terminate2 = client2.sendMessage("quit");
            Assertions.assertEquals("Message received: Hello from thread 2", resp2);
            Assertions.assertEquals("Exiting", terminate);
        } catch (Exception e) {
            System.out.println("Network failed");
        }
    }

    /**
     * Tests if multiple open clients can work with a server if their commands are interleaved
     */
    @Test
    public void test_interleave_multiple_clients() {
        Client client1 = new Client();
        Client client2 = new Client();
        try {
            client1.startConnection(FREUD, TEST_PORT);
            client2.startConnection(FREUD, TEST_PORT);
            String resp1 = client1.sendMessage("1");
            String resp2 = client2.sendMessage("2");
            String resp1_prime = client1.sendMessage("1 again");
            String resp2_prime = client2.sendMessage("2 again");
            String terminate = client1.sendMessage("quit");
            String terminate2 = client2.sendMessage("quit");
            Assertions.assertEquals("Message received: 1", resp1);
            Assertions.assertEquals("Message received: 2", resp2);
            Assertions.assertEquals("Message received: 1 again", resp1_prime);
            Assertions.assertEquals("Message received: 2 again", resp2_prime);
            Assertions.assertEquals("Exiting", terminate);
            Assertions.assertEquals("Exiting", terminate2);
        } catch (Exception e) {
            System.out.println("Network failed");
        }
    }
}
