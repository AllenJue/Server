/**
 * Author: Allen Jue
 * Date: 12/17/2022
 */

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.IOException;

/**
 * Tests the accuracy of the client-server relationship between multiple clients
 * @throws IOException if IO devices failed
 */
@TestMethodOrder(MethodOrderer.MethodName.class)
public class MultipleClientTests {
    private static final String FREUD = "128.83.120.232";
    private static final int TEST_PORT = 3000;

    private static final String credential1 = "allen jue";
    private static final String credential2 = "jue allen";

    /**
     * Tests multiple clients trying to login to invalid accounts
     * @throws IOException
     */
    @Test
    public void test_1_login_multiple_clients_invalid() throws IOException {
        Client client1 = new Client();
        Client client2 = new Client();
        client1.startConnection(FREUD, TEST_PORT);
        client2.startConnection(FREUD, TEST_PORT);
        boolean client1Success = client1.authenticate(credential1, false);
        boolean client2Success = client2.authenticate(credential2, false);
        Assertions.assertFalse(client1Success);
        Assertions.assertFalse(client2Success);
    }

    /**
     * Test multiple clients simultaneously logging in
     * @throws IOException if IO devices failed
     */
    @Test
    public void test_2_create_multiple_clients_valid() throws IOException {
        Client client1 = new Client();
        Client client2 = new Client();
        client1.startConnection(FREUD, TEST_PORT);
        client2.startConnection(FREUD, TEST_PORT);
        boolean client1Success = client1.authenticate(credential1, true);
        boolean client2Success = client2.authenticate(credential2, true);
        Assertions.assertTrue(client1Success && client2Success);
        String resp1 = client1.sendMessage("Hello");
        String terminate = client1.sendMessage("quit");
        Assertions.assertEquals("Message received: Hello", resp1);
        Assertions.assertEquals("Exiting", terminate);
        String resp2 = client2.sendMessage("Hello from thread 2");
        String terminate2 = client2.sendMessage("quit");
        Assertions.assertEquals("Message received: Hello from thread 2", resp2);
        Assertions.assertEquals("Exiting", terminate2);
    }

    /**
     * Test if multiple clients at once can work commands for each are sequentially adjacent
     * @throws IOException if IO devices failed
     */
    @Test
    public void test_3_login_multiple_valid_clients() throws IOException {
        Client client1 = new Client();
        Client client2 = new Client();
        client1.startConnection(FREUD, TEST_PORT);
        client2.startConnection(FREUD, TEST_PORT);
        boolean client1Success = client1.authenticate(credential1, false);
        boolean client2Success = client2.authenticate(credential2, false);
        Assertions.assertTrue(client1Success && client2Success);
        String resp1 = client1.sendMessage("Hello");
        String terminate = client1.sendMessage("quit");
        Assertions.assertEquals("Message received: Hello", resp1);
        Assertions.assertEquals("Exiting", terminate);
        String resp2 = client2.sendMessage("Hello from thread 2");
        String terminate2 = client2.sendMessage("quit");
        Assertions.assertEquals("Message received: Hello from thread 2", resp2);
        Assertions.assertEquals("Exiting", terminate);
    }

    /**
     * Tests if multiple open clients can work with a server if their commands are interleaved
     * @throws IOException if IO devices failed
     */
    @Test
    public void test_4_interleave_multiple_clients() throws IOException {
        Client client1 = new Client();
        Client client2 = new Client();
        client1.startConnection(FREUD, TEST_PORT);
        client2.startConnection(FREUD, TEST_PORT);
        boolean client1Success = client1.authenticate(credential1, false);
        boolean client2Success = client2.authenticate(credential2, false);
        Assertions.assertTrue(client1Success && client2Success);
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
    }
}
