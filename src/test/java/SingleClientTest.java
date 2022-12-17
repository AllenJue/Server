
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SingleClientTest {
    private static final String FREUD = "128.83.120.232";
    private static final int TEST_PORT = 3000;

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
