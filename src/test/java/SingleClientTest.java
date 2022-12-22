/**
 * Author: Allen Jue
 * Data: 12/17/2022
 */

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * Tests for accuracy within a single client-server relationship.
 * Note that test cases will pass if the network is simply not working.
 */
public class SingleClientTest {
    private static final String FREUD = "128.83.120.232";   /* Default IP Address, lab machine */
    private static final int TEST_PORT = 3000;              /* Arbitrary port used */

    @Test
    public void attempt_to_login_nonexistent_account() throws IOException {
        Client client = new Client();
        String credentials = "Doesn't exist";
        boolean authenticated = client.authenticate(credentials, false);
        Assertions.assertFalse(authenticated);
    }

    @Test
    public void test_create_account() throws IOException {
        Client client = new Client();
        String credentials = "allen jue"; /* username: allen, password: jue */
        boolean authenticated = client.authenticate(credentials, true);
        Assertions.assertTrue(authenticated);
        String resp = client.sendMessage("Hello, world");
        Assertions.assertEquals("Message received: Hello, world", resp);
        String resp1 = client.sendMessage("quit");
        Assertions.assertEquals("Exiting", resp1);
    }

    @Test
    public void test_login_to_created_account() throws IOException {
        Client client = new Client();
        String existingCredentials = "allen jue";
        boolean authenticated = client.authenticate(existingCredentials, false);
        Assertions.assertTrue(authenticated);
        String resp = client.sendMessage("Hello, world");
        Assertions.assertEquals("Message received: Hello, world", resp);
        String resp1 = client.sendMessage("quit");
        Assertions.assertEquals("Exiting", resp);
    }

    @Test
    public void test_attempt_creating_existing_account() throws IOException {
        Client client = new Client();
        String existingCredentials = "allen jue";
        boolean authenticated = client.authenticate(existingCredentials, true);
        Assertions.assertFalse(authenticated);
    }

}
